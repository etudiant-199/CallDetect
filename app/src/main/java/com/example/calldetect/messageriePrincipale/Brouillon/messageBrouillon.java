package com.example.calldetect.messageriePrincipale.Brouillon;

public class messageBrouillon {
    public int idBrouillon;
    public String numeroBrouillon;
    public String messageBrouillon;

    public messageBrouillon(String numeroBrouillon, String messageBrouillon) {
        this.numeroBrouillon = numeroBrouillon;
        this.messageBrouillon = messageBrouillon;
    }

    public messageBrouillon(int idBrouillon, String numeroBrouillon, String messageBrouillon) {
        this.idBrouillon = idBrouillon;
        this.numeroBrouillon = numeroBrouillon;
        this.messageBrouillon = messageBrouillon;
    }

    public int getIdBrouillon() {
        return idBrouillon;
    }

    public void setIdBrouillon(int idBrouillon) {
        this.idBrouillon = idBrouillon;
    }

    public String getNumeroBrouillon() {
        return numeroBrouillon;
    }

    public void setNumeroBrouillon(String numeroBrouillon) {
        this.numeroBrouillon = numeroBrouillon;
    }

    public String getMessageBrouillon() {
        return messageBrouillon;
    }

    public void setMessageBrouillon(String messageBrouillon) {
        this.messageBrouillon = messageBrouillon;
    }

    @Override
    public String toString() {
        return "messageBrouillon{" +
                "numeroBrouillon='" + numeroBrouillon + '\'' +
                ", messageBrouillon='" + messageBrouillon + '\'' +
                '}';
    }
}
