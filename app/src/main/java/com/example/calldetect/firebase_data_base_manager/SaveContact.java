package com.example.calldetect.firebase_data_base_manager;

import com.example.calldetect.messageriePrincipale.contact.ContactPhone;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SaveContact {
    public static final String COLLECTION_NAME = "contact";


    /**
     * cette methode permet de designer la collection dans la quelle on doit travailler
     * @return
     */
    public static CollectionReference getCollectionRef(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }


    public static Task<Void> addContact(ContactPhone contacts){
        return getCollectionRef().document(contacts.getNumero()).set(contacts);
    }

    public static Task<DocumentSnapshot> takeConact(String numero){
        return getCollectionRef().document(numero).get();
    }


}
