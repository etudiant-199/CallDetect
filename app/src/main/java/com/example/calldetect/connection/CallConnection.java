package com.example.calldetect.connection;

import android.annotation.SuppressLint;
import android.telecom.CallAudioState;
import android.telecom.Connection;

@SuppressLint("NewApi")
public class CallConnection extends Connection {
    @Override
    public void onShowIncomingCallUi() {
        super.onShowIncomingCallUi();

    }

    @Override
    public void onCallAudioStateChanged(CallAudioState state) {
        super.onCallAudioStateChanged(state);

    }

    @Override
    public void onHold() {
        super.onHold();
    }

    @Override
    public void onUnhold() {
        super.onUnhold();

    }

    @Override
    public void onAnswer() {
        super.onAnswer();

    }

    @Override
    public void onReject() {
        super.onReject();

    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();

    }

}
