package com.example.calldetect.messageriePrincipale.notificationSMS;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.calldetect.R;
import com.example.calldetect.database.DataBaseManager;
import com.example.calldetect.messageriePrincipale.ActivityMessaferiePrincipale;
import com.example.calldetect.messageriePrincipale.ModelMessagePrinc;
import com.example.calldetect.messageriePrincipale.contact.ContactPhone;
import com.example.calldetect.messageriePrincipale.messageCustomiser.Activity_message_personaliser;
import com.example.calldetect.models.ModelContacts;

import java.util.ArrayList;
import java.util.List;

import static com.example.calldetect.messageriePrincipale.notificationSMS.notificationMessage.CHANNEL;

public class serviceNotification extends Service {

    private NotificationManagerCompat notificationManager;
    private String numero;
    private String messages, nomPersonne;
    private List<ModelContacts> listContact;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStart(intent, startId);

        // on obtient par l'intent le numero et le message

        numero = intent.getExtras().getString("numero");
        messages = intent.getExtras().getString("message");

        //fin


        //on initialise la list de contact
        listContact = getContact();
        //fin


        notificationManager = NotificationManagerCompat.from(this);


        // on prepare l'activiter qui sera demarer au clic
        Intent activityIntent = new Intent(this, ActivityMessaferiePrincipale.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this,0,activityIntent,0);
        //fin



        //on prepare l'activite qui sera l'ancer quant on va cliquer sur le bouton repondre  de la notification
        Intent serviceIntentRepondreSMS = new Intent(this, Activity_message_personaliser.class);
        serviceIntentRepondreSMS.putExtra("number", numero);
        PendingIntent pendingIntentRepondreSMS = PendingIntent.getActivity(this, 0, serviceIntentRepondreSMS, 0);


        //on prepare ce qui va se passer quant on cliquera sur masquer coe lue
        Intent serviceIntentmasquerLue = new Intent( this, serviceNotificationMasquerLue.class);
        serviceIntentmasquerLue.putExtra("numero", numero);
        PendingIntent pendingIntentMasquerLue = PendingIntent.getService(this, 0, serviceIntentmasquerLue, 0);


        //on interroge la base de donnees pour savoir le nombre de nouveau message
        List<ModelMessagePrinc> listmsg ;
        DataBaseManager manager = new DataBaseManager(this);
        listmsg = manager.lectureMessageNonLu();
        manager.close();
        //fin


        // si ma  liste n'a qu'un seulle message non lue, on  fait une notification classique
        if (listmsg.size()==1) {


            if (nomContact(listmsg.get(0).getNumero())==null){
                nomPersonne = listmsg.get(0).getNumero();
            }else {
                nomPersonne = nomContact(listmsg.get(0).getNumero());
            }


            Notification notification = new NotificationCompat.Builder(this, CHANNEL)
                    .setSmallIcon(R.drawable.message)
                    .setContentTitle(nomPersonne)
                    .setContentText(listmsg.get(0).getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(listmsg.get(0).getMessage()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setColor(getResources().getColor(R.color.colorAccentLight))
                    .setContentIntent(pendingIntent)
                    .setGroup("Group_Notification")
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .addAction(R.drawable.message, getString(R.string.repondre), pendingIntentRepondreSMS)
                    .addAction(R.drawable.ic_done_all, getString(R.string.marquer_lu), pendingIntentMasquerLue)
                    .build();

            notificationManager.notify(1, notification);

        }else {


            Notification notification = null;
            for (int i = 0; i < listmsg.size() ; i++) {


                // on recherche le nom du numero pour l'associer a la notification
                if (nomContact(listmsg.get(i).getNumero())==null){
                    nomPersonne = listmsg.get(i).getNumero();
                }else {
                    nomPersonne = nomContact(listmsg.get(i).getNumero());
                }
                //end


                notification = new NotificationCompat.Builder(this, CHANNEL)
                        .setSmallIcon(R.drawable.message)
                        .setContentTitle(nomPersonne)
                        .setContentText(listmsg.get(i).getMessage())
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setColor(getResources().getColor(R.color.colorAccentLight))
                        .setContentIntent(pendingIntent)
                        .setGroup("Group_Notification")
                        .build();
                notificationManager.notify(i+2 , notification);
            }


            // list de message sous forme de ligne
            NotificationCompat.InboxStyle lineStyle = new NotificationCompat.InboxStyle();
            for (int i = 0; i < listmsg.size() ; i++) {

                // on recherche le nom du numero pour l'associer a la notification
                if (nomContact(listmsg.get(i).getNumero())==null){
                    nomPersonne = listmsg.get(i).getNumero();
                }else {
                    nomPersonne = nomContact(listmsg.get(i).getNumero());
                }
                //end

                lineStyle.addLine(nomPersonne + " " + listmsg.get(i).getMessage());
            }
            lineStyle.setBigContentTitle(listmsg.size() + " " + "nouveaux messages");
            lineStyle.setSummaryText("CallDetect");
            //fin


            //notification groupée commence ici
            Notification summaryNotification = new NotificationCompat.Builder(this, CHANNEL)
                    .setSmallIcon(R.drawable.message)
                    .setStyle(lineStyle)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setGroup("Group_Notification")
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setContentIntent(pendingIntent)
                    .setGroupSummary(true)
                    .build();
            // fin

            notificationManager.notify(listmsg.size()+2, summaryNotification);
        }
        return START_NOT_STICKY;
    }


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
        for (int i =0; i<list.size(); i++){
            if (list.get(i).getContact().equals(numero)){
                return list.get(i).getName();
            }
        }
        return null;
    }


}
