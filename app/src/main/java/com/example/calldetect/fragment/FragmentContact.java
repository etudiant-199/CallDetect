package com.example.calldetect.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.activities.ContactInfoActivity;
import com.example.calldetect.adapters.ContactRvAdapter;
import com.example.calldetect.messageriePrincipale.messageCustomiser.Activity_message_personaliser;
import com.example.calldetect.models.ModelContacts;
import com.example.calldetect.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class FragmentContact extends Fragment{
    private static final String TAG = "FragmentContact";
    private List<ModelContacts> contactsList = new ArrayList<>();
    private List<Boolean> booleans = new ArrayList<>();
    private int oldPosition = -1;
    private ContactRvAdapter adapter;
    private ModelContacts oldContact;
    private ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        oldContact = new ModelContacts();
        View v = inflater.inflate(R.layout.frag_contacts, container, false);
        progressBar = v.findViewById(R.id.progressBar);
        adapter = new ContactRvAdapter(booleans, contactsList);
        RecyclerView recyclerView = v.findViewById(R.id.rv_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ContactRvAdapter.OnItemClickListener() {
            @Override
            public void damaskElement(int position) {
                if (oldPosition != position){
                   changePosition(position, true);
                   changePosition(oldPosition, false);
                   oldPosition = position;
                }else {
                    changePosition(position, false);
                    oldPosition = -1;
                }
            }
            @Override
            public void sendCall(int position) {
                Utils.call(requireContext(), contactsList.get(position).getNumber());
            }

            @Override
            public void sendMessage(int position) {
                ModelContacts calls = contactsList.get(position);
                Intent intent = new Intent(requireContext(), Activity_message_personaliser.class);
                intent.putExtra("number", calls.getNumber());
                requireActivity().startActivity(intent);

            }

            @Override
            public void contactDetail(int position) {
                Intent intentDetail = new Intent(requireContext(), ContactInfoActivity.class);
                intentDetail.putExtra("Contact", contactsList.get(position));
                startActivity(intentDetail);
            }
        });
        adapter.setOnItemLongClickListener(new ContactRvAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position) {
                final String phoneNumber = contactsList.get(position).getNumber();
                new MaterialAlertDialogBuilder(requireContext())
                        .setItems(R.array.contactOptions1, new DialogInterface.OnClickListener() {
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
                                        if (Utils.deletedContact(requireActivity(), phoneNumber)){
                                            contactsList.remove(contactsList.get(position));
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
                                }
                            }
                        })
                        .show();
            }
        });
        setContact();
        return v;
    }

    private void changePosition(int position, boolean b) {
        if (position <= -1)
            return;
        booleans.remove(position);
        booleans.add(position, b);
        adapter.notifyItemChanged(position);
    }
    public void setBooleans() {
        for (ModelContacts ignored : contactsList)
            booleans.add(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setContact();
    }

    /**
     * Functiont to set the list of the contacts.
     */
    public void setContact(){
        new ContactAsync().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class ContactAsync extends AsyncTask<Void, Integer, List<ModelContacts>> {

        @Override
        protected List<ModelContacts> doInBackground(Void... voids) {
            Cursor cursor =  requireActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone
                            .CONTENT_URI, null,null, null,
                    ContactsContract.Contacts.DISPLAY_NAME+" ASC");
            assert cursor != null;
            int name_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int number_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int image_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
            if (cursor.getCount() <= 0)
                return null;
            while (cursor.moveToNext()){
                String name = cursor.getString(name_index);
                String phoneNumber = cursor.getString(number_index);
                String image = cursor.getString(image_index);
                ModelContacts contactModel = new ModelContacts(name, image, phoneNumber);
                if (!isTheSame(oldContact, contactModel)) {
                    contactsList.add(contactModel);
                    booleans.add(false);
                }
                oldContact = contactModel;
            }
            cursor.close();
            return contactsList;
        }

        @Override
        protected void onPreExecute() {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            contactsList.clear();
            booleans.clear();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(List<ModelContacts> modelContacts) {
            Log.i(TAG, "List of contact is set.");
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private boolean isTheSame(ModelContacts c1, ModelContacts c2) {
        String num1 = c1.getNumber().replace(" ", "");
        String num2 = c2.getNumber().replace(" ", "");
        return num1.contains(num2) || num2.contains(num1) && c1.getName().equals(c2.getName());
    }
}
