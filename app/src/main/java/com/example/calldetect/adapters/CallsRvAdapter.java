package com.example.calldetect.adapters;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.calldetect.R;
import com.example.calldetect.models.ModelCalls;
import com.example.calldetect.utils.Utils;

import java.util.List;

public class CallsRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_CONTACT = 0;
    private static final int ITEM_DATE_GROUP = 1;
    private Context mContext;
    private List<ModelCalls> mListCalls;
    private OnItemActionClickListener mListener;
    private OnItemLongClickListener mListenerLong;
    private SparseIntArray drawablesIndicator = new SparseIntArray();
    public interface OnItemActionClickListener {
        void onItemClick(int position);
        void onMessageClick(int position);
        void onCallClick(int position);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
    public void setOnItemLongClickListener (OnItemLongClickListener listenerLong) {
        mListenerLong = listenerLong;
    }
    public void setOnItemActionClickListener (OnItemActionClickListener listener) {
        mListener = listener;
    }
    public  CallsRvAdapter(Context context, List<ModelCalls> listCalls){
       mContext = context;
       mListCalls = listCalls;
       drawablesIndicator.put(Utils.CallLogType.OUTGOING, R.drawable.ic_call_made);
       drawablesIndicator.put(Utils.CallLogType.MISSED, R.drawable.ic_call_missed);
       drawablesIndicator.put(Utils.CallLogType.INCOMING, R.drawable.ic_call_received);
       drawablesIndicator.put(Utils.CallLogType.REJECTED, R.drawable.ic_call_missed_outgoing);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==ITEM_CONTACT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_calls, parent, false);
            return new ContactItemHolder(view, mListener, mListenerLong);
        }else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_date_group, parent, false);
            return new DateGroupItemHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {
        ModelCalls contact = mListCalls.get(position);
        if (hold instanceof ContactItemHolder) {
            ContactItemHolder holder = (ContactItemHolder) hold;
            TextDrawable drawable;
            ColorGenerator generator = ColorGenerator.MATERIAL;
            if (contact.getName().length() > 2) {
                String startName = contact.getName().substring(0, 2);
                drawable = TextDrawable.builder().buildRound(startName, generator.getRandomColor());
            }else{
                drawable = TextDrawable.builder().buildRound("#", generator.getRandomColor());
            }
            int i = contact.getInteractions();
            String interaction = i == 1 ? "" : " (" + contact.getInteractions() + ")";
            String nameWithCallNumber = contact.getName() + interaction;
            String callDuration = " (" + Utils.SecondsToTime(contact.getDuration()) + ")";
            String detail = contact.getDate() +" "+ mContext.getResources().getString(R.string.at)
                    +" "+ contact.getTime() + callDuration;
            holder.imageView3.setImageDrawable(drawable);
            holder.name.setText(nameWithCallNumber);
            holder.date.setText(detail);
            holder.date.setCompoundDrawablesWithIntrinsicBounds(drawablesIndicator
                    .get(contact.getType()), 0, 0, 0);
        } else {
            DateGroupItemHolder holder = (DateGroupItemHolder) hold;
            holder.date_group.setText(contact.getDate());
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (mListCalls.get(position).getType() == -1)
            return ITEM_DATE_GROUP;
        else
            return ITEM_CONTACT;
    }
    @Override
    public int getItemCount() {
        return mListCalls.size();
    }
    public static class DateGroupItemHolder extends RecyclerView.ViewHolder {
        TextView date_group;
        DateGroupItemHolder (@NonNull View itemView) {
            super(itemView);
            date_group = itemView.findViewById(R.id.date_group);
        }
    }
    public static class ContactItemHolder extends RecyclerView.ViewHolder{
        ImageView imageView3;
        TextView name, date;
        ImageButton callBtn, messageBtn;
        ContactItemHolder(@NonNull View itemView, final OnItemActionClickListener listener,
                          final OnItemLongClickListener listenerLong) {
            super(itemView);
            imageView3 = itemView.findViewById(R.id.imageView3);
            name = itemView.findViewById(R.id.call_name);
            date = itemView.findViewById(R.id.call_date);
            callBtn = itemView.findViewById(R.id.call_btn);
            messageBtn = itemView.findViewById(R.id.message_btn);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listenerLong.onItemLongClick(position);
                    }
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCallClick(position);
                    }
                }
            });
            messageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMessageClick(position);
                    }
                }
            });
        }
    }
}
