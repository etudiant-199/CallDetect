package com.example.calldetect.messageriePrincipale.notificationSMS;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class notificationMessage extends Application {
    public static final String CHANNEL = "channel1";
    @Override
    public void onCreate() {
        super.onCreate();
        
        createNotificationChannel();
    }


    /**
     * ici on cree juste le canna de notifiaction . et le canal n'est que pour les version O
     */
    private void createNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel  = new NotificationChannel(CHANNEL, "channel ", NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("this is channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);


        }

    }
}
