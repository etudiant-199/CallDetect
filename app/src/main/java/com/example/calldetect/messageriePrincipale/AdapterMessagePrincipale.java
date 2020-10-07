package com.example.calldetect.messageriePrincipale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.database.DataBaseManager;
import com.example.calldetect.messageriePrincipale.messageCustomiser.Activity_message_personaliser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessagePrincipale extends RecyclerView.Adapter<AdapterMessagePrincipale.ViewHolder> implements Filterable {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<ModelMessagePrinc> list;
    private List<ModelMessagePrinc> listFiltred;
    private ActivityMessaferiePrincipale activityMessaferiePrincipale;
    private static final int ID_PAGE = 1;


    public  AdapterMessagePrincipale(Context context, List<ModelMessagePrinc> list){

        this.context = context;
        this.list = list;
        this.listFiltred = list;
        activityMessaferiePrincipale = (ActivityMessaferiePrincipale) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_messagerie_principale  , viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view, activityMessaferiePrincipale);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final TextView nom, message, jour, premierLettreNom;
        CircleImageView circleImageView;
        LinearLayout cardView;
        nom = viewHolder.nomVH;
        message = viewHolder.messageVH;
        jour = viewHolder.jourVH;
        premierLettreNom = viewHolder.debutLettreNom;
        circleImageView = viewHolder.image;
        cardView = viewHolder.cardView;

        //si pas vue
        if (listFiltred.get(i).getRead() == 0){
            message.setMaxLines(3);
            message.setTextColor(context.getResources().getColor(R.color.noir));
            message.setTypeface(null, Typeface.BOLD);
            nom.setTypeface(null, Typeface.BOLD);
        }else {
            message.setMaxLines(1);
            message.setTextColor(context.getResources().getColor(R.color.noir));
            message.setTypeface(null, Typeface.NORMAL);
            nom.setTypeface(null, Typeface.NORMAL);
        }

        if (listFiltred.get(i).getNom()==null){
            nom.setText(listFiltred.get(i).getNumero());
        }else {
            nom.setText(listFiltred.get(i).getNom());
        }
        message.setText(listFiltred.get(i).getMessage());
        jour.setText(listFiltred.get(i).getJour());
        premierLettreNom.setText(listFiltred.get(i).getFirsrLetter());
        circleImageView.setImageDrawable(context.getResources().getDrawable(listFiltred.get(i).getImage()));

    }


    @Override
    public int getItemCount() {
        return listFiltred.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults() ;
                if (constraint == null || constraint.length() == 0){
                    filterResults.count = list.size();
                    filterResults.values = list;
                }else {
                    String searchStr = constraint.toString().replace(" ","").toLowerCase();
                    List<ModelMessagePrinc> resultData = new ArrayList<>();
                    for (int i=0; i<list.size(); i++){
                        if (list.get(i).getNom().toLowerCase().replace(" ","").contains(searchStr)){
                            resultData.add(list.get(i));
                        }
                        filterResults.count = resultData.size();
                        filterResults.values = resultData;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listFiltred = (List<ModelMessagePrinc>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }


    public class  ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {
        TextView nomVH, messageVH, jourVH,debutLettreNom;
        CircleImageView image;
        ActivityMessaferiePrincipale activityMessaferiePrincipale;
        LinearLayout cardView;
        public ViewHolder(@NonNull View itemView, ActivityMessaferiePrincipale activityMessaferiePrincipale) {
            super(itemView);
            nomVH = itemView.findViewById(R.id.nomM);
            messageVH = itemView.findViewById(R.id.messageM);
            jourVH = itemView.findViewById(R.id.jourM);
            debutLettreNom = itemView.findViewById(R.id.firtLetter);
            image = itemView.findViewById(R.id.image);
            this.activityMessaferiePrincipale = activityMessaferiePrincipale;
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnLongClickListener(activityMessaferiePrincipale);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // ici c'est pour la selection des messages pour la suppression
            if (activityMessaferiePrincipale.is_multiselection_active) {
                activityMessaferiePrincipale.prepareSelection(getAdapterPosition());
            }

            // ici c'est pour ouvrir la discussion pour un contact precis !
            if (!activityMessaferiePrincipale.is_multiselection_active){
                Intent intent = new Intent(context, Activity_message_personaliser.class);
                intent.putExtra("number", listFiltred.get(getAdapterPosition()).getNumero());
                ((ActivityMessaferiePrincipale) context).startActivityForResult(intent, ID_PAGE);

                DataBaseManager manager = new DataBaseManager(context);
                manager.UpdateMsgLue(listFiltred.get(getAdapterPosition()).getNumero());
                manager.close();


            }
        }

    }


    //on supprime de la grande liste la liste des elements selectionnes
    public void UpdateAdapter(List<ModelMessagePrinc> list_select){
        for (int i=0; i< list_select.size();i++){
            list.remove(list_select.get(i));
        }
        notifyDataSetChanged();
    }



}
