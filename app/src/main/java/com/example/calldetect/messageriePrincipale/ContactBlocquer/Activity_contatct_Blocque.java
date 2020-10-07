package com.example.calldetect.messageriePrincipale.ContactBlocquer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.database.DataBaseManager;

import java.util.List;

public class Activity_contatct_Blocque extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterContactLock adapterContactLock;
    private List<ContactLock> list;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatct__blocque);

        recyclerView = findViewById(R.id.recyclerView);

        //on agit sur la toolbar
        toolbar = findViewById(R.id.Toolbar_page_centrale);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.contactBlocque);

        //connection a la db
        DataBaseManager manager = new DataBaseManager(this);
        list = manager.listContactBlocquer();
        Toast.makeText(this, Integer.toString(list.size()), Toast.LENGTH_SHORT).show();
        manager.close();
        //fin


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterContactLock = new AdapterContactLock(this, list );
        adapterContactLock.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterContactLock);

    }

    public void deveroullage(int i) {
        DataBaseManager manager = new DataBaseManager(this);
        manager.suppresionContactBlocquer(list.get(i).getNumero());
        list = manager.listContactBlocquer();
        manager.close();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterContactLock = new AdapterContactLock(this, list );
        adapterContactLock.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterContactLock);
    }
}
