package com.example.calldetect.messageriePrincipale;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.calldetect.database.DataBaseManager;
import com.example.calldetect.messageriePrincipale.ContactBlocquer.ContactLock;
import com.example.calldetect.messageriePrincipale.notificationSMS.serviceNotification;

import java.util.List;

public class MyReceiverSMS2 extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    String msg, numeroPhone, jour, nomOperateur="";
    int operateurid;
    private List<ContactLock> listContactLock;
    private Boolean contactBlocquer = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        // on se connecte a la base de donne pour prendre tous les contacts  blocquees
        DataBaseManager manager1 = new DataBaseManager(context);
        listContactLock = manager1.listContactBlocquer();
        manager1.close();
        //fin


        Bundle dataBundle = intent.getExtras();
        if (intent.getAction().equals(SMS_RECEIVED)) {
            if (dataBundle != null) {
                Object[] mypdus = (Object[]) dataBundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[mypdus.length];
                for (int i = 0; i < mypdus.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = dataBundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) mypdus[i], format);
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) mypdus[i]);
                    }
                    msg = messages[i].getMessageBody();
                    numeroPhone = messages[i].getOriginatingAddress();
                    jour = String.valueOf(messages[i].getTimestampMillis());
                    operateurid = messages[i].getIndexOnIcc();
                }


// si le numero qui envoie le message, apparait dans la liste des contact blocquer, on ne sauvegarde pas son message
                for (int i = 0; i < listContactLock.size(); i++) {

                    //si la taille du numero est > a 4
                    if (listContactLock.get(i).getNumero().length() > 4 && numeroPhone.replace(" ", "").length() > 4) {

                        // si le numero contient le caractere + cela veut dire qu'il est sous le format normale
                        if (listContactLock.get(i).getNumero().contains("+")) {
                            if (numeroPhone.replace(" ", "").equals(listContactLock.get(i).getNumero().replace(" ", "")) || listContactLock.get(i).getNumero().replace(" ", "").contains(numeroPhone.replace(" ", ""))) {
                                contactBlocquer = true;
                            }
                        } else {
                            if (numeroPhone.replace(" ", "").equals(listContactLock.get(i).getNumero().replace(" ", "")) || numeroPhone.replace(" ", "").contains(listContactLock.get(i).getNumero().replace(" ", ""))) {
                                contactBlocquer = true;
                            }
                        }
                    } else {
                        if (numeroPhone.replace(" ", "").equals(listContactLock.get(i).getNumero().replace(" ", ""))) {
                            contactBlocquer = true;
                        }
                    }

                }


                // si le contact est blocque, on ne  le sauveagarde pas

                if (!contactBlocquer) {


                    // on cherche le nom de la sim
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

//                        Log.d("oswaldo",String.valueOf(operateurid));
//                        nomOperateur = (String) subscriptionManager.getActiveSubscriptionInfo(operateurid).getDisplayName();

                    }
                    // end



                    Toast.makeText(context, msg + " " + numeroPhone, Toast.LENGTH_SHORT).show();
                    Log.d("oswald", "broadcast active");
                    DataBaseManager manager = new DataBaseManager(context);
                    manager.insertMessage(new ModelMessagePrinc(numeroPhone, msg, jour, 0, nomOperateur, 1, 2, 2, 2));
                    Toast.makeText(context, "insertion ok", Toast.LENGTH_SHORT).show();
                    manager.close();

// la notification  declanche ici .............

                    //on declanche le service
                    Intent intent1 = new Intent(context, serviceNotification.class);
                    intent1.putExtra("numero", numeroPhone);
                    intent1.putExtra("message", msg);
                    context.startService(intent1);
                    //fin

// fin de la notification......................




                    // c'est ici quon vera si stocker dans la base de donnee ou pas !


                    //end

                    // on declanche le broadcast dans le code java
                    Intent intentBroadcast = new Intent();
                    intentBroadcast.setAction("SMS_RECEIVED_ACTION");
                    context.sendBroadcast(intentBroadcast);

                }
            }

        }

    }
}
