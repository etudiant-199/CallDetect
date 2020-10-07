package com.example.calldetect.firebase_data_base_manager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.calldetect.activities.Home;
import com.example.calldetect.R;
import com.example.calldetect.models.Users_Simple;
import com.example.calldetect.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActionAboutUsers {

    private final static String COLLECTION_NAME_SIMPLE = "Utilisateurs_Simple";

    /**
     * simple users
     */
    private static CollectionReference getSimpleCollectionSimple(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_SIMPLE);
    }

    public static Task<Void> addSimpleUsers(Users_Simple users_simple){
        return getSimpleCollectionSimple().document(users_simple.getLogin()).set(users_simple);
    }

    /**
     * Function to update an user.
     */
    public static Task<Void> updateUser(Users_Simple users_simple) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("login", users_simple.getLogin());
        userMap.put("nom", users_simple.getName());
        userMap.put("prenom", users_simple.getPrenom());
        userMap.put("profession", users_simple.getProfession());
        userMap.put("otherNumber", users_simple.getOtherNumber());
        userMap.put("email", users_simple.getEmail());
        userMap.put("profile", users_simple.getProfile());
        return getSimpleCollectionSimple().document(users_simple.getLogin()).update(userMap);
    }

    public static Task<DocumentSnapshot> getParticularSimpleUsers(String number){
        return getSimpleCollectionSimple()
                .document(number)
                .get();
    }


    public static void setSimpleUsers(final Context context, final Users_Simple users){
        final ProgressDialog progressDialog = ProgressDialog.show(context, null, context.getString(R.string.wait));
        final String number = users.getLogin();

        getParticularSimpleUsers(number).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Utils.setToastMessage(context, context.getString(R.string.compte_existe_deja));
                }
                else {
                    Intent intent = new Intent(context, Home.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            }
        });
    }

}
