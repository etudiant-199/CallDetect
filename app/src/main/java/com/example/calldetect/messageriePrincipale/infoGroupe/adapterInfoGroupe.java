package com.example.calldetect.messageriePrincipale.infoGroupe;

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

public class adapterInfoGroupe extends RecyclerView.Adapter<adapterInfoGroupe.Viewholder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<ModelContacts> list;


    public  adapterInfoGroupe(Context context, List<ModelContacts> list){
       this.list = list;
       this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_info_group, viewGroup, false);
        Viewholder viewHolder = new Viewholder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder viewholder, int i) {

        TextView nom, numero;
        nom = viewholder.nom;
        numero = viewholder.numero;

        nom.setText(list.get(i).getName());
        numero.setText(list.get(i).getContact());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {

        TextView nom, numero;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            nom = itemView.findViewById(R.id.nomPersonne);
            numero = itemView.findViewById(R.id.numeroPersonne);


        }
    }
}
