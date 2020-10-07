package com.example.calldetect.messageriePrincipale.notificationSMS;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.calldetect.database.DataBaseManager;

public class serviceNotificationMasquerLue extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String numero = intent.getExtras().getString("numero");
        DataBaseManager manager = new DataBaseManager(this);
        manager.UpdateMsgLue(numero);
        manager.close();

        return START_STICKY;
    }
}
