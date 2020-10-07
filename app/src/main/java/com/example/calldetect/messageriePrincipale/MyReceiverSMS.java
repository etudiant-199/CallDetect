package com.example.calldetect.messageriePrincipale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.calldetect.database.DataBaseManager;

public class MyReceiverSMS extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    String msg, numeroPhone, jour, operateur;
    @Override
    public void onReceive(Context context, Intent intent) {


            Bundle dataBundle = intent.getExtras();

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
                    operateur = messages[i].getServiceCenterAddress();
                }
                Toast.makeText(context, msg + " " + numeroPhone, Toast.LENGTH_SHORT).show();
                Log.d("oswald", "broadcast active");
                DataBaseManager manager = new DataBaseManager(context);
                manager.insertMessage(new ModelMessagePrinc(numeroPhone, msg, jour, 0, operateur, 1, 2, 2, 2));
                Toast.makeText(context, "insertion ok", Toast.LENGTH_SHORT).show();
                manager.close();
                // c'est ici quon vera si stocker dans la base de donnee ou pas !


                //end

                // on declanche le broadcast dans le code java
                Intent intentBroadcast = new Intent();
                intentBroadcast.setAction("SMS_RECEIVED_ACTION");
                context.sendBroadcast(intentBroadcast);






            }

    }
}
