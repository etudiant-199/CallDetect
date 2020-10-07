package com.example.calldetect.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ModelCalls implements Parcelable {

    private String name;
    private String number;
    private String date;
    private String time;
    private int duration;
    private int type;
    private int interactions;
    private int registered;

    public ModelCalls() {
        this.name = "";
        this.number = "";
        this.date = "";
        this.time = "";
        this.duration = -1;
        this.type = -1;
        this.interactions = -1;
        this.registered = 0;
    }

    public ModelCalls(String name, String number, int duration, String date, String time, int type) {
        this.name = name;
        this.number = number;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.type = type;
        this.interactions = 1;
        this.registered = 0;
    }

    private ModelCalls(Parcel in) {
        name = in.readString();
        number = in.readString();
        date = in.readString();
        time = in.readString();
        duration = in.readInt();
        type = in.readInt();
        interactions = in.readInt();
        registered = in.readInt();
    }

    public static final Creator<ModelCalls> CREATOR = new Creator<ModelCalls>() {
        @Override
        public ModelCalls createFromParcel(Parcel in) {
            return new ModelCalls(in);
        }

        @Override
        public ModelCalls[] newArray(int size) {
            return new ModelCalls[size];
        }
    };
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType () {
        return type;
    }

    public void setType (int type) {
        this.type = type;
    }

    public int getInteractions () {
        return interactions;
    }

    public void setInteractions (int interactions) {
        this.interactions = interactions;
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    @NonNull
    @Override
    public String toString() {
        return "ModelCalls{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", duration='" + duration + '\'' +
                ", type='" + type + '\'' +
                ", interactions='" + interactions + '\'' +
                ", registered='" + registered + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(number);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeInt(duration);
        dest.writeInt(type);
        dest.writeInt(interactions);
        dest.writeInt(registered);
    }
}
