package com.example.calldetect.activities;
/*
  Activity to managed the settings od the application.

  @author Ronald Tchuekou.
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.calldetect.R;
import com.example.calldetect.adapters.ItemDrawerAdapter;
import com.example.calldetect.models.ItemDrawer;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {
    public static final String FRAGMENT_EXTRA = "Frag_Type";

    public enum FragType {
        SOUND, LANGUAGE, IDENTIFICATION, MESSAGE, BLOCKED, APPEARANCE, BACKUP, CALL_SAVE,
        CONFIDENTIAL, ABOUT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.setting);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        // Initialization of the views.
        initViews();
    }

    private void initViews() {
        ListView itemSettingListView = findViewById(R.id.setting_list_view);
        final List<ItemDrawer> settingsList = new ArrayList<>();
        settingsList.add(new ItemDrawer("general", ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_settings, null), getString(R.string.general)
        ));
        settingsList.add(new ItemDrawer("son", ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_enregistre, null), getString(R.string.Son)
        ));
        settingsList.add(new ItemDrawer("language", ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_language, null), getString(R.string.Langue)
        ));
        settingsList.add(new ItemDrawer("identification", ResourcesCompat.getDrawable(
                getResources(), R.drawable.identification, null), getString(R.string.Identification)
        ));
        settingsList.add(new ItemDrawer("message", ResourcesCompat.getDrawable(
                getResources(), R.drawable.message, null), getString(R.string.Message)
        ));
        settingsList.add(new ItemDrawer("block", ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_lock_on_option, null), getString(R.string.Bloquer)
        ));
        settingsList.add(new ItemDrawer("account", ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_account, null), getString(R.string.Compte)
        ));
        settingsList.add(new ItemDrawer("appearance", ResourcesCompat.getDrawable(
                getResources(), R.drawable.apparence, null), getString(R.string.Apparence)
        ));
        settingsList.add(new ItemDrawer("saver", ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_saver, null), getString(R.string.Sauvegarde)
        ));
        settingsList.add(new ItemDrawer("recording", ResourcesCompat.getDrawable(
                getResources(), R.drawable.enregistrement_appel, null), getString(R.string.Enregistrement_appel)
        ));
        settingsList.add(new ItemDrawer("conf", ResourcesCompat.getDrawable(
                getResources(), R.drawable.confidentialite, null), getString(R.string.Centre_de_confidentialite)
        ));
        settingsList.add(new ItemDrawer("about", ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_info_outline_black_24dp, null), getString(R.string.A_propos)
        ));

        ItemDrawerAdapter adapterList = new ItemDrawerAdapter(settingsList);
        itemSettingListView.setAdapter(adapterList);

        adapterList.setOnItemClickListener(new ItemDrawerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ItemDrawer itemDrawer = settingsList.get(position);
                switch (itemDrawer.getOption()) {
                    case "general":
                        startActivity(new Intent(Settings.this, GeneralSetting.class));
                        break;
                    case "son":
                        setToFragManager(1);
                        break;
                    case "language":
                        setToFragManager(2);
                        break;
                    case "identification":
                        setToFragManager(3);
                        break;
                    case "message":
                        setToFragManager(4);
                        break;
                    case "block":
                        setToFragManager(5);
                        break;
                    case "account":
                        startActivity(new Intent(Settings.this, Profile.class));
                        break;
                    case "appearance":
                        setToFragManager(6);
                        break;
                    case "saver":
                        setToFragManager(7);
                        break;
                    case "recording":
                        setToFragManager(8);
                        break;
                    case "conf":
                        setToFragManager(9);
                        break;
                    case "about":
                        setToFragManager(10);
                        break;
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    private void setToFragManager(int id) {
        Intent intentManager = new Intent(this, SettingsFragmentManager.class);
        switch (id) {
            case 1:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.SOUND);
                break;
            case 2:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.LANGUAGE);
                break;
            case 3:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.IDENTIFICATION);
                break;
            case 4:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.MESSAGE);
                break;
            case 5:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.BLOCKED);
                break;
            case 6:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.APPEARANCE);
                break;
            case 7:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.BACKUP);
                break;
            case 8:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.CALL_SAVE);
                break;
            case 9:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.CONFIDENTIAL);
                break;
            case 10:
                intentManager.putExtra(FRAGMENT_EXTRA, FragType.ABOUT);
                break;
        }
        startActivity(intentManager);
    }
}
