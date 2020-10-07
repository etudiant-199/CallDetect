package com.example.calldetect.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.calldetect.messageriePrincipale.Brouillon.messageBrouillon;
import com.example.calldetect.messageriePrincipale.ContactBlocquer.ContactLock;
import com.example.calldetect.messageriePrincipale.ModelMessagePrinc;
import com.example.calldetect.messageriePrincipale.contact.ContactPhone;
import com.example.calldetect.messageriePrincipale.infoUser.InfoUser;
import com.example.calldetect.utilisateur.Utilisateur;

import java.util.ArrayList;
import java.util.List;

public class DataBaseManager extends SQLiteOpenHelper {

    private static String nomBd = "calldetect.db";
    private static int numVersion = 2;

    public DataBaseManager( Context context) {
        super(context, nomBd, null, numVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String user = "create table utilisateur (" +
                "numeroTelephone TEXT not null primary key,"+
                "nomprenom TEXT not null,"+
                "profession TEXT not null,"+
                "pays TEXT not null,"+
                "typecompte TEXT not null,"+
                "photoProfil TEXT not null,"+
                "email TEXT not null,"+
                "sexe TEXT not null,"+
                "dateNaissance Text not null,"+
                "localisation Text not null"+
                ")";

        String archive = " create table archive("+
                "numeroTelephone Text not null primary key,"+
                "nomprenom TEXT not null,"+
                "profession TEXT not null,"+
                "pays TEXT not null,"+
                "typecompte TEXT not null,"+
                "photoProfil TEXT not null,"+
                "email TEXT not null,"+
                "sexe TEXT not null,"+
                "dateNaissance TEXT not null,"+
                "localisation TEXT not null"+
                ")";

        String message = "create table message (" +
                "id INTEGER not null primary key autoincrement,"+
                "adresse TEXT not null,"+
                "body TEXT not null,"+
                "read Integer not null,"+
                "heure TEXT not null,"+
                "operateurTelephonique TEXT not null,"+
                "received Integer not null,"+
                "msgBienRecue Integer not null,"+
                "msgEnvoye Integer not null,"+
                "echecEnvoie Integer not null"+
                ")";


        String contactBlocquer = "create table contactLock("+
                "id INTEGER not null primary key autoincrement,"+
                "numero TEXT not null,"+
                "nom TEXT not null "+
                ")";

        String contactSignaler  = "create table contactDangereux("+
                "id INTEGER not null primary key autoincrement,"+
                "numero TEXT not  null"+
                ")";

        String brouillon = "create table brouillonBD("+
                "id INTEGER not null primary key autoincrement,"+
                "numero TEXT not null,"+
                "message TEXT not null"+
                ")";

        String contact = "create table contact("+
                "id INTEGER not null primary key autoincrement,"+
                "nom TEXT not null,"+
                "numero TEXT not null"+
                ")";


        String infoUser = "create table infouser("+
                "id INTEGER not null primary key autoincrement,"+
                "numero TEXT not null,"+
                "pays TEXT not null,"+
                "codepays TEXT not null"+
                ")";

        db.execSQL(user);
        db.execSQL(message);
        db.execSQL(contactBlocquer);
        db.execSQL(contactSignaler);
        db.execSQL(brouillon);
        db.execSQL(archive);
        db.execSQL(contact);
        db.execSQL(infoUser);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * cette methode permet d'inserer un noueau message dans la base de donnee
     * @param a ModelMessagePrinc qui  a le message  et tous les ellements qui tourne autour comme l'heure , la date ...
     */
    public void insertMessage(ModelMessagePrinc a){
        ContentValues donnee = new ContentValues();
        donnee.put("adresse", a.getNumero());
        donnee.put("body", a.getMessage());
        donnee.put("read", a.getRead());
        donnee.put("heure", a.getJour());
        donnee.put("operateurTelephonique", a.getOperateurTelephonique());
        donnee.put("received", a.getReceived());
        donnee.put("msgBienRecue", a.getMsgBienRecue());
        donnee.put("msgEnvoye", a.getMsgEnvoye());
        donnee.put("echecEnvoie", a.getEchecEnvoie());
        this.getWritableDatabase().insert("message", null, donnee);
    }

    public void insertArchive(ModelMessagePrinc a){
        ContentValues donnee = new ContentValues();
        donnee.put("adresse", a.getNumero());
        donnee.put("body", a.getMessage());
        donnee.put("read", a.getRead());
        donnee.put("heure", a.getJour());
        donnee.put("operateurTelephonique", a.getOperateurTelephonique());
        donnee.put("received", a.getReceived());
        donnee.put("msgBienRecue", a.getMsgBienRecue());
        donnee.put("msgEnvoye", a.getMsgEnvoye());
        donnee.put("echecEnvoie", a.getEchecEnvoie());
        this.getWritableDatabase().insert("message", null, donnee);
    }





    public void insertContact(ContactPhone contact){
        ContentValues donnee = new ContentValues();
        donnee.put("nom",contact.getNom() );
        donnee.put("numero", contact.getNumero());
        this.getWritableDatabase().insert("contact", null , donnee);
    }


    public void insertUserInfo(InfoUser infoUser){
        ContentValues donnee = new ContentValues();
        donnee.put("numero", infoUser.getNumero());
        donnee.put("pays", infoUser.getPays());
        donnee.put("codepays", infoUser.getCodepays());
        this.getWritableDatabase().insert("infouser", null, donnee);
    }


    /**
     * ici on lit les contact de notre base de donnee sqlite !
     * @return
     */
    public List<ContactPhone> lectureContact(){

        List<ContactPhone> list = new ArrayList<>();

        String[] projection = { "nom", "numero"};
        Cursor cursor = this.getReadableDatabase().query("contact",projection,null,null,null,null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            list.add(new ContactPhone(cursor.getString(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        return list;
    }



    public List<InfoUser> lectureInfoUser(){
        List<InfoUser> list = new ArrayList<>();
        String[] projection = {"id", "numero", "pays","codepays"};
        Cursor cursor = this.getReadableDatabase().query("infouser", projection, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            list.add(new InfoUser(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            cursor.moveToNext();
        }
        return list;
    }



    /**
     * cette methode permet d'ajouter un nouveau contact bloquer a la base de donnee
     * @param contactLock
     */
    public void insertContactBlocquer(ContactLock contactLock){
        ContentValues donnee = new ContentValues();
        donnee.put("numero", contactLock.getNumero());
        donnee.put("nom", contactLock.getNom());
        this.getWritableDatabase().insert("contactLock", null, donnee);
    }


    /**
     * cette methode permet de sauvegarder les contact dangereux () ou encore signaler le contact !
     * @param contact
     */
    public void insertContactDangereux(String contact){
        ContentValues donnee = new ContentValues();
        donnee.put("numero", contact);
        this.getWritableDatabase().insert("contactDangereux",null, donnee);
    }

    /**
     * cette methode permet de sauvegarder un msg dans le brouillon
     * @param brouillon
     */
    public void insertBrouillon (messageBrouillon brouillon){
        ContentValues donnee = new ContentValues();
        donnee.put("numero", brouillon.getNumeroBrouillon());
        donnee.put("message", brouillon.getMessageBrouillon());
        this.getWritableDatabase().insert("brouillonBD",null, donnee);
    }

    /**
     *cette methode permet de selectionner tous les messages du brouillon
     * @return
     */
    public List<messageBrouillon> selectMessageBrouillon(){

        List<messageBrouillon> listBrouillon = new ArrayList<>();
        String[] projection = {"id", "numero", "message"};
        Cursor cursor = this.getReadableDatabase().query("brouillonBD", projection, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            listBrouillon.add(new messageBrouillon(cursor.getInt(0), cursor.getString(1), cursor.getString(2) ) ) ;
            cursor.moveToNext();
        }
        return listBrouillon;
    }





    /**
     * cette methode modifira le msg dans la bse de donnee pour un contact donnee
     */
    public  void updateMsgeBrouillon(messageBrouillon brouillon){
        ContentValues donnee = new ContentValues();
        donnee.put("message", brouillon.getMessageBrouillon());

        this.getWritableDatabase().update("brouillonBD",donnee,"numero" + " = ?", new String[]{brouillon.getNumeroBrouillon()});
    }


    /**
     * message pour supprimer un message dans le broillon
     * @param numero
     */
    public void suppressionBrouillon(String numero){
        this.getWritableDatabase().delete("brouillonBD", "numero" + " = ?", new String[] {String.valueOf(numero)});
    }


    /**
     * methode pour recuperer les contact malveillants
     * @return
     */
    public List<String> recuperationContactDangereux(){
        List<String> list = new ArrayList<>();
        String[] projection = {"id", "numero"};
        Cursor cursor = this.getReadableDatabase().query("contactDangereux",projection,null,null,null,null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            list.add(cursor.getString(1));
            cursor.moveToNext();
        }
        return list;
    }


    /**
     * cette methode permet d'enlever de la base de donnee un contact blocquer
     * @param numero
     */
    public void suppresionContactBlocquer(String numero){

        this.getWritableDatabase().delete("contactLock", "numero" + " = ?", new String[] {String.valueOf(numero)});

    }

    /**
     * cette methode permet de supprimer une liste de message a partir de leur id
     * @param listMsg
     */
    public void suppressionQuelqueSms(List<ModelMessagePrinc> listMsg){
        for (int i = 0; i <listMsg.size() ; i++) {
            this.getWritableDatabase().delete("message","id"+" = ?", new String[]{listMsg.get(i).getId()});
        }
    }



    /**
     * cette methode renvoie la liste des contact blocque
     * @param
     * @return
     */
    public List<ContactLock> listContactBlocquer(){
        List<ContactLock> contactLocks  = new ArrayList<>();
        String projection[] = {"id", "numero", "nom"};
        Cursor cursor = this.getReadableDatabase().query("contactLock", projection, null, null, null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            ContactLock contact = new ContactLock(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            contactLocks.add(contact);
            cursor.moveToNext();
        }
        return contactLocks;
    }


    /**
     * cette methode est appeller pour passer le champs echecEnvoie a 1 pour un message livre a une heure donnee si se message n'a pas
     * ete envoyer avec success
     * @param time
     */
    public void UpdateEchecMsg(String time){
        ContentValues donnee = new ContentValues();
        donnee.put("heure", time);
        donnee.put("echecEnvoie", 1);

        this.getWritableDatabase().update("message",donnee,"heure" + " = ?", new String[]{time});
    }

    /**
     *cette methode est appeller pour passer le champs msgEnvoye a 1 pour un message livre a une heure donnee si se message a ete
     *  envoyer avec success
     * @param time
     */
    public void UpdateMsgEnvoye(String time){
        ContentValues donnee = new ContentValues();
        donnee.put("heure", time);
        donnee.put("msgEnvoye", 1);

        this.getWritableDatabase().update("message",donnee,"heure" + " = ?", new String[]{time});
    }

    /**
     *cette methode est appeller pour passer le champs msgBienRecue a 1 pour un message livre a une heure donnee si se message a
     * ete envoyer avec success  et le destinataire a bien recue .
     * @param time
     */
    public void UpdateMsgBienRecue(String time){
        ContentValues donnee = new ContentValues();
        donnee.put("heure", time);
        donnee.put("msgBienRecue", 1);

        this.getWritableDatabase().update("message",donnee,"heure" + " = ?", new String[]{time});
    }



    /**
     * cette methode permet de passer le read a 1 pour dire que le messsage est lue . ici on passe tous les message dont le read est a 0 a 1 sur le nume
     * ro prit en parametre
     * @param numero
     */
    public void UpdateMsgLue(String numero){
        List<ModelMessagePrinc> listMessage = new ArrayList<>();
        String projection[] = {"id ,adresse", "body","heure","read","operateurTelephonique","received","msgBienRecue","msgEnvoye","echecEnvoie"};
        Cursor cursor = this.getReadableDatabase().query("message", projection, null, null, null, null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){

            ModelMessagePrinc messagePrinc = new ModelMessagePrinc(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4), cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getInt(8),cursor.getInt(9));

            //si la taille du numero est > a 4
            if(cursor.getString(1).replace(" ","").length()>4 && numero.replace(" ","").length()>4) {

                // si le numero contient le caractere + cela veut dire qu'il est sous le format normale
                if (cursor.getString(1).contains("+")) {
                    if (numero.replace(" ", "").equals(cursor.getString(1).replace(" ", "")) || cursor.getString(1).replace(" ", "").contains(numero.replace(" ", ""))) {

                        if (cursor.getInt(4)==0) {
                            ContentValues donnee = new ContentValues();
                            donnee.put("read", 1);
                            this.getWritableDatabase().update("message", donnee, "id" + "= ?", new String[]{cursor.getString(0)});
                        }
                    }

                } else {
                    if (numero.replace(" ", "").equals(cursor.getString(1).replace(" ", "")) || numero.replace(" ", "").contains(cursor.getString(1).replace(" ", ""))) {

                        if (cursor.getInt(4)==0) {
                            ContentValues donnee = new ContentValues();
                            donnee.put("read", 1);
                            this.getWritableDatabase().update("message", donnee, "id" + "= ?", new String[]{cursor.getString(0)});
                        }

                    }
                }
            }else {
                if (numero.replace(" ", "").equals(cursor.getString(1).replace(" ",""))){

                    if (cursor.getInt(4)==0) {
                        ContentValues donnee = new ContentValues();
                        donnee.put("read", 1);
                        this.getWritableDatabase().update("message", donnee, "id" + "= ?", new String[]{cursor.getString(0)});
                    }

                }
            }

            cursor.moveToNext();
        }
    }




    /**
     * cette methode permet de passer le read a 1 pour dire que le messsage est lue . ici on passe tous les message dont le read est a 0 a 1  sur tous les numeros
     * @param
     */
    public void UpdateMsgLue(){
        String projection[] = {"id ,adresse", "body","heure","read","operateurTelephonique","received","msgBienRecue","msgEnvoye","echecEnvoie"};
        Cursor cursor = this.getReadableDatabase().query("message", projection, null, null, null, null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){

            if (cursor.getInt(4)==0) {
                ContentValues donnee = new ContentValues();
                donnee.put("read", 1);
                this.getWritableDatabase().update("message", donnee, "id" + "= ?", new String[]{cursor.getString(0)});
            }
            cursor.moveToNext();
        }
    }

    /**
     * cette methode supprime ume liste de message passe en parametre dans la base de donnee
     * @param listnumero
     */
    public void suppressionDiscussion(List<ModelMessagePrinc> listnumero){

        for (int i = 0; i<listnumero.size(); i++) {

            String projection[] = {"id ,adresse", "body", "heure", "read", "operateurTelephonique", "received",  "msgBienRecue","msgEnvoye", "echecEnvoie"};
            Cursor cursor = this.getReadableDatabase().query("message", projection, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                ModelMessagePrinc messagePrinc = new ModelMessagePrinc(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9));

                // si c'est une discussion de groupe groupe
                if (listnumero.get(i).getNumero().split(",").length>=2){

                    if (listnumero.get(i).getNumero().replace(" ", "").equals(cursor.getString(1).replace(" ", "")) ) {

                        this.getWritableDatabase().delete("message", "id" + " = ?", new String[] {String.valueOf(messagePrinc.getId())});

                    }

                }else {


                    //si la taille du numero est > a 4
                    if (cursor.getString(1).replace(" ", "").length() > 4 && listnumero.get(i).getNumero().replace(" ", "").length() > 4) {

                        // si le numero contient le caractere + cela veut dire qu'il est sous le format normale
                        if (cursor.getString(1).contains("+")) {
                            if (listnumero.get(i).getNumero().replace(" ", "").equals(cursor.getString(1).replace(" ", "")) || cursor.getString(1).replace(" ", "").contains(listnumero.get(i).getNumero().replace(" ", ""))) {

                                this.getWritableDatabase().delete("message", "id" + " = ?", new String[]{String.valueOf(messagePrinc.getId())});

                            }
                        } else {
                            if (listnumero.get(i).getNumero().replace(" ", "").equals(cursor.getString(1).replace(" ", "")) || listnumero.get(i).getNumero().replace(" ", "").contains(cursor.getString(1).replace(" ", ""))) {
                                this.getWritableDatabase().delete("message", "id" + " = ?", new String[]{String.valueOf(messagePrinc.getId())});
                            }
                        }
                    } else {
                        if (listnumero.get(i).getNumero().replace(" ", "").equals(cursor.getString(1).replace(" ", ""))) {
                            this.getWritableDatabase().delete("message", "id" + " = ?", new String[]{String.valueOf(messagePrinc.getId())});
                        }
                    }
                }

                cursor.moveToNext();
            }
        }

    }

    /**
     * cette methode nous permet de lire tous les message de la  base de donnee
     * @return
     */
    public List<ModelMessagePrinc> lectureMessage(){
        List<ModelMessagePrinc> listMessage = new ArrayList<>();
        String projection[] = {"id ,adresse", "body","heure","read","operateurTelephonique","received","msgBienRecue","msgEnvoye","echecEnvoie"};
        Cursor cursor = this.getReadableDatabase().query("message", projection, null, null, null, null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            ModelMessagePrinc messagePrinc = new ModelMessagePrinc(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4), cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getInt(8),cursor.getInt(9));
            listMessage.add(messagePrinc);
            cursor.moveToNext();
        }
        return listMessage;
    }


    /**
     * on lie tous les messages le read est 0 ; c'est a dire tous les messages dont on n'a pas encore lue
     * @return
     */
    public List<ModelMessagePrinc> lectureMessageNonLu(){
        List<ModelMessagePrinc> listMessage = new ArrayList<>();
        String projection[] = {"id ,adresse", "body","heure","read","operateurTelephonique","received","msgBienRecue","msgEnvoye","echecEnvoie"};
        Cursor cursor = this.getReadableDatabase().query("message", projection, null, null, null, null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            if (cursor.getInt(4)==0) {
                ModelMessagePrinc messagePrinc = new ModelMessagePrinc(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9));
                listMessage.add(messagePrinc);
            }
            cursor.moveToNext();
        }
        return listMessage;
    }


    /**
     * ici on lie les message particulier d'un numero de telephone dont on a  besoin.
     * @param numero
     * @return
     */
    public List<ModelMessagePrinc> lectureMessage(String numero){
        List<ModelMessagePrinc> listMessage = new ArrayList<>();
        String projection[] = {"id ,adresse", "body","heure","read","operateurTelephonique","received","msgBienRecue","msgEnvoye","echecEnvoie"};
        Cursor cursor = this.getReadableDatabase().query("message", projection, null, null, null, null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){

            ModelMessagePrinc messagePrinc = new ModelMessagePrinc(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4), cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getInt(8),cursor.getInt(9));

            // si c'est un groupe alors ;

            if (numero.split(",").length>=2){

                if (cursor.getString(1).replace(" ","").equals(numero.replace(" ",""))){

                    listMessage.add(messagePrinc);

                }


            }else {


                //si la taille du numero est > a 4
                if (cursor.getString(1).replace(" ", "").length() > 4 && numero.replace(" ", "").length() > 4) {

                    // si le numero contient le caractere + cela veut dire qu'il est sous le format normale
                    if (cursor.getString(1).contains("+")) {
                        if (numero.replace(" ", "").equals(cursor.getString(1).replace(" ", "")) || cursor.getString(1).replace(" ", "").contains(numero.replace(" ", ""))) {
                            listMessage.add(messagePrinc);
                        }
                    } else {
                        if (numero.replace(" ", "").equals(cursor.getString(1).replace(" ", "")) || numero.replace(" ", "").contains(cursor.getString(1).replace(" ", ""))) {
                            listMessage.add(messagePrinc);
                        }
                    }
                } else {
                    if (numero.replace(" ", "").equals(cursor.getString(1).replace(" ", ""))) {
                        listMessage.add(messagePrinc);
                    }
                }

            }
            cursor.moveToNext();
        }
        return listMessage;
    }



    // methode pour les utilisateurs
    public void insertUser(Utilisateur a){
        ContentValues donnee = new ContentValues();
        donnee.put("numeroTelephone", a.getNumero());
        donnee.put("nomprenom", a.getNomPrenom());
        donnee.put("profession", a.getProffession());
        donnee.put("pays", a.getPays());
        donnee.put("typecompte", a.getTypeCompte());
        donnee.put("photoProfil", a.getProfil());
        donnee.put("email", a.getEmail());
        donnee.put("sexe",a.getSexe());
        donnee.put("dateNaissance", a.getDateNaissance());
        donnee.put("localisation", a.getLocalisation());
        this.getWritableDatabase().insert("utilisateur",null,donnee);
    }


    public List<Utilisateur> lectureUtilisateur(){
        List<Utilisateur> liste = new ArrayList<>();
        String[] projection = {"numeroTelephone", "nomprenom","pays","profession","typecompte","photoProfil","email","sexe","dateNaissance","localisation"};
        Cursor cursor = this.getReadableDatabase().query("utilisateur", projection, null, null, null, null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Utilisateur utilisateur = new Utilisateur(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4), cursor.getString(5), cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9));
            liste.add(utilisateur);
            cursor.moveToNext();
        }
        cursor.close();
        return liste;
    }

    public void UpdateUtilisateur(Utilisateur a){
        ContentValues donnee = new ContentValues();
        donnee.put("numeroTelephone", a.getNumero());
        donnee.put("nomprenom", a.getNomPrenom());
        donnee.put("profession", a.getProffession());
        donnee.put("pays", a.getPays());
        donnee.put("typecompte", a.getTypeCompte());
        donnee.put("photoProfil", a.getProfil());
        donnee.put("email", a.getEmail());
        donnee.put("sexe",a.getSexe());
        donnee.put("dateNaissance", a.getDateNaissance());
        donnee.put("localisation", a.getLocalisation());
        this.getWritableDatabase().update("utilisateur",donnee,"numeroTelephone" + " = ?", new String[]{String.valueOf(a.getNumero())});
    }

}

