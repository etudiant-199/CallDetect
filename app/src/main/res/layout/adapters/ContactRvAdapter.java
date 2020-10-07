package com.example.calldetect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.models.ModelContacts;

import java.util.List;

public class ContactRvAdapter extends RecyclerView.Adapter<ContactRvAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Context mcontext;
    private List<ModelContacts>  mlistContact;


    public ContactRvAdapter(Context context, List<ModelContacts> listContact){
        mcontext = context;
        mlistContact = listContact;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate(R.layout.item_contacts, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView contact_name, contact_number;
        contact_name = holder.contact_name;
        contact_number = holder.contact_number;

        contact_name.setText(mlistContact.get(position).getName());
        contact_number.setText(mlistContact.get(position).getContact());
    }

    @Override
    public int getItemCount() {
        return mlistContact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView contact_name, contact_number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_number = itemView.findViewById((R.id.contact_number));
        }
    }


}
