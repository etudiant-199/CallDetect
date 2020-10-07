package com.example.calldetect.messageriePrincipale.Sim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.calldetect.R;

import java.util.ArrayList;

public class AdapterSim extends ArrayAdapter<Sim_class> {



    public AdapterSim (Context context, ArrayList<Sim_class> list){
        super(context, 0, list);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView (int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_sim, parent, false
            );
        }

        // on initialise nos element de l'item
        ImageView imageViewSim = convertView.findViewById(R.id.imageSim);
        TextView nomSim = convertView.findViewById(R.id.nom_sim);
        //fin

        // creation de l'objet sim
        Sim_class sim_class = getItem(position);

        if ( sim_class != null) {
            imageViewSim.setImageResource(sim_class.getImageSim());
            nomSim.setText(sim_class.getNomSim());
        }

        return convertView;
    }
}
