package com.example.calldetect.messageriePrincipale.contact;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.database.DataBaseManager;
import com.example.calldetect.messageriePrincipale.ActivityMessaferiePrincipale;
import com.example.calldetect.messageriePrincipale.ModelMessagePrinc;
import com.example.calldetect.messageriePrincipale.messageCustomiser.Activity_message_personaliser;
import com.example.calldetect.models.ModelContacts;

import java.util.ArrayList;
import java.util.List;

public class Contact_messagerie extends AppCompatActivity {
    private static final int ID_PAGE = 1;
    private RecyclerView recyclerView;
    private List<ModelContacts> list, listContactSelect = new ArrayList<>();
    private AdapterContactMessagerie adapterContactMessagerie;
    private Toolbar toolbar;
    public Boolean is_multiple_select,isCreateGroup = false;
    public int counter = 0;
    public String message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_messagerie);


        is_multiple_select = getIntent().getExtras().getBoolean("valeur");
        message = getIntent().getStringExtra("messageTransfert");
        isCreateGroup = getIntent().getExtras().getBoolean("groupe");

        // initialisation des composants
        toolbar = findViewById(R.id.Toolbar_page_centrale);
        recyclerView = findViewById(R.id.recyclerViewContactMessagerie);


        //modification  de la toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.selectionContact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = getContact();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterContactMessagerie  = new AdapterContactMessagerie(this, list);
        recyclerView.setAdapter(adapterContactMessagerie);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activite_contact_messagerie, menu);
        if (is_multiple_select){

        }
        MenuItem menuItem = menu.findItem(R.id.rechecher);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterContactMessagerie.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.rechecher:

                break;

            case R.id.validationTransfert:
                // si ce n'est pas pour creer un groupe
                if (!isCreateGroup) {
                    long dateee = System.currentTimeMillis();
                    for (int i = 0; i < listContactSelect.size(); i++) {
                        sendMessage(listContactSelect.get(i).getContact(), message, String.valueOf(dateee));
                    }
                    Intent intent = new Intent(Contact_messagerie.this, ActivityMessaferiePrincipale.class);
                    startActivity(intent);
                }else {
                    String listContact=listContactSelect.get(0).getContact();

                    for (int i = 1; i <listContactSelect.size() ; i++) {
                        listContact = listContact+","+listContactSelect.get(i).getContact();

                    }

                    Intent intent = new Intent(Contact_messagerie.this, Activity_message_personaliser.class);
                    intent.putExtra("number",listContact);
                    startActivityForResult(intent, 1);
                    finish();

                }
                break;

            case R.id.creerGroupe:
                finish();
                Intent intent1 = new Intent(Contact_messagerie.this, Contact_messagerie.class);
                intent1.putExtra("valeur", true);
                intent1.putExtra("groupe",true);
                startActivityForResult(intent1, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * cette methode retourne la liste des contacts qui sont dans le repertoire et dans la base de donnee de contact de Calldetect
     * @return
     */
    private List<ModelContacts> getContact(){
        // je recupere les contact sauvegarder dans en local dans calldetect
        DataBaseManager baseManager = new DataBaseManager(this);
        List<ContactPhone> contactPhoneList = baseManager.lectureContact();
        baseManager.close();
        //fin




        List<ModelContacts> list = new ArrayList<>();
        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, ContactsContract.Contacts.DISPLAY_NAME+" ASC");
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            list.add(new ModelContacts(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), false, R.drawable.ic_account));
        }

        //je conactenne les deux listes
        for (int i = 0; i <contactPhoneList.size() ; i++) {
            list.add(new ModelContacts(contactPhoneList.get(i).getNom(), contactPhoneList.get(i).getNumero()));
        }
        //fin

        return list;
    }

    public void ouvertureDiscucssion(){

        Intent intent = new Intent(this, Activity_message_personaliser.class);
        intent.putExtra("number", adapterContactMessagerie.numbers);
        startActivityForResult(intent, ID_PAGE);
        finish();

    }

    public void prepareSelection(int i) {
        if (!list.get(i).getChecked()){

            list.get(i).setChecked(true);
            listContactSelect.add(list.get(i));
            counter++;
            toolbar.getMenu().clear();
            toolbar.setTitle(counter +" "+ getResources().getString(R.string.selection));
            toolbar.inflateMenu(R.menu.menu_active_contact_messagerie_delete);


            list.get(i).setImag(R.mipmap.icon_muutiple_selection);
            adapterContactMessagerie.notifyDataSetChanged();

        }else {

            list.get(i).setChecked(false);
            listContactSelect.remove(list.get(i));
            counter --;
            toolbar.setTitle(counter +" "+ getResources().getString(R.string.selection));


            list.get(i).setImag(R.drawable.ic_account);
            adapterContactMessagerie.notifyDataSetChanged();

        }
    }




    public void sendMessage(String number, String message, final String date) {

        try {
            String SENT = "Message Sent";
            String DELIVERED = "Message Delivered";

            PendingIntent sendPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            PendingIntent deliveredID = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);


            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:


                            DataBaseManager managee = new DataBaseManager(context);
                            managee.UpdateMsgEnvoye(date);
                            managee.close();
                            Toast.makeText(context, "transmission ok 1", Toast.LENGTH_SHORT).show();

                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                            DataBaseManager manager = new DataBaseManager(context);
                            manager.UpdateEchecMsg(date);
                            manager.close();
                            Toast.makeText(context, "erreur ", Toast.LENGTH_SHORT).show();


                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();

                            break;
                    }

                }
            }, new IntentFilter(SENT));


            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:


                            DataBaseManager managere = new DataBaseManager(context);
                            managere.UpdateMsgBienRecue(date);
                            managere.close();
                            Toast.makeText(context, "transmisson ok", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(context, "transmission fail", Toast.LENGTH_SHORT).show();
                            DataBaseManager manager = new DataBaseManager(context);
                            manager.UpdateEchecMsg(date);
                            manager.close();


                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
                            break;
                    }


                }
            }, new IntentFilter(DELIVERED));



            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(number, null, message, sendPI, deliveredID);


            ModelMessagePrinc messagePrinc = new ModelMessagePrinc(number, message, date, 1, "", 0, 0, 0, 0);

            //on sauvegarde le message.
            DataBaseManager manager = new DataBaseManager(this);
            manager.insertMessage(messagePrinc);
            manager.close();


        }catch (Exception e){
            Toast.makeText(this, "une erreur est survenue lors de l'envoie du message", Toast.LENGTH_SHORT).show();
        }

    }


}
