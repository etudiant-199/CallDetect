package com.example.calldetect.utils;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;

import com.example.calldetect.R;
import com.example.calldetect.activities.Home;
import com.example.calldetect.firebase_data_base_manager.SignalNumberHelper;
import com.example.calldetect.fragment.settings.SettingsApp;
import com.example.calldetect.models.SignalNumber;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import static android.content.Context.NOTIFICATION_SERVICE;

public class InterceptCall extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(final Context context, Intent intent) {
        try{
            final String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            final String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            Log.e("InterceptCall", "onReceive: phoneNumber => " + phoneNumber);

            // Check the signalisation.
            SignalNumberHelper.isSignal(phoneNumber)
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (state != null)
                                if (documentSnapshot.toObject(SignalNumber.class) != null
                                        && state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
                                    setNotification(context, phoneNumber);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("InterceptCall", "onFailure: ", e);
                        }
                    });
        }catch(Exception e) {
            Log.e("InterceptCall", "onReceive: ", e);
        }

    }

    private void setNotification(Context context, String phoneNumber) {
        Intent intention =  new Intent(context, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,
                intention, PendingIntent.FLAG_UPDATE_CURRENT);
        String title = context.getString(R.string.signalisation_for) + phoneNumber;
        String body = context.getString(R.string.has_signal_by_arna);
        NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle
                .Message(body, 10, new Person.Builder().setName(title).build());
        NotificationCompat.Builder notificationCompat = new NotificationCompat
                .Builder(context, SettingsApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setStyle(new NotificationCompat.MessagingStyle(new Person.Builder().setName(title).build())
                        .addMessage(message))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_account))
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setVibrate(new long[] {0, 1000, 500, 1000})
                .setContentIntent(pendingIntent);
        NotificationManager nm = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        assert nm != null;
        nm.notify(1, notificationCompat.build());
    }
}
