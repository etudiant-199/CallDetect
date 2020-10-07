package com.example.calldetect.adapters;

import android.text.Html;
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

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

public class ComposerAdapter extends RecyclerView.Adapter<ComposerAdapter.SearchHolder> {

    private OnItemClickListener listener;
    private OnItemLongClickListener listenerLong;
    private List<ModelContacts> contacts;

    public ComposerAdapter(List<ModelContacts> contacts){
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_contact,
                        parent, false), listener, listenerLong
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        ModelContacts contact = contacts.get(position);

        String name = contact.getName().replace("<font color='#42A5F5'><b>", "");
        name = name.replace("</b></font>", "");
        String startName;
        if (name.length() < 2)
            startName = String.valueOf(name.charAt(0));
        else
            startName = name.substring(0, 2);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable drawable= TextDrawable.builder().buildRound(startName,generator.getRandomColor());

        holder.avatar.setImageDrawable(drawable);
        holder.phone_number.setText(Html.fromHtml(contact.getNumber()));
        holder.user_name.setText(Html.fromHtml(contact.getName()));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemCall(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        listener = mListener;
    }

    public void setOnItemLongClickListener (OnItemLongClickListener mListener) {
        listenerLong = mListener;
    }

    static class SearchHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView user_name, phone_number;
        ImageButton call_btn;

        SearchHolder (View itemView, final OnItemClickListener listener,
                      final OnItemLongClickListener listenerLong) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            user_name = itemView.findViewById(R.id.user_name);
            phone_number = itemView.findViewById(R.id.user_phone_number);
            call_btn = itemView.findViewById(R.id.call_btn);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != NO_POSITION)
                        listener.onItemClick(position);
                }
            });
            call_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != NO_POSITION)
                        listener.onItemCall(position);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != NO_POSITION)
                        listenerLong.onItemLongClick(position);
                    return true;
                }
            });
        }
    }
}
