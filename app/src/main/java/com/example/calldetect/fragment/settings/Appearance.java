package com.example.calldetect.fragment.settings;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.calldetect.R;

public class Appearance extends PreferenceFragmentCompat {
    public static String TAG = "Appearance";
    public static String KEY = "key_theme_pref";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.appearance, rootKey);
        ListPreference themePreference = findPreference(KEY);

        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            String themeOption = (String) newValue;
                            ThemeHelper.applyTheme(themeOption);
                            return true;
                        }
                    });
        }

    }

    public static class ThemeHelper {

        public static final String LIGHT_MODE = "light";
        public static final String DARK_MODE = "dark";
        public static final String DEFAULT_MODE = "default";

        public static void applyTheme(@NonNull String themePref) {
            switch (themePref) {
                case LIGHT_MODE: {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                }
                case DARK_MODE: {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                }
                default: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    }
                    break;
                }
            }
        }
    }
}
