package com.example.calldetect.models;

import androidx.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable;

public class Users_Simple implements Parcelable {

    private String login;
    private String nom;
    private String prenom;
    private String profession;
    private String otherNumber;
    private String email;
    private String profile;

    public Users_Simple(){ }

    public Users_Simple(String m_login, String m_name, String m_prenom, String m_profession){
        login = m_login;
        nom = m_name;
        prenom = m_prenom;
        profession = m_profession;
    }

    public Users_Simple (String m_login, String m_name, String m_prenom, String m_profession,
                         String otherNumber, String email, String profile){
        login = m_login;
        nom = m_name;
        prenom = m_prenom;
        profession = m_profession;
        this.otherNumber = otherNumber;
        this.email = email;
        this.profile = profile;
    }

    private Users_Simple(Parcel in) {
        login = in.readString();
        nom = in.readString();
        prenom = in.readString();
        profession = in.readString();
        otherNumber = in.readString();
        email = in.readString();
        profile = in.readString();
    }

    public static final Creator<Users_Simple> CREATOR = new Creator<Users_Simple>() {
        @Override
        public Users_Simple createFromParcel(Parcel in) {
            return new Users_Simple(in);
        }

        @Override
        public Users_Simple[] newArray(int size) {
            return new Users_Simple[size];
        }
    };

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return nom;
    }

    public void setName(String name) {
        this.nom = name;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getOtherNumber() {
        return otherNumber;
    }

    public void setOtherNumber(String otherNumber) {
        this.otherNumber = otherNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(login);
        dest.writeString(nom);
        dest.writeString(prenom);
        dest.writeString(profession);
        dest.writeString(otherNumber);
        dest.writeString(email);
        dest.writeString(profile);
    }

    @NonNull
    @Override
    public String toString() {
        return "Users_Simple{" +
                "login='" + login + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", profession='" + profession + '\'' +
                ", otherNumber='" + otherNumber + '\'' +
                ", email='" + email + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
