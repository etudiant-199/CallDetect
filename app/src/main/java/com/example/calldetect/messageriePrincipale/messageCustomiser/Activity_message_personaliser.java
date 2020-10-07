package com.example.calldetect.messageriePrincipale.messageCustomiser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.database.DataBaseManager;
import com.example.calldetect.firebase_data_base_manager.SaveContact;
import com.example.calldetect.firebase_data_base_manager.SignalNumberHelper;
import com.example.calldetect.messageriePrincipale.BannierePersonnaliser.BanniereCustomiser;
import com.example.calldetect.messageriePrincipale.Brouillon.messageBrouillon;
import com.example.calldetect.messageriePrincipale.ContactBlocquer.ContactLock;
import com.example.calldetect.messageriePrincipale.ModelMessagePrinc;
import com.example.calldetect.messageriePrincipale.Sim.AdapterSim;
import com.example.calldetect.messageriePrincipale.Sim.Sim_class;
import com.example.calldetect.messageriePrincipale.contact.ContactPhone;
import com.example.calldetect.messageriePrincipale.contact.Contact_messagerie;
import com.example.calldetect.messageriePrincipale.infoGroupe.Info_groupe;
import com.example.calldetect.messageriePrincipale.infoUser.InfoUser;
import com.example.calldetect.models.ModelContacts;
import com.example.calldetect.models.SignalNumber;
import com.example.calldetect.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Activity_message_personaliser extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final int ID_PAGE = 1 ;
    private ImageButton btnSendactif, btnSendPassif;
    private Toolbar toolbar;

    private String namePersonne, numberPersonne;
    public String nomSim = " ";
    public RecyclerView recyclerView;
    public AdapterMessagePersonnaliser adapterMessagePersonnaliser;
    public List<ModelMessagePrinc> lisMsg, listMessageSelect;
    private EditText message;

    private IntentFilter intentFilter;
    public Spinner listsim;
    private  List<ContactLock> listContactBloquer;
    private Boolean contactBlocque = false;
    private ArrayList<Sim_class> myArraySim;
    private AdapterSim adapterSim;
    public Boolean is_multiselection_active = false;
    private int counter = 0;
    private LinearLayoutManager linearLayoutManagerE,linearLayoutManagerR;
    public Long dt;
    private Boolean isContactSignaled = false;



    // ici on dit ce qui doit ce passer quant un nouveau message arrive
    private BroadcastReceiver intenReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //on initialise la liste de message;
            DataBaseManager manager1 = new DataBaseManager(context);
            lisMsg = manager1.lectureMessage(numberPersonne);
            manager1.close();
            //fin

            // on initailise de recyclerview
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapterMessagePersonnaliser = new AdapterMessagePersonnaliser(lisMsg, context);
            recyclerView.setHasFixedSize(true);
            recyclerView.scrollToPosition(adapterMessagePersonnaliser.getItemCount()-1);// cessi me met directement a la fin de mon recyclerView
            recyclerView.setAdapter(adapterMessagePersonnaliser);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_personaliser);



        // intent filter pour declancher le braodcast
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        // fin intent-filter



        // on cree l'oject personne
        numberPersonne = this.getIntent().getExtras().getString("number");


        //initialisation des variables
        btnSendactif = findViewById(R.id.btnSendactif);
        btnSendPassif = findViewById(R.id.btnSendpassif);
        toolbar = findViewById(R.id.Toolbar_page_centrale);
        recyclerView = findViewById(R.id.conversation);
        message = findViewById(R.id.message);
        listsim = findViewById(R.id.listSim);
        listMessageSelect = new ArrayList<>();




        // on personnalise la toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //fin



        // ici on associe au numero un nom si cela existe
        if (nomContact(numberPersonne) == null) {


            namePersonne = numberPersonne;
            toolbar.setTitle(namePersonne);




            //+++++++++++++++++++++++++++++++++++++ ici on  fouille le numero dans la base de donnee et on active la palette ++++++++++++++++++++++++++

            DataBaseManager manager = new DataBaseManager(this);
            final List<InfoUser> userInfos = manager.lectureInfoUser();
            List<String> listContactSignaled = manager.recuperationContactDangereux();
            manager.close();

            final String numberPersonneUpdate;
            // ici jessaye de rendre le numero conforme ! en ajoutant le code du pays devant le numero s'il nya pas !
            if (numberPersonne.contains("+")){
                numberPersonneUpdate = numberPersonne;
            }else {
                numberPersonneUpdate = "+"+userInfos.get(0).getCodepays()+numberPersonne;
            }


            // on verifie dans la bd si ce n'est pas un contact signaler
            if (listContactSignaled.contains(numberPersonneUpdate.replace(" ",""))){
                isContactSignaled = true;
            }else {
                SignalNumberHelper.takeSinalNumber(numberPersonneUpdate.replace(" ", ""))
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String numerobd = documentSnapshot.getString("number");
                                if (numerobd != null) {
                                    isContactSignaled = true;
                                }
                            }
                        });
            }
            //fin



            //ici commence la detection du numero dans la base de donnee
            SaveContact.takeConact(numberPersonneUpdate.replace(" ",""))
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            final String nom = documentSnapshot.getString("nom");
                            final String numero = documentSnapshot.getString("numero");

                            final ContactPhone contactPhone = new  ContactPhone(nom, numberPersonneUpdate);

                            if (nom == null){
                                 contactPhone.setNom("Iconnue");
                            }else {

                            }
                            final BanniereCustomiser popup = new BanniereCustomiser(Activity_message_personaliser.this);
                            popup.setNomInconne(contactPhone.getNom());
                            popup.setNumeroInconue(numberPersonne);
                            popup.setfirtletterInconne(contactPhone.getNom().substring(0,1));
                            if (isContactSignaled){
                                popup.setCouleurCardre(R.color.danger);
                            }


                            popup.btnConfirmer().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DataBaseManager manager = new DataBaseManager(Activity_message_personaliser.this);

                                    manager.insertContact(contactPhone);
                                    manager.close();

                                    toolbar.setTitle(contactPhone.getNom());
                                    namePersonne = contactPhone.getNom();

                                    popup.dismiss();
                                }
                            });

                            popup.btnSignaler().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DataBaseManager manager = new DataBaseManager(Activity_message_personaliser.this);
                                    if (contactPhone.getNumero().contains("+")){
                                        manager.insertContactDangereux(contactPhone.getNumero().replace(" ",""));
                                    }else {
                                        manager.insertContactDangereux(("+"+userInfos.get(0).getCodepays()+contactPhone.getNumero()).replace(" ",""));
                                    }

                                    manager.close();
                                    insertContactSignale();// insertion dans firebase
                                    popup.dismiss();


                                }
                            });

                            popup.btnBloquer().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DataBaseManager manager = new DataBaseManager(Activity_message_personaliser.this);
                                    manager.insertContactBlocquer(new ContactLock(contactPhone.getNumero() , nom));
                                    manager.close();
                                    popup.dismiss();
                                }
                            });


                            popup.btnRefuser().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //////////// ici rien na ete fait ! en cour de reflection
                                    popup.dismiss();
                                }
                            });


                            popup.btnCancel().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popup.dismiss();
                                }
                            });


                            popup.btnMessage().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popup.dismiss();
                                }
                            });

                            popup.btnAppeller().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.call(Activity_message_personaliser.this, numberPersonne);
                                }
                            });

                            popup.BtnValiderNom().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String nomsaisie = popup.getSaisieNomInconue();
                                    if (!nomsaisie.replace(" ","").equals("")){
                                        SaveContact.addContact(new ContactPhone(nomsaisie, contactPhone.getNumero()));

                                        // on ajoute egalement ce conatact en bd locale
                                        DataBaseManager manager = new DataBaseManager(Activity_message_personaliser.this);
                                        manager.insertContact(new ContactPhone(nomsaisie, contactPhone.getNumero()));
                                        manager.close();
                                        //fin

                                        toolbar.setTitle(contactPhone.getNom());
                                        namePersonne = contactPhone.getNom();

                                        popup.dismiss();

                                    }
                                }
                            });

                            popup.btnAjouterContact().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DataBaseManager manager = new DataBaseManager(Activity_message_personaliser.this);
                                    manager.insertContact(new ContactPhone(nom, contactPhone.getNumero()));
                                    manager.close();

                                    //.................. modifier la toolbar plus tard
                                    toolbar.setTitle(contactPhone.getNom());
                                    namePersonne = contactPhone.getNom();

                                    popup.dismiss();
                                }
                            });

                            popup.build();
                            Toast.makeText(Activity_message_personaliser.this, nom+" "+numero, Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
            // fin de fouille dans la base de donnee





        } else {
            namePersonne = nomContact(numberPersonne);
            toolbar.setTitle(nomContact(numberPersonne));
        }



        //on cache le bouton d'envoie actif
        btnSendPassif.setVisibility(View.GONE);

        // on active les btn pour qu'il reagissent au clic
        btnSendactif.setOnClickListener(this);

        //on initialise la liste de message;
        DataBaseManager manager = new DataBaseManager(this);
        lisMsg = manager.lectureMessage(numberPersonne);
        manager.close();
        //fin


        // on initailise de recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterMessagePersonnaliser = new AdapterMessagePersonnaliser(lisMsg, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.scrollToPosition(adapterMessagePersonnaliser.getItemCount()-1);// cessi me met directement a la fin de mon recyclerView
        recyclerView.setAdapter(adapterMessagePersonnaliser);
        //fin



        // ici on regarde s'il ya un msge sauvegarder coe brouillon
        DataBaseManager managerBd = new DataBaseManager(this);
        List<messageBrouillon> messageBrouillonList = managerBd.selectMessageBrouillon();
        if (containElement(messageBrouillonList, numberPersonne)){
            message.setText(messageBrouillon(messageBrouillonList, numberPersonne));
        }
        managerBd.close();
        //fin



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager localsubscriptionManager = SubscriptionManager.from(Activity_message_personaliser.this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (localsubscriptionManager.getActiveSubscriptionInfoCount() > 1) {

                List localList = localsubscriptionManager.getActiveSubscriptionInfoList();
                SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
                SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);


                // on initialise la liste des sim
                myArraySim = new ArrayList<>();
                myArraySim.add(new Sim_class(R.drawable.sim1, String.valueOf(simInfo1.getDisplayName())));
                myArraySim.add(new Sim_class(R.drawable.sim2, String.valueOf(simInfo2.getDisplayName())));

                //adpter sim..........
                adapterSim = new AdapterSim(this, myArraySim);
                listsim.setAdapter(adapterSim);

            } else {
                listsim.setVisibility(View.GONE);
            }
        }


        // ici on gerer le textView pour les contact Blocque

        DataBaseManager manager1 = new DataBaseManager(this);
        listContactBloquer = manager1.listContactBlocquer();
        manager1.close();

        for (int i = 0; i < listContactBloquer.size() ; i++) {
            if (listContactBloquer.get(i).getNumero().equals(numberPersonne)){
                message.setEnabled(false);
                message.setFocusable(false);
                message.setText(R.string.contactBlocque);
                listsim.setVisibility(View.GONE);
                contactBlocque = true;
            }
        }
    }


    /**
     * on declanche le broadcast Reciver
     */
    @Override
    protected void onResume() {
        registerReceiver(intenReciver, intentFilter);
        super.onResume();
    }

    /**
     * on met fin au receiver des que l'action est fini
     */
    @Override
    protected void onPause() {
        unregisterReceiver(intenReciver);
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onClick(View v) {
        if (!is_multiselection_active) {
            switch (v.getId()) {
                case R.id.btnSendactif:


                    if (!contactBlocque && !message.getText().toString().replace(" ","").equals("")) {
                        dt = System.currentTimeMillis();// on recuperer la date et l'heure en long

                        //si c'est un groupe
                        if (numberPersonne.split(",").length >= 2) {

                            for (int i = 0; i < numberPersonne.split(",").length; i++) {
                                sendMessage(numberPersonne.split(",")[i], message.getText().toString(), String.valueOf(dt));
                            }


                        } else {
                            sendMessage(numberPersonne, message.getText().toString(), String.valueOf(dt));
                        }


                        ModelMessagePrinc messagePrinc = new ModelMessagePrinc(numberPersonne, message.getText().toString(), String.valueOf(dt), 1, nomSim, 0, 0, 0, 0);

                        //on sauvegarde le message.
                        DataBaseManager manager = new DataBaseManager(this);
                        manager.insertMessage(messagePrinc);
                        manager.close();

                        //on initialise la liste de message;
                        DataBaseManager manager1 = new DataBaseManager(this);
                        lisMsg = manager1.lectureMessage(numberPersonne);
                        manager1.close();
                        //fin

                        // on initailise de recyclerview
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        adapterMessagePersonnaliser = new AdapterMessagePersonnaliser(lisMsg, this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.scrollToPosition(adapterMessagePersonnaliser.getItemCount() - 1);// cessi me met directement a la fin de mon recyclerView
                        recyclerView.setAdapter(adapterMessagePersonnaliser);

                        // on vide le contenue du EditText ou il ya le message
                        message.setText("");

                        // on actualise la page si necessaire

                    }

                    break;
            }
        }else {

        }
    }




    /**
     * cette methode est celle qui permet d'envoyer les message et de gerer l'accuser de reception.
     * @param number
     * @param message
     * @param date
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
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


                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                            DataBaseManager manager = new DataBaseManager(context);
                            manager.UpdateEchecMsg(date);
                            manager.close();



                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:


                            break;
                    }
                    //on initialise la liste de message;
                    DataBaseManager manager1 = new DataBaseManager(context);
                    lisMsg = manager1.lectureMessage(numberPersonne);
                    manager1.close();
                    //fin
                    // on initailise de recyclerview
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    adapterMessagePersonnaliser = new AdapterMessagePersonnaliser(lisMsg, context);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.scrollToPosition(adapterMessagePersonnaliser.getItemCount() - 1);
                    recyclerView.setAdapter(adapterMessagePersonnaliser);
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

                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                            DataBaseManager manager = new DataBaseManager(context);
                            manager.UpdateEchecMsg(date);
                            manager.close();


                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:

                            break;
                    }

                    //on initialise la liste de message;
                    DataBaseManager manager1 = new DataBaseManager(context);
                    lisMsg = manager1.lectureMessage(numberPersonne);
                    manager1.close();
                    //fin
                    // on initailise de recyclerview
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    adapterMessagePersonnaliser = new AdapterMessagePersonnaliser(lisMsg, context);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.scrollToPosition(adapterMessagePersonnaliser.getItemCount() - 1);
                    recyclerView.setAdapter(adapterMessagePersonnaliser);
                }
            }, new IntentFilter(DELIVERED));



            //envoie du message avec le spinner

            // on verifie la correspondance entre le nom dsns le spinner et le id
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                SubscriptionManager localsubscriptionManager = SubscriptionManager.from(Activity_message_personaliser.this);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (localsubscriptionManager.getActiveSubscriptionInfoCount() > 1) {


                    List localList = localsubscriptionManager.getActiveSubscriptionInfoList();
                    SubscriptionInfo sim1 = (SubscriptionInfo) localList.get(0);
                    SubscriptionInfo sim2 = (SubscriptionInfo) localList.get(1);

                    // si le  nom dans le spinner correspond au nom de la sim1, on envoi le message avec id de cette sim la .sinon on envoi avec l'id de la sim2


                    if (listsim.getSelectedItemPosition()==0) {
                        SmsManager.getSmsManagerForSubscriptionId(sim1.getSubscriptionId()).sendTextMessage(number, null, message, sendPI, deliveredID);
                        nomSim = (String) sim1.getDisplayName();
                    } else {
                        SmsManager.getSmsManagerForSubscriptionId(sim2.getSubscriptionId()).sendTextMessage(number, null, message, sendPI, deliveredID);
                        nomSim = (String) sim2.getDisplayName();
                    }

                }else {


                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(number, null, message, sendPI, deliveredID);

                }
            }




        }catch (Exception e){

        }

    }



    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (numberPersonne.split(",").length>=2){
            getMenuInflater().inflate(R.menu.menu_groupe, menu);
        }else {
            if (!is_multiselection_active) {
                if (!contactBlocque) {
                    getMenuInflater().inflate(R.menu.menu_message_personnalise, menu);
                } else {
                    getMenuInflater().inflate(R.menu.menu_desactiviter_contact, menu);
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                if (is_multiselection_active){
                    clearActionMode();
                }else {
                    if (!message.getText().toString().trim().equals("") && !contactBlocque) {

                        DataBaseManager manager = new DataBaseManager(this);
                        List<messageBrouillon> listBrouillon = manager.selectMessageBrouillon();
                        if (containElement(listBrouillon, numberPersonne)) {
                            messageBrouillon brouillon = new messageBrouillon(numeroBdBrouilon(listBrouillon, numberPersonne), message.getText().toString());
                            manager.updateMsgeBrouillon(brouillon);
                        } else {

                            messageBrouillon brouillon = new messageBrouillon(numberPersonne, message.getText().toString());
                            manager.insertBrouillon(brouillon);
                        }
                        manager.close();
                        Toast.makeText(this, R.string.brouillon, Toast.LENGTH_SHORT).show();


                    }else {
                        if (message.getText().toString().replace(" ","").equals("")) {
                            DataBaseManager manager1 = new DataBaseManager(this);
                            List<messageBrouillon> listBrouillon1 = manager1.selectMessageBrouillon();
                            if (containElement(listBrouillon1, numberPersonne)) {
                                manager1.suppressionBrouillon(numeroBdBrouilon(listBrouillon1, numberPersonne));
                            }

                            manager1.close();
                        }


                    }

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();

                }
                break;

            case R.id.bloquerNum:
                ContactLock contactLock = new ContactLock(numberPersonne ,namePersonne);

                DataBaseManager manager = new DataBaseManager(this);
                manager.insertContactBlocquer(contactLock);
                manager.close();
                message.setEnabled(false);
                message.setFocusable(false);
                message.setText(R.string.contactBlocque);
                listsim.setVisibility(View.GONE);
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_desactiviter_contact);
                contactBlocque = true;
                break;

            case R.id.deblocquer:
                DataBaseManager manager1 = new DataBaseManager(this);
                manager1.suppresionContactBlocquer(numberPersonne);
                manager1.close();
                message.setEnabled(true);
                message.setFocusable(true);
                message.setText("");
                message.setHint(R.string.saisirMessageTexte);
                listsim.setVisibility(View.VISIBLE);
                contactBlocque = false;
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_message_personnalise);
                break;
            case R.id.signalerNum:
                List<String> contactDanger;
                DataBaseManager manager2 = new DataBaseManager(this);
                contactDanger = manager2.recuperationContactDangereux();
                List<InfoUser> infoUserList = manager2.lectureInfoUser();
                if (!contactDanger.contains(numberPersonne.replace(" ",""))){
                    if (numberPersonne.contains("+")){
                        manager2.insertContactDangereux(numberPersonne.replace(" ",""));
                    }else {
                        manager2.insertContactDangereux(("+"+infoUserList.get(0).getCodepays()+numberPersonne).replace(" ",""));
                    }

                    Toast.makeText(this, getResources().getString(R.string.confirmationsignalement), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, getResources().getString(R.string.signalementExistant), Toast.LENGTH_SHORT).show();
                }
                manager2.close();
                break;
            case R.id.bloquerEtSignaler:

                ContactLock contactLock1 = new ContactLock(numberPersonne ,namePersonne);

                DataBaseManager manager3 = new DataBaseManager(this);
                manager3.insertContactBlocquer(contactLock1);
                manager3.close();
                message.setEnabled(false);
                message.setFocusable(false);
                message.setText(R.string.contactBlocque);
                listsim.setVisibility(View.GONE);
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_desactiviter_contact);
                contactBlocque = true;


                List<String> contactDanger1;
                DataBaseManager manager4 = new DataBaseManager(this);
                contactDanger1 = manager4.recuperationContactDangereux();
                if (!contactDanger1.contains(numberPersonne)){
                    manager4.insertContactDangereux(numberPersonne);
                    Toast.makeText(this, getResources().getString(R.string.confirmationsignalement), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, getResources().getString(R.string.signalementExistant), Toast.LENGTH_SHORT).show();
                }
                manager4.close();
                break;

            case R.id.delete_msg_personalise:
                DataBaseManager dataBaseManager = new DataBaseManager(this);
                dataBaseManager.suppressionQuelqueSms(listMessageSelect);
                dataBaseManager.close();

                clearActionMode();

                break;

            case R.id.transferer:

                methodeTransfertMsg();

                break;
            case R.id.infoGroup:

                Intent intent = new Intent(Activity_message_personaliser.this, Info_groupe.class);
                intent.putExtra("numero",numberPersonne);
                intent.putExtra("nom", namePersonne);
                startActivityForResult(intent, ID_PAGE);

                break;

            case R.id.supGroup:
                List<ModelMessagePrinc> list = new ArrayList<>();
                list.add(new ModelMessagePrinc(numberPersonne));
                DataBaseManager dataBaseManager1 = new DataBaseManager(this);
                dataBaseManager1.suppressionDiscussion(list);
                dataBaseManager1.close();

                Intent intent1 = new Intent();
                setResult(RESULT_OK, intent1);
                finish();
                break;

            case R.id.appeler:
                Utils.call(Activity_message_personaliser.this, numberPersonne);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * cette mthode et celle qui assure la focntion de transfert d'un message !
     */
    public void methodeTransfertMsg(){
        if (listMessageSelect.size()==1){
            Intent intent = new Intent(Activity_message_personaliser.this, Contact_messagerie.class);
            intent.putExtra("valeur", true);
            intent.putExtra("messageTransfert", listMessageSelect.get(0).getMessage());
            intent.putExtra("groupe",false);
            startActivityForResult(intent,ID_PAGE);
        }
    }





    @Override
    public void onBackPressed() {

        // si la selection multiple pour les msge elementaire n'est pas activer; alors ...
        if (!is_multiselection_active) {

            // si la zone du msg n'est aps vide et le contactact n'est pas blocquer , alors on sauveagrde le brouillon
            if (!message.getText().toString().trim().equals("") && !contactBlocque) {

                DataBaseManager manager = new DataBaseManager(this);
                List<messageBrouillon> listBrouillon = manager.selectMessageBrouillon();
                if (containElement(listBrouillon, numberPersonne)) {
                    messageBrouillon brouillon = new messageBrouillon(numeroBdBrouilon(listBrouillon, numberPersonne), message.getText().toString());
                    manager.updateMsgeBrouillon(brouillon);
                } else {

                    messageBrouillon brouillon = new messageBrouillon(numberPersonne, message.getText().toString());
                    manager.insertBrouillon(brouillon);
                }
                manager.close();
                Toast.makeText(this, R.string.brouillon, Toast.LENGTH_SHORT).show();


            } else {
                if (message.getText().toString().replace(" ","").equals("")) {
                    DataBaseManager manager1 = new DataBaseManager(this);
                    List<messageBrouillon> listBrouillon1 = manager1.selectMessageBrouillon();
                    if (containElement(listBrouillon1, numberPersonne)) {
                        manager1.suppressionBrouillon(numeroBdBrouilon(listBrouillon1, numberPersonne));
                    }

                    manager1.close();
                }


            }

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);

            super.onBackPressed();

        }




        if (is_multiselection_active){
            clearActionMode();
        }


    }


    /**
     * cette methode prend une liste de messge brouillon et dit si un contatc a deja un messge brouillon
     * @param listmessageBrouillons
     * @param numero
     * @return
     */
    public Boolean containElement(List<messageBrouillon> listmessageBrouillons , String numero){
        for (int i=0; i<listmessageBrouillons.size();i++){
            if (numero.contains("+")){
                if (numero.replace(" ","").equals(listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", "")) | numero.replace(" ", "").contains(listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", ""))){
                    return true;
                }
            }else {
                if (numero.replace(" ", "").equals(listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", "")) | listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", "").contains(numero.replace(" ", "")) ){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * cette methode ci me permet de recuperer le numero avec la quelle jai d'abort sauvegarder le premier brouillon !
     * @param listmessageBrouillons
     * @param numero
     * @return
     */
    public String numeroBdBrouilon(List<messageBrouillon> listmessageBrouillons, String numero)  {
        for (int i=0; i<listmessageBrouillons.size();i++){
            if (numero.contains("+")){
                if (numero.replace(" ","").equals(listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", "")) | numero.replace(" ", "").contains(listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", ""))){
                    return listmessageBrouillons.get(i).getNumeroBrouillon();
                }
            }else {
                if (numero.replace(" ", "").equals(listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", "")) | listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", "").contains(numero.replace(" ", "")) ){
                    return listmessageBrouillons.get(i).getNumeroBrouillon();
                }
            }
        }
        return null;
    }


    /**
     * cette methode ci me permet de recuperer le message sauveagerder coe brouillon sauvegarder le premier brouillon !
     * @param listmessageBrouillons
     * @param numero
     * @return
     */
    public String messageBrouillon(List<messageBrouillon> listmessageBrouillons, String numero)  {
        for (int i=0; i<listmessageBrouillons.size();i++){
            if (numero.contains("+")){
                if (numero.replace(" ","").equals(listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", "")) | numero.replace(" ", "").contains(listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", ""))){
                    return listmessageBrouillons.get(i).getMessageBrouillon();
                }
            }else {
                if (numero.replace(" ", "").equals(listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", "")) | listmessageBrouillons.get(i).getNumeroBrouillon().replace(" ", "").contains(numero.replace(" ", "")) ){
                    return listmessageBrouillons.get(i).getMessageBrouillon();
                }
            }
        }
        return null;
    }


    /**
     * cette methode retourne la liste des contacts
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

    /**
     * cette methode permet de rechercher le nom associer a un contect
     * @param numero
     * @return
     */
    public String  nomContact(String numero){

        List<ModelContacts> list ;
       list = getContact();

       // si c'est un groupe
       if (numero.split(",").length>=2){

           String nomGroupe = "";
           for (int k = 0; k < numero.split(",").length ; k++) {

               for (int j = 0; j < list.size(); j++){
                   if (numero.split(",")[k].equals(list.get(j).getContact())){
                       nomGroupe = nomGroupe +"," +list.get(j).getName();
                   }
               }

           }

           return nomGroupe.substring(1);

       }else {

           for (int i = 0; i < list.size(); i++) {
               if (list.get(i).getContact().equals(numero)) {
                   return list.get(i).getName();
               }
           }

       }
        return null;
    }


    /**
     * pour declencher la selection multiple
     * @param v
     * @return
     */
    @Override
    public boolean onLongClick(View v) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_selection_act_personnaliser);
        toolbar.setTitle(R.string.null_select);
        is_multiselection_active = true;
        adapterMessagePersonnaliser.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    /**
     * cette mthode designe l'action qui sera fait quant on selection un message lors de la selection multiple
     * @param position
     */
    @SuppressLint("ResourceAsColor")
    public void selectionMessage(int position){
        if (is_multiselection_active) {
            if (!lisMsg.get(position).isCheket()) {
                lisMsg.get(position).setCheket(true);
                listMessageSelect.add(lisMsg.get(position));
                counter = counter + 1;
                modificationHeader(counter);



            }else {
                lisMsg.get(position).setCheket(false);
                listMessageSelect.remove(lisMsg.get(position));
                counter --;
                modificationHeader(counter);



            }
        }

    }


    /**
     * cette methode remet tous a normale quant on quitte le mode selection Multiple
     */
    public void clearActionMode(){
        is_multiselection_active = false;
        toolbar.getMenu().clear();
        if (numberPersonne.split(",").length>=2){
            toolbar.inflateMenu(R.menu.menu_groupe);
        }else {
            if (!contactBlocque) {
                toolbar.inflateMenu(R.menu.menu_message_personnalise);
            } else {
                toolbar.inflateMenu(R.menu.menu_desactiviter_contact);
            }
        }

        toolbar.setTitle(namePersonne);
        counter = 0;
        listMessageSelect.clear();

        //on initialise la liste de message;
        DataBaseManager manager = new DataBaseManager(this);
        lisMsg = manager.lectureMessage(numberPersonne);
        manager.close();
        //fin


        // on initailise de recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterMessagePersonnaliser = new AdapterMessagePersonnaliser(lisMsg, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.scrollToPosition(adapterMessagePersonnaliser.getItemCount()-1);// cessi me met directement a la fin de mon recyclerView
        recyclerView.setAdapter(adapterMessagePersonnaliser);
        //fin
    }


    public void modificationHeader(int nbre){
        if (nbre == 0){
            toolbar.setTitle(R.string.null_select);
        }else {
            toolbar.setTitle(nbre+" "+getResources().getString(R.string.selection));
        }
    }


    /**
     * cette methode permet de d'inserer dans la bd firebase les contact signaler
     */
    public void insertContactSignale(){

        DataBaseManager manager = new DataBaseManager(this);
        List<String> list = manager.recuperationContactDangereux();
        String nuberUser = manager.lectureInfoUser().get(0).getNumero();
        String codePays = manager.lectureInfoUser().get(0).getCodepays();
        manager.close();

        Date currentDate = Calendar.getInstance().getTime();
        for (int i = 0; i <list.size() ; i++) {
            if (list.contains("+")){
                SignalNumberHelper.addSignalNumber( new SignalNumber(list.get(i).replace(" ",""),nuberUser,currentDate));
            }else {
                SignalNumberHelper.addSignalNumber( new SignalNumber(("+"+codePays+list.get(i)).replace(" ",""),nuberUser,currentDate));
            }

        }

    }


}
