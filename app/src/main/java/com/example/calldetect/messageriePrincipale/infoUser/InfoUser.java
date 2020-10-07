package com.example.calldetect.messageriePrincipale.infoUser;

public class InfoUser {
    private int id ;
    private  String numero;
    private String pays;
    private  String codepays;


    public InfoUser(int id, String numero, String pays, String codepays) {
        this.id = id;
        this.numero = numero;
        this.pays = pays;
        this.codepays = codepays;
    }

    public InfoUser(String numero, String pays, String codepays) {
        this.numero = numero;
        this.pays = pays;
        this.codepays = codepays;
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

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getCodepays() {
        return codepays;
    }

    public void setCodepays(String codepays) {
        this.codepays = codepays;
    }
}
