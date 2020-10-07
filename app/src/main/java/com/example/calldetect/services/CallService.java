package com.example.calldetect.services;

import android.annotation.SuppressLint;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;

import com.example.calldetect.utils.Utils;

@SuppressLint("NewApi")
public class CallService extends ConnectionService {

    public CallService() {
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount,
                                                 ConnectionRequest request) {
        Utils.setToastMessage(this, "The service method on create out going connection.");
        return super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request);
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount,
                                                 ConnectionRequest request) {
        Utils.setToastMessage(this, "The service method on create out going connection failed.");
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount,
                                                 ConnectionRequest request) {
        Utils.setToastMessage(this, "The service method on create incoming Connection.");
        return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request);
    }

    @Override
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount,
                                                 ConnectionRequest request) {
        Utils.setToastMessage(this, "The service method on create incoming Connection failed.");
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
    }
}
