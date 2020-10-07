package com.example.calldetect.fragment.settings;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.calldetect.R;

import java.util.Locale;

public class Language extends PreferenceFragmentCompat {

    public static final String TAG = "Language";
    public static final String KEY = "language_key_pref";

    private Activity activity;

    public Language(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(LanguageHelper.onAttach(context, "fr"));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.language, rootKey);
        ListPreference languagePreferences = findPreference(KEY);
        if (languagePreferences != null) {
            languagePreferences.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            String languageOption = (String) newValue;
                            Log.e(TAG, "onPreferenceChange: "+languageOption, null);
                            LanguageHelper.setLocale(activity, languageOption);
                            return true;
                        }
                    });
        }
    }

//    public static void loadLanguageLocale(Activity activity) {
//        SharedPreferences preferences = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE);
//        String language = preferences.getString(KEY, "fr");
//        assert language != null;
//        Log.e(TAG, "loadLanguageLocale: : " + language, null);
//        LanguageHelper.setSocialLang(activity, language);
//    }

    public static class LanguageHelper {

        public static Context onAttach (Context context) {
            String lang = getPersistenceData(context, Locale.getDefault().getLanguage());
            return setLocale(context, lang);
        }
        public static Context onAttach (Context context, String defaultLanguage) {
            String lang = getPersistenceData(context, defaultLanguage);
            return setLocale(context, lang);
        }
        public static Context setLocale(Context context, String language) {
            Log.e(TAG, "setLocale: Language = " + language, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                return updateResources(context, language);
            return updateResourcesLegacy(context, language);
        }
        public static String getPersistenceData(Context context, String defaultLanguage) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(KEY, defaultLanguage);
        }
        public static void persist (Context context, String language) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString(KEY, language);
            editor.apply();
        }
        @TargetApi(Build.VERSION_CODES.N)
        public static Context updateResources (Context context, String lang) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = context.getResources().getConfiguration();
            config.setLocale(locale);
            config.setLayoutDirection(locale);
            persist(context, lang);
            return context.createConfigurationContext(config);
        }
        public static Context updateResourcesLegacy (Context context, String lang) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            config.locale = locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                config.setLayoutDirection(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            persist(context, lang);
            return context;
        }
//
//        public static void applyLanguage(@NonNull String languagePref, Context activity) {
//            switch (languagePref) {
//                case FRENCH: {
//                    setSocialLang(activity,"fr");
//                    break;
//                }
//                case ENGLISH: {
//                    setSocialLang(activity,"en");
//                    break;
//                }
//                default: {
//                    setSocialLang(activity,"");
//                    break;
//                }
//            }
//        }
//
//        /**
//         * Function to set the selected social language.
//         * @param lang Social language.
//         */
//        public static void setSocialLang(Context context, String lang) {
//            Locale locale = new Locale(lang);
//            Locale.setDefault(locale);
//            Configuration config = new Configuration();
//            config.locale = locale;
//            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
//            // Share the preference language.
//            SharedPreferences.Editor editor = context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
//            editor.putString(KEY, lang);
//            editor.apply();
//            Log.e(Language.TAG, "setSocialLang: Language is set.", null);
//        }
    }

}
