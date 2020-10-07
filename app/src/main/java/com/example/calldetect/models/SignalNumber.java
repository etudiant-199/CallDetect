package com.example.calldetect.models;

import androidx.annotation.NonNull;

import java.util.Date;

public class SignalNumber {
    private String number;
    private String signalBy;
    private Date addDate;

    public SignalNumber () { }
    public SignalNumber (String number, String signalBy, Date addDate) {
        setNumber (number);
        setSignalBy(signalBy);
        setAddDate (addDate);
    }

    public String getNumber () { return number; }
    public void setNumber (String number) { this.number = number; }
    public String getSignalBy () { return signalBy; }
    public void setSignalBy (String signalBy) { this.signalBy = signalBy; }
    public Date getAddDate () { return addDate; }
    public void setAddDate (Date addDate) { this.addDate = addDate; }

    @Override
    @NonNull
    public String toString () {
        return "SignalNumber : {" +
                "number = " + number
                +", signalBy = " + signalBy
                +", addDate = " + addDate
                +"}";
    }
}
