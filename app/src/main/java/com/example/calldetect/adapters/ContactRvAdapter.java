package com.example.calldetect.adapters;

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
import com.example.calldetect.models.ModelContacts;

import java.util.List;

public class ContactRvAdapter extends RecyclerView.Adapter<ContactRvAdapter.viewHolder>{

    private List<ModelContacts> contact;
    private List<Boolean> viewPosition;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mListenerLong;

    public ContactRvAdapter(List<Boolean> viewPosition, List<ModelContacts> contact) {
        this.viewPosition = viewPosition;
        this.contact = contact;
    }

    public interface OnItemClickListener {
        void damaskElement(int position);
        void sendCall(int position);
        void sendMessage(int position);
        void contactDetail(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener  = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listenerLong){
        mListenerLong = listenerLong;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts,parent,false);

        return new viewHolder(v, mListener,mListenerLong);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        ModelContacts user = contact.get(position);
        holder.phone_name.setText(user.getName());
        holder.phone_number.setText(user.getNumber());

        //SET AVATAR
        String startName;
        if (user.getName().length() < 2)
            startName = String.valueOf(user.getName().charAt(0));
        else
            startName = user.getName().substring(0, 2);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable drawable= TextDrawable.builder().buildRound(startName,generator.getRandomColor());
        holder.image.setImageDrawable(drawable);

        if (viewPosition.get(position)) {
            holder.phone_call.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.VISIBLE);
            holder.details.setVisibility(View.VISIBLE);
        } else {
            holder.phone_call.setVisibility(View.GONE);
            holder.message.setVisibility(View.GONE);
            holder.details.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contact.size();
    }

    static class viewHolder  extends RecyclerView.ViewHolder{
        TextView phone_name, phone_number;
        ImageView image;
        ImageButton phone_call, message, details;

        viewHolder(@NonNull final View itemView, final OnItemClickListener listener,
                   final OnItemLongClickListener listenerLong) {
            super(itemView);
            phone_name=itemView.findViewById(R.id.phone_name);
            phone_number=itemView.findViewById(R.id.phone_number);
            image=itemView.findViewById(R.id.request_image);
            phone_call =itemView.findViewById(R.id.phone_call);
            details =itemView.findViewById(R.id.details);
            message=itemView.findViewById(R.id.message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.damaskElement(position);


                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listenerLong.onItemLongClick(position);

                    return true;
                }
            });
            phone_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.sendCall(position);
                }
            });
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.sendMessage(position);
                }
            });
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.contactDetail(position);
                }
            });
        }
    }
}
