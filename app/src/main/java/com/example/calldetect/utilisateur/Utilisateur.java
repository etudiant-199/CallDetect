package com.example.calldetect.utilisateur;

public class Utilisateur  {
    private String numero ;
    private String nomPrenom;
    private String pays;
    private String proffession;
    private String typeCompte;
    private String profil;
    private String email;
    private String sexe;
    private String dateNaissance;
    private String localisation;


    public Utilisateur(String numero, String nomPrenom, String pays, String proffession, String typeCompte, String profil, String email, String sexe, String dateNaissance, String localisation) {
        this.numero = numero;
        this.nomPrenom = nomPrenom;
        this.pays = pays;
        this.proffession = proffession;
        this.typeCompte = typeCompte;
        this.profil = profil;
        this.email = email;
        this.sexe = sexe;
        this.dateNaissance = dateNaissance;
        this.localisation = localisation;
    }


    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfil() {
        return profil;
    }

    public void setProfil(String profil) {
        this.profil = profil;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNomPrenom() {
        return nomPrenom;
    }

    public void setNomPrenom(String nomPrenom) {
        this.nomPrenom = nomPrenom;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getProffession() {
        return proffession;
    }

    public void setProffession(String proffession) {
        this.proffession = proffession;
    }

    public String getTypeCompte() {
        return typeCompte;
    }

    public void setTypeCompte(String typeCompte) {
        this.typeCompte = typeCompte;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "numero='" + numero + '\'' +
                ", nomPrenom='" + nomPrenom + '\'' +
                ", pays='" + pays + '\'' +
                ", proffession='" + proffession + '\'' +
                ", typeCompte='" + typeCompte + '\'' +
                '}';
    }
}

