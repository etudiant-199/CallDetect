package com.example.calldetect.models;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class ItemDrawer {
    private String option;
    private Drawable icon;
    private String title;

    public ItemDrawer (String option, Drawable icon, String title) {
        setOption(option);
        setIcon(icon);
        setTitle(title);
    }

    public String getOption() { return option; }
    public void setOption (String option) { this.option = option; }
    public Drawable getIcon () { return icon; }
    public void setIcon (Drawable icon) { this.icon = icon; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @NonNull
    @Override
    public String toString() {
        return "ItemDrawer{" +
                "option='" + option + '\'' +
                ", icon=" + icon +
                ", title='" + title + '\'' +
                '}';
    }
}
