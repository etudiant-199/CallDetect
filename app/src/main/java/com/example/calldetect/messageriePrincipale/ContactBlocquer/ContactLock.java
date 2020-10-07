package com.example.calldetect.messageriePrincipale.ContactBlocquer;

public class ContactLock {
    private int id;
    private String numero;
    private String nom;


    public ContactLock(int id, String numero, String nom) {
        this.id = id;
        this.numero = numero;
        this.nom = nom;
    }

    public ContactLock(String numero, String nom) {
        this.numero = numero;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
