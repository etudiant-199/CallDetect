package com.example.calldetect.messageriePrincipale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiverMMS extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "super okkkkkk", Toast.LENGTH_SHORT).show();
    }
}
