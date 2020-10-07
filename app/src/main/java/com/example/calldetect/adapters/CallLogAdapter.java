package com.example.calldetect.adapters;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.models.CallLog;
import com.example.calldetect.utils.Utils;

import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallHolder> {
    private SparseIntArray drawablesIndicator = new SparseIntArray();
    private List<CallLog> logs;
    public CallLogAdapter (List<CallLog> logs) {
        this.logs = logs;
        drawablesIndicator.put(Utils.CallLogType.OUTGOING, R.drawable.ic_call_made);
        drawablesIndicator.put(Utils.CallLogType.MISSED, R.drawable.ic_call_missed);
        drawablesIndicator.put(Utils.CallLogType.INCOMING, R.drawable.ic_call_received);
        drawablesIndicator.put(Utils.CallLogType.REJECTED, R.drawable.ic_call_missed_outgoing);
    }
    @NonNull
    @Override
    public CallHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_log,
                parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull CallHolder holder, int position) {
        CallLog callLog = logs.get(position);
        holder.time.setText(callLog.getTime());
        String callDuration = " (" + Utils.SecondsToTime(callLog.getDuration()) + ")";
        String detail = callLog.getNumber() + " " + callDuration;
        holder.call_duration.setText(detail);
        holder.call_duration.setCompoundDrawablesWithIntrinsicBounds(drawablesIndicator
                .get(callLog.getType()), 0, 0, 0);

    }
    @Override
    public int getItemCount() {
        return logs.size();
    }
    static class CallHolder extends RecyclerView.ViewHolder {
        TextView time, call_duration;
        CallHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            call_duration = itemView.findViewById(R.id.call_duration);
        }
    }
}
