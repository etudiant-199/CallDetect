package com.example.calldetect.messageriePrincipale;

public class ModelMessagePrinc {
    private String numero;
    private String nom;
    private String message;
    private String jour;
    private String id;
    private String id_thread;
    private int read;
    private int image;
    private String firsrLetter;
    private boolean isCheket;
    private String operateurTelephonique;
    private int received ;

    private int msgEnvoye;
    private  int msgBienRecue;
    private int EchecEnvoie;


    public ModelMessagePrinc(String numero, String nom, String message, String jour, int read, int image, String firsrLetter, boolean isCheket, String id) {
        this.numero = numero;
        this.nom = nom;
        this.message = message;
        this.jour = jour;
        this.read = read;
        this.image = image;
        this.firsrLetter = firsrLetter;
        this.isCheket = isCheket;
        this.id = id;
    }


    // sans id, pour sauvegarder le mssage
    public ModelMessagePrinc(String numero, String message, String jour, int read, String operateurTelephonique, int received, int msgBienRecue, int msgEnvoye, int echecEnvoie) {
        this.numero = numero;
        this.message = message;
        this.jour = jour;
        this.read = read;
        this.operateurTelephonique = operateurTelephonique;
        this.received = received;
        this.msgBienRecue = msgBienRecue;
        this.msgEnvoye = msgEnvoye;
        this.EchecEnvoie = echecEnvoie;
    }

    //avec id, pour la lecture du message
    public ModelMessagePrinc(String id, String numero, String message, String jour, int read, String operateurTelephonique, int received, int msgBienRecue, int msgEnvoye, int echecEnvoie) {
        this.id = id;
        this.numero = numero;
        this.message = message;
        this.jour = jour;
        this.read = read;
        this.operateurTelephonique = operateurTelephonique;
        this.received = received;
        this.msgBienRecue = msgBienRecue;
        this.msgEnvoye = msgEnvoye;
        this.EchecEnvoie = echecEnvoie;
    }

    public ModelMessagePrinc(String numero) {
        this.numero = numero;
    }

    public int getMsgEnvoye() {
        return msgEnvoye;
    }

    public void setMsgEnvoye(int msgEnvoye) {
        this.msgEnvoye = msgEnvoye;
    }

    public int getMsgBienRecue() {
        return msgBienRecue;
    }

    public void setMsgBienRecue(int msgBienRecue) {
        this.msgBienRecue = msgBienRecue;
    }

    public int getEchecEnvoie() {
        return EchecEnvoie;
    }

    public void setEchecEnvoie(int echecEnvoie) {
        EchecEnvoie = echecEnvoie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getOperateurTelephonique() {
        return operateurTelephonique;
    }

    public void setOperateurTelephonique(String operateurTelephonique) {
        this.operateurTelephonique = operateurTelephonique;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_thread() {
        return id_thread;
    }

    public void setId_thread(String id_thread) {
        this.id_thread = id_thread;
    }

    public boolean isCheket() {
        return isCheket;
    }

    public void setCheket(boolean cheket) {
        isCheket = cheket;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getFirsrLetter() {
        return firsrLetter;
    }

    public void setFirsrLetter(String firsrLetter) {
        this.firsrLetter = firsrLetter;
    }



    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }
}
