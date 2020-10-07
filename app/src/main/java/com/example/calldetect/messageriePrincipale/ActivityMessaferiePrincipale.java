package com.example.calldetect.messageriePrincipale;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.database.DataBaseManager;
import com.example.calldetect.messageriePrincipale.ContactBlocquer.Activity_contatct_Blocque;
import com.example.calldetect.messageriePrincipale.contact.ContactPhone;
import com.example.calldetect.messageriePrincipale.contact.Contact_messagerie;
import com.example.calldetect.models.ModelContacts;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ActivityMessaferiePrincipale extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {
    private static final int ID_PAGE = 1 ;
    public boolean is_multiselection_active = false;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private AdapterMessagePrincipale adapterMessagePrincipale;
    public List<ModelMessagePrinc>Messaeg_select = new ArrayList<>();
    private List<ModelMessagePrinc> Messg_generale = new ArrayList<>();
    private int counter= 0;
    private ImageButton newConversation;
    private List<ModelContacts> listContact;
    private FirebaseFirestore objectFirebase;
    private DocumentReference objetDocumentReference;

    private int[] image ={R.drawable.back1, R.drawable.back2, R.drawable.back3, R.drawable.back4, R.drawable.back5
    , R.drawable.back6, R.drawable.back7, R.mipmap.back8, R.mipmap.back9 };

    public static final String SMS_RECEIVE = "android.provider.Telephony.SMS_RECEIVED";
    private IntentFilter intentFilter;
    private List<String> listAdress;




    // on declare le broadcastReceiver
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // on initialise la list et on reappel de receiver
            listContact = getContact();
            Messg_generale = getMessage();


            recyclerView = findViewById(R.id.recycleMessage);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivityMessaferiePrincipale.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapterMessagePrincipale = new AdapterMessagePrincipale(ActivityMessaferiePrincipale.this, Messg_generale);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapterMessagePrincipale);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaferie_principale);




        

        //on demande la permission de pou
        if (ActivityCompat.checkSelfPermission(ActivityMessaferiePrincipale.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ActivityMessaferiePrincipale.this,new String[]{Manifest.permission.RECEIVE_SMS}, PackageManager.PERMISSION_GRANTED);
        }



        


        //on declare l'intent filter
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        //fin



        newConversation = findViewById(R.id.new_discusion);
        newConversation.setOnClickListener(this);


        // on s'occupe de la toolbar
        toolbar = findViewById(R.id.Toolbar_page_centrale);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.message);
        //fin


        // initialisation des listes
        listContact = getContact();
        Messg_generale = getMessage();


        recyclerView = findViewById(R.id.recycleMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterMessagePrincipale = new AdapterMessagePrincipale(this, Messg_generale);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMessagePrincipale);
        // fin recycerView



        // declaration de lapplication comme application par defaut
        final String myPackageName = getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
                final AlertDialog.Builder mypopup = new AlertDialog.Builder(this);
                mypopup.setTitle(R.string.applicationDefaut);
                mypopup.setMessage(R.string.msgApplicationDefault);
                mypopup.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                        startActivity(intent);

                    }
                });
                mypopup.setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                mypopup.show();
            }else {

            }
        }
        //fin gestion definition de l'application par defaut


    }


    // on declanche le broa
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_messagerie_principale, menu);
        MenuItem menuItem = menu.findItem(R.id.rechecher);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterMessagePrincipale.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_selection:

                DataBaseManager manager = new DataBaseManager(this);
                manager.suppressionDiscussion(Messaeg_select);
                manager.close();

                adapterMessagePrincipale.UpdateAdapter(Messaeg_select);
                clearActionMode();
                adapterMessagePrincipale.notifyDataSetChanged();
                Log.d("oswald", String.valueOf(Messaeg_select.size()));
                break;
            case android.R.id.home:
                clearActionMode();
                deselection(Messaeg_select);
                adapterMessagePrincipale.notifyDataSetChanged();
                break;
            case R.id.rechecher:

                break;
            case R.id.contact_blocquer:
                Intent intent = new Intent(this, Activity_contatct_Blocque.class);
                startActivity(intent);
                break;
            case R.id.marquer_lue:
                DataBaseManager manager1 = new DataBaseManager(this);
                manager1.UpdateMsgLue();
                manager1.close();

                // initialisation des listes
                listContact = getContact();
                Messg_generale = getMessage();

                recyclerView = findViewById(R.id.recycleMessage);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapterMessagePrincipale = new AdapterMessagePrincipale(this, Messg_generale);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterMessagePrincipale);
                // fin recycerView

                break;

        }
        return super.onOptionsItemSelected(item);
    }





    /**
     * cette methode me permet de savoir si un numero est contenue dans  une liste
     * @param listContact
     * @param numero
     * @return
     */
    public Boolean containElement(List<String> listContact , String numero){
        for (int i=0; i<listContact.size();i++){

            //  si c'est un groupe
            if (listContact.get(i).split(",").length>=2){

                if (listContact.get(i).replace(" ","").equals(numero.replace(" ",""))){
                    return true;
                }

            }else {

                if (numero.contains("+")) {
                    if (numero.replace(" ", "").equals(listContact.get(i).replace(" ", "")) | numero.replace(" ", "").contains(listContact.get(i).replace(" ", ""))) {
                        return true;
                    }
                } else {
                    if (numero.replace(" ", "").equals(listContact.get(i).replace(" ", "")) | listContact.get(i).replace(" ", "").contains(numero.replace(" ", ""))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * cette methode me retourne la liste des derniers message de chaque contact dans l'orde !
     * @return
     */
    public List<ModelMessagePrinc> getMessage(){
        List<ModelMessagePrinc> list = new ArrayList<>();
        listAdress = new ArrayList<>();
        List<ModelMessagePrinc> listDernierMessage = new ArrayList<>();
        List<ModelMessagePrinc> listDernierMessageOfficiel = new ArrayList<>();

        List<ModelMessagePrinc> listBd;

        // on initialise la liste initiale
        DataBaseManager manager = new DataBaseManager(this);
        listBd = manager.lectureMessage();
        manager.close();


        // ici on ressort la liste des contact figurant dans notre bd ayant effectuer un dialpoque(conversation quelconque)
        for (int i = 0; i<listBd.size(); i++){
            if(containElement(listAdress,listBd.get(i).getNumero())){

            }else {
                listAdress.add(listBd.get(i).getNumero());
            }

            // ici on ajoute tous les message dans la liste list
            list.add(new ModelMessagePrinc(listBd.get(i).getNumero(),
                    listBd.get(i).getNumero(),
                    listBd.get(i).getMessage(),
                    listBd.get(i).getJour(),
                    listBd.get(i).getRead(),
                    image[genererNbre(8)],
                    listBd.get(i).getNumero().substring(0,1) , false,
                    listBd.get(i).getId()));
        }



        // on associe a chaque numero son dernier message
        for (int i=0; i<listAdress.size();i++){
            listDernierMessage.add(dernierMessage(list, listAdress.get(i)));
        }



// on remplace chaque numero par son nom s'il existe dans la liste des contact
        for (int i = 0 ; i < listDernierMessage.size(); i++){

            // si c'est un groupe
            if (listDernierMessage.get(i).getNumero().split(",").length>=2){

                String nomGroupe = "";
                for (int k = 0; k < listDernierMessage.get(i).getNumero().split(",").length ; k++) {

                    for (int j = 0; j < listContact.size(); j++){
                        if (listDernierMessage.get(i).getNumero().split(",")[k].equals(listContact.get(j).getContact())){
                            nomGroupe = nomGroupe +"," +listContact.get(j).getName();
                        }
                    }

                }

                listDernierMessage.get(i).setNom(nomGroupe.substring(1));


            }else {

                for (int j = 0; j < listContact.size(); j++) {
                    if (listDernierMessage.get(i).getNumero().equals(listContact.get(j).getContact())) {
                        listDernierMessage.get(i).setNom(listContact.get(j).getName());
                        listDernierMessage.get(i).setFirsrLetter(listContact.get(j).getName().substring(0, 1).toUpperCase());
                    }
                }
            }
        }

        //on reclasse la liste dans l'ordre en fonction des long


        /**
         * ici on transfert les elemts d'une liste pour l'autre tout en les classant du plus recent au plus ancien
         */
        final int taille = listDernierMessage.size(); // juste pour definir la constance
        for (int k = 0; k < taille ;k++){
            listDernierMessageOfficiel.add(plusGrand(listDernierMessage));
            listDernierMessage.remove(plusGrand(listDernierMessage));

        }



        //on parcour et on modifie la date de long au format nornmal et on modifie aussi l'image pour les groupe
        for(int t = 0; t<listDernierMessageOfficiel.size();t++){
            //on formate la date
            Date date = new Date(Long.parseLong(listDernierMessageOfficiel.get(t).getJour()));
            String jourMois = (String) DateFormat.format("dd", date);
            String jourSemaine = (String) DateFormat.format("EE", date);
            String mois = (String) DateFormat.format("MMMM", date);
            listDernierMessageOfficiel.get(t).setJour(jourSemaine+" "+jourMois+" "+mois);

            if (listDernierMessageOfficiel.get(t).getNumero().split(",").length>=2){
                listDernierMessageOfficiel.get(t).setFirsrLetter("");
                listDernierMessageOfficiel.get(t).setImage(R.mipmap.img_groupee);
            }

        }


        Log.d("oswald", Integer.toString(list.size()));
        Log.d("oswald", Integer.toString(listBd.size()));

        return listDernierMessageOfficiel;
    }


    /**
     * cette methode permet de determiner le model de message qui est le plus grand
     * @param list
     * @return
     */
    public ModelMessagePrinc plusGrand(List<ModelMessagePrinc> list){
        ModelMessagePrinc messagePrinc = list.get(0);
        if (list.size()>1) {
            for (int i = 1; i < list.size(); i++) {
                if (Long.parseLong(messagePrinc.getJour()) < Long.parseLong(list.get(i).getJour())) {
                    messagePrinc = list.get(i);
                } else {

                }
            }
        }
        if (list.size()==1){
            messagePrinc = list.get(0);
        }

        return messagePrinc;
    }

    /**
     * cette fonction genere aleatoirement un nbre entre 0 et max et le retout pur utilisation
     * @param max
     * @return un entier compris entre 0 et max
     */
    public int genererNbre (int max){
        Random random = new Random();
        return random.nextInt(max);
    }
    
    /**
     * retourn ele dernier message d'un contact
     * @param listGnl
     * @param adress
     * @return
     */
    public ModelMessagePrinc dernierMessage(List<ModelMessagePrinc> listGnl, String adress){
        String message = null;
        Long dateLastMessage = Long.valueOf(0);
        int read = 0;
        int image = 0;
        String firstLetter ="A";
        String id = null;

        for (int i = 0; i<listGnl.size(); i++){

            // si c'est le numero de la discussion depasse 4 caractere et n'est pas un groupe;
            if (adress.replace(" ","").length()>4 && listGnl.get(i).getNumero().split(",").length<2) {

                if (adress.replace(" ", "").contains("+")) {
                    if ((listGnl.get(i).getNumero().replace(" ", "").equals(adress.replace(" ", "")) ||
                            adress.replace(" ", "").contains(listGnl.get(i).getNumero().replace(" ", ""))) &&
                            dateLastMessage <= Long.parseLong(listGnl.get(i).getJour())) {

                        message = listGnl.get(i).getMessage();
                        dateLastMessage = Long.valueOf(listGnl.get(i).getJour());
                        read = listGnl.get(i).getRead();
                        image = listGnl.get(i).getImage();
                        firstLetter = listGnl.get(i).getFirsrLetter();
                        id = listGnl.get(i).getId();

                    }
                } else {
                    if ((listGnl.get(i).getNumero().replace(" ", "").equals(adress.replace(" ", "")) ||
                            listGnl.get(i).getNumero().replace(" ", "").contains(adress.replace(" ", ""))) &&
                            dateLastMessage <= Long.parseLong(listGnl.get(i).getJour())) {

                        message = listGnl.get(i).getMessage();
                        dateLastMessage = Long.valueOf(listGnl.get(i).getJour());
                        read = listGnl.get(i).getRead();
                        image = listGnl.get(i).getImage();
                        firstLetter = listGnl.get(i).getFirsrLetter();
                        id = listGnl.get(i).getId();

                    }
                }

            }else {

                if (listGnl.get(i).getNumero().replace(" ","").equals(adress.replace(" ","")) && dateLastMessage <= Long.parseLong(listGnl.get(i).getJour())) {
                    message = listGnl.get(i).getMessage();
                    dateLastMessage = Long.valueOf(listGnl.get(i).getJour());
                    read = listGnl.get(i).getRead();
                    image = listGnl.get(i).getImage();
                    firstLetter = listGnl.get(i).getFirsrLetter();
                    id = listGnl.get(i).getId();
                }
            }
        }

        //on formate la date
        Date date = new Date(dateLastMessage);
        String jourMois = (String) DateFormat.format("dd", date);
        String jourSemaine = (String) DateFormat.format("EE", date);
        String mois = (String) DateFormat.format("MMMM", date);


        final ModelMessagePrinc modelMessagePrinc = new ModelMessagePrinc(adress, adress, message, String.valueOf(dateLastMessage), read, image, firstLetter, false,id);
        return modelMessagePrinc;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * cette methode permet de declancher la selection multiple en passant is_multi_selectin a true et en modifaint la toolbar
     * @param v
     * @return
     */
    @Override
    public boolean onLongClick(View v) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.delete_list);
        toolbar.setTitle(R.string.null_select);
        is_multiselection_active = true;
        adapterMessagePrincipale.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    /**
     * cette methode defini le comment doit reagir l'application au clic de selection et au clic de deselection
     * @param position
     */
    public void prepareSelection( int position){
        if (is_multiselection_active == true) {
            if (!Messg_generale.get(position).isCheket()) {
                Messaeg_select.add(Messg_generale.get(position));
                counter = counter + 1;
                updateCounter(counter);
                Messg_generale.get(position).setImage(R.mipmap.icon_muutiple_selection);
                Messg_generale.get(position).setCheket(true);
                Messg_generale.get(position).setFirsrLetter(" ");
                adapterMessagePrincipale.notifyDataSetChanged();
            } else {
                Messaeg_select.remove(Messg_generale.get(position));
                counter = counter - 1;
                updateCounter(counter);
                Messg_generale.get(position).setCheket(false);

                // si c'est un groupe
                if (Messg_generale.get(position).getNumero().split(",").length>=2){
                    Messg_generale.get(position).setImage(R.mipmap.img_groupee);
                    Messg_generale.get(position).setFirsrLetter("");
                }else {
                    Messg_generale.get(position).setImage(image[genererNbre(8)]);
                    Messg_generale.get(position).setFirsrLetter(Messg_generale.get(position).getNumero().substring(0, 1));
                }
                adapterMessagePrincipale.notifyDataSetChanged();
            }
        }
    }

    /**
     * cette methode nous permet de mettre a jour le compteur de selection (nbre d'element selectionnee)
     * @param counter
     */
    public void updateCounter(int counter){
        if (counter == 0){
            toolbar.setTitle(R.string.null_select);
        }else {
            toolbar.setTitle(counter+" "+getResources().getString(R.string.selection));
        }
    }

    /**
     * cette methode remet tous a normale quant on quitte le mode selection Multiple
     */
    public void clearActionMode(){
        is_multiselection_active = false;
        toolbar.getMenu().clear();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.inflateMenu(R.menu.menu_messagerie_principale);
        toolbar.setTitle(R.string.message);
        counter = 0;
        Messaeg_select.clear();

        // initialisation des listes
        listContact = getContact();
        Messg_generale = getMessage();

        recyclerView = findViewById(R.id.recycleMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterMessagePrincipale = new AdapterMessagePrincipale(this, Messg_generale);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMessagePrincipale);
        // fin recycerView
    }

    public void deselection(List<ModelMessagePrinc> listSelectionne){
        if (listSelectionne.size()>0){
            for (int i =0; i<listSelectionne.size();i++){
                is_multiselection_active = false;
                int position = Messg_generale.indexOf(listSelectionne.get(i))+1;
                Messg_generale.get(position).setImage(image[genererNbre(8)]);
                Messg_generale.get(position).setFirsrLetter(Messg_generale.get(position).getNumero().substring(0, 1));
                Messg_generale.get(position).setCheket(false);
                adapterMessagePrincipale.notifyDataSetChanged();
            }
            listSelectionne.clear();
            counter = 0;
        }
    }

    /**
     * il defini la reaction au clic sur le btn back si on est en mode selection multiple
     */
    @Override
    public void onBackPressed() {
        if (is_multiselection_active){
            clearActionMode();
            adapterMessagePrincipale.notifyDataSetChanged();
            deselection(Messaeg_select);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.new_discusion:
                if (ActivityCompat.checkSelfPermission(ActivityMessaferiePrincipale.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ActivityMessaferiePrincipale.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                }else {
                    clearActionMode();
                    deselection(Messaeg_select);
                    Intent intent = new Intent(this, Contact_messagerie.class);
                    intent.putExtra("valeur", false);
                    intent.putExtra("groupe",false);
                    startActivityForResult(intent,ID_PAGE);
                }
                break;
        }
    }

    /**
     * cette methode retourne la liste des contacts
     * @return
     */
    private List<ModelContacts> getContact(){

        List<ModelContacts> list = null;
        if (ActivityCompat.checkSelfPermission(ActivityMessaferiePrincipale.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ActivityMessaferiePrincipale.this,new String[]{Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);
        }else {
            // je recupere les contact sauvegarder dans en local dans calldetect
            DataBaseManager baseManager = new DataBaseManager(this);
            List<ContactPhone> contactPhoneList = baseManager.lectureContact();
            baseManager.close();
            //fin




            list = new ArrayList<>();
            Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                list.add(new ModelContacts(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), false, R.drawable.ic_account));
            }

            //je conactenne les deux listes
            for (int i = 0; i < contactPhoneList.size(); i++) {
                list.add(new ModelContacts(contactPhoneList.get(i).getNom(), contactPhoneList.get(i).getNumero()));
            }
            //fin
        }

        return list;

    }





    /**
     * quant on retourne sur l'activite, le systeme execute cette methode override
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            // on initialise la list et on reappel de receiver
            listContact = getContact();
            Messg_generale = getMessage();


            recyclerView = findViewById(R.id.recycleMessage);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivityMessaferiePrincipale.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapterMessagePrincipale = new AdapterMessagePrincipale(ActivityMessaferiePrincipale.this, Messg_generale);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapterMessagePrincipale);


    }

}
