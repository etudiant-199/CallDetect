package com.example.calldetect.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.calldetect.R;
import com.example.calldetect.messageriePrincipale.messageCustomiser.Activity_message_personaliser;
import com.example.calldetect.models.ModelContacts;
import com.example.calldetect.utils.Utils;
import com.google.android.material.appbar.MaterialToolbar;

import static com.example.calldetect.helpers.ContactHelper.DETAILS_QUERY_ID;
import static com.example.calldetect.helpers.ContactHelper.PROJECTION;
import static com.example.calldetect.helpers.ContactHelper.SELECTION;
import static com.example.calldetect.helpers.ContactHelper.SORT_ORDER;

public class ContactInfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    ImageView btn_call, btn_message;
    TextView phone_number;
    ModelContacts contacts;
    MenuItem favoriteItem, blockItem;
    private boolean is_favorite = false, is_blocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        contacts = getIntent().getParcelableExtra("Contact");
        assert contacts != null;
        // Format the name.
        contacts.setName(contacts.getName().replace("<font color='#42A5F5'><b>", ""));
        contacts.setName(contacts.getName().replace("</b></font>", ""));

        // Format the number.
        contacts.setNumber(contacts.getNumber().replace("<font color='#42A5F5'><b>", ""));
        contacts.setNumber(contacts.getNumber().replace("</b></font>", ""));

//        ContactHelper contactHelper = new ContactHelper(this, contacts.getNumber());
//        contacts = contactHelper.getContacts();
        LoaderManager.getInstance(this).initLoader(DETAILS_QUERY_ID, null, this);
//        getSupportLoaderManager().initLoader(DETAILS_QUERY_ID, null, this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null ) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(contacts.getName());
        }

        initView();
        setViewValues();
        intiViewListeners();
    }

    /**
     * Function to initialized the views listeners.
     */
    private void intiViewListeners() {

        findViewById(R.id.whatsapp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openWhatsApp(ContactInfoActivity.this, contacts.getNumber());
            }
        });

        findViewById(R.id.facebook_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openFacebook(ContactInfoActivity.this, contacts.getNumber());
            }
        });

        findViewById(R.id.skype_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openSkype(ContactInfoActivity.this, contacts.getNumber());
            }
        });
    }

    private void setViewValues() {
        phone_number.setText(contacts.getNumber());
    }

    private void initView() {
        btn_call = findViewById(R.id.btn_call);
        btn_message = findViewById(R.id.btn_message);
        phone_number = findViewById(R.id.phone_number);

        // Click listeners
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.call(ContactInfoActivity.this, contacts.getNumber());
            }
        });
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactInfoActivity.this, Activity_message_personaliser.class);
                intent.putExtra("number", contacts.getNumber());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        favoriteItem = menu.getItem(1);
        blockItem = menu.getItem(2);
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_option:
                Utils.updateContact(this, contacts.getNumber());
                finish();
                break;
            case R.id.favorite_option:
                if (is_favorite){
                    is_favorite = false;
                    Utils.removeToFavorite(contacts.getNumber(), this);
                    blockItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_add_option));
                } else{
                    is_favorite = true;
                    Utils.addToFavorite(contacts.getNumber(), this);
                    blockItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_remove_option));
                }
                break;
            case R.id.signal_option:
                Utils.signalNumber(contacts.getNumber(), this);
                break;
            case R.id.block_option:
                if (is_blocked){
                    is_blocked = false;
                    blockItem.setIcon(getResources().getDrawable(R.drawable.ic_lock_off_option));
                    Utils.unblockPhoneNumber(this, contacts.getNumber());
                } else {
                    is_blocked = true;
                    blockItem.setIcon(getResources().getDrawable(R.drawable.ic_lock_on_option));
                    Utils.blockNumber(this, contacts.getNumber());
                }
                break;
            case R.id.delete_option:
                Utils.deletedContact(this, contacts.getNumber());
                break;
            case R.id.share_option:
                Utils.shareNumber(contacts.getNumber(), this);
                break;
            case R.id.invite_option:
                Utils.inViteNumber(contacts.getNumber(), this);
                break;
            default:
                finish();
                break;
        }
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            String[] selectionArgs = new String[]{contacts.getNumber()};
            // Starts the query
            return new CursorLoader(
                    ContactInfoActivity.this,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PROJECTION,
                    SELECTION,
                    selectionArgs,
                    SORT_ORDER
            );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int name_index = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int number_index = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int phonic_index = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHONETIC_NAME);
        int contact_id_index = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        data.moveToFirst();
        if (data.getCount() == 0)
            return;
        contacts.setName(data.getString(name_index));
        contacts.setNumber(data.getString(number_index));
        contacts.setImage(data.getString(phonic_index));
        contacts.setUseWhatsApp(Utils.hasWhatsApp(data.getString(contact_id_index), ContactInfoActivity.this) ? 1 : 0);

        phone_number.setText(contacts.getNumber());

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.i("ContactInfo", "On loader reset method.");
    }
}
