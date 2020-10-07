package com.example.calldetect.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Classe qui permet de gérer les utilisateurs de l'application.
 */
public class User implements Parcelable {

    private String id;
    private String name;
    private String surname;
    private String birthday;
    private String email;
    private String phone;
    private String phone2;
    private String sex;
    private String address;
    private String profile;
    private String profession;
    private String cni;
    private String deliver_date;
    private boolean isCertificate;

    public User () {
        id = "";
        name = "";
        surname = "";
        birthday = "";
        email = "";
        phone = "";
        phone2 = "";
        sex = "";
        address = "";
        profession = "";
        profile = "";
        cni = "";
        deliver_date = "";
        isCertificate = false;
    }
    public User (String id, String name, String surname, String birthday, String email, String phone, String phone2,
                 String sex, String address, String profile, String profession, boolean isCertificate) {
        setId(id);
        setName(name);
        setSurname(surname);
        setBirthday(birthday);
        setEmail(email);
        setPhone (phone);
        setPhone2 (phone2);
        setSex(sex);
        setAddress (address);
        setProfile (profile);
        setProfession(profession);
        setCni("");
        setDeliver_date("");
        setCertification(isCertificate);
    }
    public User (String id, String name, String surname, String profession) {
        setId(id);
        setName(name);
        setSurname(surname);
        setBirthday("");
        setEmail("");
        setPhone ("");
        setSex("");
        setAddress ("");
        setProfile ("");
        setCni("");
        setDeliver_date("");
        setProfession(profession);
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        surname = in.readString();
        birthday = in.readString();
        email = in.readString();
        phone = in.readString();
        phone2 = in.readString();
        sex = in.readString();
        address = in.readString();
        profile = in.readString();
        profession = in.readString();
        cni = in.readString();
        deliver_date = in.readString();
        isCertificate = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getProfession () { return profession; }
    public void setProfession (String profession) { this.profession = profession; }
    public String getCni () { return cni; }
    public void setCni (String cni) { this.cni = cni; }
    public String getDeliver_date () { return deliver_date; }
    public void setDeliver_date (String deliver_date ) { this.deliver_date = deliver_date; }
    public boolean getCertification () { return isCertificate; }
    public void setCertification (boolean isCertificate) { this.isCertificate = isCertificate; }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", phone2='" + phone2 + '\'' +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", profile='" + profile + '\'' +
                ", profession='" + profession + '\'' +
                ", cni='" + cni + '\'' +
                ", deliver_date='" + deliver_date + '\'' +
                ", isCertificate='" + isCertificate + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(birthday);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(phone2);
        dest.writeString(sex);
        dest.writeString(address);
        dest.writeString(profile);
        dest.writeString(profession);
        dest.writeString(cni);
        dest.writeString(deliver_date);
        dest.writeInt(isCertificate ? 1 : 0);
    }
}
