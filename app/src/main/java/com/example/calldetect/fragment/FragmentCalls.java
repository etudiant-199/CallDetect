package com.example.calldetect.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.activities.CallInfoActivity;
import com.example.calldetect.adapters.CallsRvAdapter;
import com.example.calldetect.messageriePrincipale.messageCustomiser.Activity_message_personaliser;
import com.example.calldetect.models.ModelCalls;
import com.example.calldetect.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentCalls extends Fragment {
    private static final String TAG = FragmentCalls.class.getName();
    private List<List<com.example.calldetect.models.CallLog>> callLogs = new ArrayList<>();
    private List<ModelCalls> modelCalls = new ArrayList<>();
    private String oldDate = "";
    private CallsRvAdapter adapter;
    private ModelCalls oldCall = new ModelCalls();
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_calls, container, false);
        adapter = new CallsRvAdapter(getContext(), modelCalls);
        oldDate = "";
        progressBar = v.findViewById(R.id.progressBar);
        final RecyclerView recyclerView = v.findViewById(R.id.rv_call);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemActionClickListener(new CallsRvAdapter.OnItemActionClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intentDetail = new Intent(requireContext(), CallInfoActivity.class);
                intentDetail.putExtra("Contact", modelCalls.get(position));
                intentDetail.putExtra("CallLogs", (ArrayList<? extends Parcelable>) callLogs.get(position));
                startActivity(intentDetail);
            }

            @Override
            public void onMessageClick(int position) {
                ModelCalls calls = modelCalls.get(position);
                Intent intent = new Intent(requireContext(), Activity_message_personaliser.class);
                intent.putExtra("number", calls.getNumber());
                requireActivity().startActivity(intent);
            }

            @Override
            public void onCallClick(int position) {
                Utils.call(requireActivity(), modelCalls.get(position).getNumber());
            }
        });
        adapter.setOnItemLongClickListener(new CallsRvAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position) {
                final String phoneNumber = modelCalls.get(position).getNumber();
                String[] items ;
                if (Utils.numberExist(requireActivity(), phoneNumber))
                    items = getResources().getStringArray(R.array.contactOptions1);
                else
                    items = getResources().getStringArray(R.array.contactOptions2);

                new MaterialAlertDialogBuilder(requireContext())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Utils.addToFavorite(phoneNumber, requireActivity());
                                        break;
                                    case 1:
                                        Utils.blockNumber(requireActivity(), phoneNumber);
                                        break;
                                    case 2:
                                        if (Utils.deleteCallLog(phoneNumber, requireContext())){
                                            modelCalls.remove(modelCalls.get(position));
                                            adapter.notifyItemRemoved(position);
                                        }else
                                            Utils.setToastMessage(requireContext(),
                                                    getString(R.string.error_has_provide));
                                        break;
                                    case 3:
                                        Utils.editContact(requireActivity(), phoneNumber);
                                        break;
                                    case 4:
                                        Utils.signalNumber(phoneNumber, requireActivity());
                                        break;
                                    case 5:
                                        Utils.addContact(requireActivity(), phoneNumber);
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setCallLog();
    }

    /**
     * Set the list of the call logs.
     */
    public void setCallLog() {
        new CallLogAsync(requireContext()).execute();
    }

    private void addCallLogToList(com.example.calldetect.models.CallLog callLog, boolean isNew) {
        if (isNew) {
            callLogs.get(callLogs.size() - 1).add(callLog);
        } else {
            List<com.example.calldetect.models.CallLog> callLogList = new ArrayList<>();
            callLogList.add(callLog);
            callLogs.add(callLogList);
        }
    }

    /**
     * Function to check if the call exist in the contact list.
     */
    private boolean isTheSame(ModelCalls c1, ModelCalls c2) {
        return ((c2.getNumber().contains(c1.getNumber()) || c1.getNumber().contains(c2.getNumber()))
                && c2.getDate().equals(c1.getDate())
                && c2.getName().equals(c1.getName())
                && c2.getType() == c1.getType());
    }

    @SuppressLint("StaticFieldLeak")
    private class CallLogAsync extends AsyncTask<Void, Integer, List<ModelCalls>> {
        private Context context;
        CallLogAsync (Context context) {
            this.context = context;
        }

        @Override
        protected List<ModelCalls> doInBackground(Void... voids) {
            oldDate = "";
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, null, null, CallLog.Calls.DATE + " DESC");
            assert cursor != null;
            int number_index = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int duration_index = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int date_index = cursor.getColumnIndex(CallLog.Calls.DATE);
            int type_index = cursor.getColumnIndex(CallLog.Calls.TYPE);
            if (cursor.getCount() == 0){
                return null;
            }
            while (cursor.moveToNext()){
                Date dateRef = new Date(Long.parseLong(cursor.getString(date_index)));
                String callDate = Utils.getDateAlias(dateRef, context);
                String callTime = Utils.getTimeAlias(dateRef);
                String callDuration = cursor.getString(duration_index);
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                int recorder = Integer.parseInt(cursor.getString(type_index));
                String phoneNumber = cursor.getString(number_index);
                if (name == null)
                    name = phoneNumber;
                int callType;
                switch (recorder){
                    case CallLog.Calls.OUTGOING_TYPE:
                        callType = Utils.CallLogType.OUTGOING;
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        callType=Utils.CallLogType.INCOMING;
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callType = Utils.CallLogType.MISSED;
                        break;
                    default:
                        callType = Utils.CallLogType.REJECTED;
                        break;
                }
                if (!oldDate.equals(callDate)){
                    callLogs.add(new ArrayList<com.example.calldetect.models.CallLog>());
                    modelCalls.add(new ModelCalls("", "", -1, callDate, callTime, -1));
                    oldDate = callDate;
                }
                ModelCalls calls = new ModelCalls(name, phoneNumber, Integer.parseInt(callDuration),
                        callDate, callTime, callType);
                com.example.calldetect.models.CallLog callLog = new com.example.calldetect.models.CallLog(
                        phoneNumber, callDate, callTime, Integer.parseInt(callDuration), callType
                );
                if (isTheSame(oldCall, calls)){
                    calls.setInteractions(oldCall.getInteractions() + 1);
                    modelCalls.remove(modelCalls.size()-1);
                    modelCalls.add(calls);
                    addCallLogToList(callLog, true);
                }else{
                    modelCalls.add(calls);
                    addCallLogToList(callLog, false);
                }
                oldCall = calls;
            }
            cursor.close();
            return modelCalls;
        }

        @Override
        protected void onPreExecute() {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            modelCalls.clear();
            callLogs.clear();
        }

        @Override
        protected void onPostExecute(List<ModelCalls> modelCalls) {
            Log.i(TAG, "onPostExecute: The result is set.");
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }
}

