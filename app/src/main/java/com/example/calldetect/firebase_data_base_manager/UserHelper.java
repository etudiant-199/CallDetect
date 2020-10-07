package com.example.calldetect.firebase_data_base_manager;

import com.example.calldetect.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Classe qui permet de gérer les utilisateurs de la base de données sur firebase.
 */
public class UserHelper {

    public static final String COLLECTION_NAME = "Users";

    public static CollectionReference getCollectionRef() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /**
     * Fonction qui permet d'ajouter un utilisateur simple dans firebase.
     * @param user Utilisateur.
     */
    public static Task<Void> addSimpleUser (User user) {
        return getCollectionRef().document(user.getId()).set(user);
    }

    /**
     * Fonction qui permet de certifier un utilisateur.
     * @param user Information de l'utilisateur.
     */
    public static Task<Void> certifyUser (User user) {
        return getCollectionRef().document(user.getId()).set(user);
    }

    /**
     * Fonction qui permet de récuperer un utilisateur de firebase.
     * @param user_id Identifiant de l'utilisateur.
     */
    public static Task<DocumentSnapshot> getUserById(String user_id) {
        return getCollectionRef().document(user_id).get();
    }

    public static Task<Void> updateUser(User user) {
        return getCollectionRef().document(user.getId()).set(user);
    }
}
