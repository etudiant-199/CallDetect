package com.example.calldetect.messageriePrincipale.contact;

public class ContactPhone {

    private String nom;
    private String numero;


    public ContactPhone(final String nom, String numero) {
        this.nom = nom;
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }


}
