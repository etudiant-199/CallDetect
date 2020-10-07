package com.example.calldetect.firebase_data_base_manager;

import com.example.calldetect.models.SignalNumber;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignalNumberHelper {

    private static CollectionReference getCollectionRef(){
        return FirebaseFirestore.getInstance().collection("SignalNumber");
    }

    public static Task<Void> addSignalNumber (SignalNumber signalNumber) {
        return getCollectionRef().document(signalNumber.getNumber()).set(signalNumber);
    }

    public static Task<Void> removeToSignalNumber (String number) {
        return getCollectionRef().document(number).delete();
    }

    public static Task<DocumentSnapshot> isSignal (String number) {
        return getCollectionRef().document(number).get();
    }


    public  static  Task<DocumentSnapshot> takeSinalNumber(String number){
        return getCollectionRef().document(number).get();
    }
}
