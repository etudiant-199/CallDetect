package com.example.calldetect.messageriePrincipale.ContactBlocquer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;

import java.util.List;

public class AdapterContactLock  extends RecyclerView.Adapter<AdapterContactLock.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<ContactLock> list;
    private Activity_contatct_Blocque activity_contatct_blocque;

    public AdapterContactLock(Context context, List<ContactLock> list) {
        this.context = context;
        this.list = list;
        activity_contatct_blocque = (Activity_contatct_Blocque) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_contact_bocquer, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        TextView nom, numero;
        ImageButton deverouiller;

        nom = viewHolder.nom;
        numero = viewHolder.numero;
        deverouiller = viewHolder.deverouiller;

        nom.setText(list.get(i).getNom());
        numero.setText(list.get(i).getNumero());



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nom, numero;
        ImageButton deverouiller;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.nomContact);
            numero = itemView.findViewById(R.id.numeroTel);
            deverouiller = itemView.findViewById(R.id.deverouiller);

            deverouiller.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            activity_contatct_blocque.deveroullage(getAdapterPosition());
        }
    }




}
