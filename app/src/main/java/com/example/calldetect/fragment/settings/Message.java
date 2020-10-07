package com.example.calldetect.fragment.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.calldetect.R;

public class Message extends PreferenceFragmentCompat {
    public static final String TAG = "Message";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.message, rootKey);
    }
}
