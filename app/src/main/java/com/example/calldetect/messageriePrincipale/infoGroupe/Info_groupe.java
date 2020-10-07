package com.example.calldetect.messageriePrincipale.infoGroupe;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.models.ModelContacts;

import java.util.ArrayList;
import java.util.List;

public class Info_groupe extends AppCompatActivity {

    private RecyclerView recyclerView;
    private adapterInfoGroupe adapterInfoGroupe;
    private String nomMenbre, numeroMembre;
    private List<ModelContacts> list = new ArrayList<>();
    private Toolbar toolbar;
    private TextView nbreParticipant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_groupe);

        // gestion de la toolbar

        // on s'occupe de la toolbar
        toolbar = findViewById(R.id.Toolbar_page_centrale);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.messageListMenbre);
        //fin

        nomMenbre = getIntent().getStringExtra("nom");
        numeroMembre = getIntent().getStringExtra("numero");

        // on prepare a la liste
        for (int i = 0; i <numeroMembre.split(",").length ; i++) {
            list.add(new ModelContacts(nomMenbre.split(",")[i],numeroMembre.split(",")[i]));
        }

        // initialoisation des elements
        recyclerView = findViewById(R.id.recyclerViewInfoGroup);
        nbreParticipant = findViewById(R.id.nbreMenbre);


        nbreParticipant.setText(getResources().getString(R.string.Nbreparticipant) +" "+ Integer.toString(list.size()));



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterInfoGroupe = new adapterInfoGroupe(this, list );
        adapterInfoGroupe.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterInfoGroupe);


    }
}
