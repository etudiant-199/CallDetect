package com.example.calldetect.fragment.settings;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.preference.PreferenceManager;

public class SettingsApp extends Application {

    public static final String CHANNEL_ID = "com.example.calldetect.channel";

    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themePref = sharedPreferences.getString(Appearance.KEY, Appearance.ThemeHelper.DEFAULT_MODE);
        assert themePref != null;
        Appearance.ThemeHelper.applyTheme(themePref);

        // Language
//        String languagePref = sharedPreferences.getString(Language.KEY, "");
//        assert languagePref != null;
//        Utils.setToastMessage(this, "SettingApp : language = " + languagePref);
//        Language.LanguageHelper.applyLanguage(languagePref, getBaseContext());
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "CallDetect notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notification Signalisation contacts");
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language.LanguageHelper.onAttach(base));
    }

}
