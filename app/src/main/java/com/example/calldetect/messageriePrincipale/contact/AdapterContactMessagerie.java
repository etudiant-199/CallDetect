package com.example.calldetect.messageriePrincipale.contact;

import android.content.Context;
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
import com.example.calldetect.models.ModelContacts;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterContactMessagerie extends RecyclerView.Adapter<AdapterContactMessagerie.ViewHolder> implements Filterable {
    private Context context;
    private List<ModelContacts> list, list_Filtred;
    private LayoutInflater layoutInflater;
    private Contact_messagerie activity ;
    public String name, numbers;
    public CircleImageView circleImageViewNormal ;




    public AdapterContactMessagerie(Context context, List<ModelContacts> list) {
        this.context = context;
        this.list = list;
        activity = (Contact_messagerie) context;
        this.list_Filtred = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_contacts_os  , viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view, activity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final TextView nom , numero;
        LinearLayout card;


        nom = viewHolder.nom;
        numero = viewHolder.numero;
        card = viewHolder.card;
        circleImageViewNormal = viewHolder.circleImageViewNormal;


        nom.setText(list_Filtred.get(i).getName());
        numero.setText(list_Filtred.get(i).getContact());

        circleImageViewNormal.setImageDrawable(context.getResources().getDrawable(list.get(i).getImag()));

        if (!activity.is_multiple_select) {

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name = nom.getText().toString();
                    numbers = numero.getText().toString();
                    activity.ouvertureDiscucssion();
                }
            });

        }else {

         }




    }

    @Override
    public int getItemCount() {
        return list_Filtred.size();
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
                    String searchStr = constraint.toString().toLowerCase();
                    List<ModelContacts> resultData = new ArrayList<>();
                    for (int i=0; i<list.size(); i++){
                        if (list.get(i).getName().toLowerCase().contains(searchStr) || list.get(i).getContact().toLowerCase().contains(searchStr)){
                            resultData.add(list.get(i));
                        }
                        filterResults.count = resultData.size();
                        filterResults.values = resultData;
                    }
                    if (resultData.size()==0){
                        resultData.add(new ModelContacts("envoyer à ", searchStr));
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list_Filtred =  (List<ModelContacts>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nom, numero;
        CircleImageView circleImageViewNormal ;
        LinearLayout card ;
        Contact_messagerie contact_messagerie;
        public ViewHolder(@NonNull View itemView, Contact_messagerie contact_messagerie) {
            super(itemView);
            nom = itemView.findViewById(R.id.contact_name);
            numero = itemView.findViewById(R.id.contact_number);
            card = itemView.findViewById(R.id.cardViewContact);
            circleImageViewNormal = itemView.findViewById(R.id.imgPresentation);

            this.contact_messagerie = contact_messagerie;
            card.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (contact_messagerie.is_multiple_select){
                contact_messagerie.prepareSelection(getAdapterPosition());
            }
        }
    }
}
