package com.example.calldetect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.models.ModelCalls;

import java.util.List;

public class CallsRvAdapter extends RecyclerView.Adapter<CallsRvAdapter.ContactItemHolder> {

    private LayoutInflater layoutInflater;
    private Context mcontext;
    private List<ModelCalls> mlistCalls;

    public  CallsRvAdapter(Context context, List<ModelCalls> listCalls){
       mcontext = context;
       mlistCalls = listCalls;

    }
    @NonNull
    @Override
    public CallsRvAdapter.ContactItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(mcontext);
        View view = layoutInflater.inflate(R.layout.item_calls, parent, false);
        CallsRvAdapter.ContactItemHolder viewHolder = new CallsRvAdapter.ContactItemHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CallsRvAdapter.ContactItemHolder holder, int position) {
        TextView name, duration, date;
        name = holder.name;
        duration = holder.duration;
        date = holder.date;

        name.setText(mlistCalls.get(position).getNumber());
        duration.setText(mlistCalls.get(position).getDuration());
        date.setText(mlistCalls.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mlistCalls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, duration, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.call_name);
            duration = itemView.findViewById(R.id.call_duration);
            date = itemView.findViewById(R.id.call_date);

        }
    }
}
