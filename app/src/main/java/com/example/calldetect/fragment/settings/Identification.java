package com.example.calldetect.fragment.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.calldetect.R;

public class Identification extends PreferenceFragmentCompat {
    public static final String TAG = "Identification";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.identification, rootKey);
    }
}
