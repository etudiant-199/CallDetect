package com.example.calldetect.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ModelContacts implements Parcelable {

    private String name;
    private String image;
    private String number;
    private int useWhatsApp;


    private int imag;
    private String contact;
    private Boolean isChecked;


    public ModelContacts(String name, String contact, Boolean isChecked) {
        this.name = name;
        this.contact = contact;
        this.isChecked = isChecked;
    }


    public ModelContacts(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public ModelContacts(String name, String contact, Boolean isChecked, int image) {
        this.name = name;
        this.contact = contact;
        this.isChecked = isChecked;
        this.imag = image;
    }

    public ModelContacts () {
        this.name = "";
        this.image = "";
        this.number = "";
        this.useWhatsApp = -1;
    }

    private ModelContacts(Parcel in) {
        name = in.readString();
        image = in.readString();
        number = in.readString();
        useWhatsApp = in.readInt();
    }

    public static final Creator<ModelContacts> CREATOR = new Creator<ModelContacts>() {
        @Override
        public ModelContacts createFromParcel(Parcel in) {
            return new ModelContacts(in);
        }

        @Override
        public ModelContacts[] newArray(int size) {
            return new ModelContacts[size];
        }
    };



    public int getImag() {
        return imag;
    }

    public void setImag(int imag) {
        this.imag = imag;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getUseWhatsApp() {
        return useWhatsApp;
    }

    public void setUseWhatsApp(int useWhatsApp) {
        this.useWhatsApp = useWhatsApp;
    }

    public ModelContacts(String name, String image, String number) {
        this.name = name;
        this.image = image;
        this.number = number;
        this.useWhatsApp = 0;
    }
    @NonNull
    @Override
    public String toString() {
        return "ModelContacts{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", number='" + number + '\'' +
                ", useWhatsApp=" + useWhatsApp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(number);
        dest.writeInt(useWhatsApp);
    }
}
