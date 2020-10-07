package com.example.calldetect.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CallLog implements Parcelable {
    private String number;
    private String date;
    private String time;
    private int duration;
    private int type;
    
    public CallLog() {}

    public CallLog(String number, String date, String time, int duration, int type) {
        this.number = number;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.type = type;
    }
    private CallLog(Parcel in) {
        number = in.readString();
        date = in.readString();
        time = in.readString();
        duration = in.readInt();
        type = in.readInt();
    }
    public static final Creator<CallLog> CREATOR = new Creator<CallLog>() {
        @Override
        public CallLog createFromParcel(Parcel in) {
            return new CallLog(in);
        }
        @Override
        public CallLog[] newArray(int size) {
            return new CallLog[size];
        }
    };
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
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    @NonNull
    @Override
    public String toString() {
        return "CallLog{" +
                "number='" + number + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", duration='" + duration + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(number);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeInt(duration);
        parcel.writeInt(type);
    }
}
