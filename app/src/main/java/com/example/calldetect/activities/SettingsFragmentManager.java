package com.example.calldetect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.calldetect.R;
import com.example.calldetect.fragment.settings.About;
import com.example.calldetect.fragment.settings.Appearance;
import com.example.calldetect.fragment.settings.Backup;
import com.example.calldetect.fragment.settings.Blocked;
import com.example.calldetect.fragment.settings.CallSave;
import com.example.calldetect.fragment.settings.Confidential;
import com.example.calldetect.fragment.settings.Identification;
import com.example.calldetect.fragment.settings.Language;
import com.example.calldetect.fragment.settings.Message;
import com.example.calldetect.fragment.settings.Sound;

import static com.example.calldetect.activities.Settings.FRAGMENT_EXTRA;

public class SettingsFragmentManager extends AppCompatActivity {
    private Bundle fragmentType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_fragment_manager);
        fragmentType = getIntent().getExtras();
        setContent();
    }

//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Language.loadLanguageLocale(this);
//    }

    private void setContent() {
        Fragment fragment = null;
        String tag = null;
        Settings.FragType type = (Settings.FragType) fragmentType.get(FRAGMENT_EXTRA);
        if (type != null) {
            switch (type) {
                case SOUND:
                    fragment = new Sound();
                    tag = getString(R.string.soud_setting);
                    break;
                case LANGUAGE:
                    fragment = new Language(this);
                    tag = getString(R.string.language_setting);
                    break;
                case IDENTIFICATION:
                    fragment = new Identification();
                    tag = getString(R.string.identification_setting);
                    break;
                case MESSAGE:
                    fragment = new Message();
                    tag = getString(R.string.message_setting);
                    break;
                case BLOCKED:
                    fragment = new Blocked();
                    tag = getString(R.string.blocked_setting);
                    break;
                case APPEARANCE:
                    fragment = new Appearance();
                    tag = getString(R.string.appearance_setting);
                    break;
                case BACKUP:
                    fragment = new Backup();
                    tag = getString(R.string.backup_setting);
                    break;
                case CALL_SAVE:
                    fragment = new CallSave();
                    tag = getString(R.string.call_save_setting);
                    break;
                case CONFIDENTIAL:
                    fragment = new Confidential();
                    tag = getString(R.string.confidential_setting);
                    break;
                case ABOUT:
                    fragment = new About();
                    tag = getString(R.string.about);
                    break;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_manager, fragment, tag)
                    .commit();
            setTitle(tag);
        }
    }

    /**
     * Function the function title.
     * @param title Title.
     */
    private void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }
}