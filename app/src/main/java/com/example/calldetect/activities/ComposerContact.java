package com.example.calldetect.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.adapters.ComposerAdapter;
import com.example.calldetect.models.ModelContacts;
import com.example.calldetect.utils.Utils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.calldetect.activities.Home.SEARCH_EXTRA;

public class ComposerContact extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener {
    private LinearLayout un, deux, trois, quatre, cinq, six, sept, huit, neuf, etoile, diaize, zero;
    private FloatingActionButton call_btn, setComposer;
    private EditText tableView, edit_search;
    private ImageButton efface;
    private View paletteComposition;
    private ComposerAdapter adapter;
    private boolean is_search = false;
    private List<ModelContacts> contactsList = new ArrayList<>();
    @SuppressLint("ClickableViewAccessibility")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composeur_contact);
        actionBarManager();

        Intent extractIntent = getIntent();
        if (extractIntent.hasExtra(SEARCH_EXTRA))
            is_search = true;

        adapter = new ComposerAdapter(contactsList);
        RecyclerView recyclerView = findViewById(R.id.rv_contactselect);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ComposerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ComposerContact.this, ContactInfoActivity.class);
                intent.putExtra("Contact", contactsList.get(position));
                startActivity(intent);
            }
            @Override
            public void onItemCall(int position) {
                String number = contactsList.get(position).getNumber();
                String unFormatNumber = number.replace("<font color='#42A5F5'><b>", "");
                unFormatNumber = unFormatNumber.replace("</b></font>", "");
                Utils.call(ComposerContact.this, unFormatNumber);
            }
        });
        adapter.setOnItemLongClickListener(new ComposerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position) {
                final ModelContacts contacts = contactsList.get(position);
                new MaterialAlertDialogBuilder(ComposerContact.this)
                        .setItems(R.array.contactOptions1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Utils.addToFavorite(contacts.getNumber(), ComposerContact.this);
                                        break;
                                    case 1:
                                        Utils.blockNumber(ComposerContact.this, contacts.getNumber());
                                        break;
                                    case 2:
                                        if (Utils.deletedContact(ComposerContact.this, contacts.getNumber())) {
                                            contactsList.remove(contacts);
                                            adapter.notifyItemRemoved(position);
                                        }else
                                            Utils.setToastMessage(ComposerContact.this, getString(R.string.error_has_provide));
                                        break;
                                    case 3:
                                        Utils.editContact(ComposerContact.this, contacts.getNumber());
                                        break;
                                    case 4:
                                        Utils.signalNumber(contacts.getNumber(), ComposerContact.this);
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
        // connection de java et xml
        initView();
        // ajout de la fonction de clic
       initListener();
        // ajout du setLong clic , le clic insister
        zero.setOnLongClickListener(this);
        efface.setOnLongClickListener(this);
        //ajout de l'evenement au toucher
        recyclerView.setOnTouchListener(this);
        // ce fragment de code c'est pour blocquer la sortie du clavier natif d'android
        tableView.setShowSoftInputOnFocus(false);
        // The search queries.
        searchQuery();
    }

    /**
     * Function to here the change in the search edit text.
     */
    private void searchQuery() {
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (paletteComposition.getVisibility() == View.VISIBLE){
                    paletteComposition.setVisibility(View.GONE);
                    setComposer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setContactThatNameContains(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Functon to set the contact list with the name.
     * @param s Contact name.
     */
    private void setContactThatNameContains(String s) {
        new AsyncContactName(s).execute();
    }
    @SuppressLint("StaticFieldLeak")
    private class AsyncContactName extends  AsyncTask<Void, Void, List<ModelContacts>> {
        String contactName;
        AsyncContactName (String contactName) {
            this.contactName = contactName;
        }
        @Override
        protected List<ModelContacts> doInBackground(Void... voids) {
            List<ModelContacts> contacts = new ArrayList<>();
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
            String[] args = new String[] {"%"+ contactName +"%"};
            Cursor cursor = ComposerContact.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, selection, args, null);
            assert cursor != null;
            int name_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int number_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int image_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
            if (cursor.getCount() <= 0) // if the content of the cursor is empty.
                return contacts;
            while (cursor.moveToNext()) {
                String name = cursor.getString(name_index);
                String number = cursor.getString(number_index);
                String image = cursor.getString(image_index);
                String formatPhoneName = "<font color='#42A5F5'><b>" + contactName + "</b></font>";
                String formatName = name.replace(contactName, formatPhoneName);
                contacts.add(new ModelContacts(formatName, image, number));
            }
            cursor.close();
            return contacts;
        }

        @Override
        protected void onPostExecute(List<ModelContacts> modelContacts) {
            contactsList.clear();
            contactsList.addAll(modelContacts);
            adapter.notifyDataSetChanged();
        }
    }
    /**
     * Function to managed the actionbar.
     */
    private void actionBarManager() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Function to init the click listener.
     */
    private void initListener() {
        un.setOnClickListener(this);
        deux.setOnClickListener(this);
        trois.setOnClickListener(this);
        quatre.setOnClickListener(this);
        cinq.setOnClickListener(this);
        six.setOnClickListener(this);
        sept.setOnClickListener(this);
        huit.setOnClickListener(this);
        neuf.setOnClickListener(this);
        zero.setOnClickListener(this);
        etoile.setOnClickListener(this);
        diaize.setOnClickListener(this);
        efface.setOnClickListener(this);
        setComposer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paletteComposition.setVisibility(View.VISIBLE);
                setComposer.setVisibility(View.GONE);
            }
        });
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tableView.getText().toString().trim().equals(""))
                    Utils.call(ComposerContact.this, tableView.getText().toString().trim());
            }
        });
    }
    /**
     * Function initialize the view.
     */
    private void initView() {
        un = findViewById(R.id.un);
        deux = findViewById(R.id.deux);
        trois = findViewById(R.id.trois);
        quatre = findViewById(R.id.quatre);
        cinq = findViewById(R.id.cinq);
        six = findViewById(R.id.six);
        sept = findViewById(R.id.sept);
        huit = findViewById(R.id.huit);
        neuf = findViewById(R.id.neuf);
        zero = findViewById(R.id.zero);
        etoile = findViewById(R.id.etoile);
        diaize = findViewById(R.id.diaize);
        call_btn = findViewById(R.id.appeler);
        tableView = findViewById(R.id.ecran);
        edit_search = findViewById(R.id.edit_search);
        efface = findViewById(R.id.effacer);
        setComposer = findViewById(R.id.set_composer);
        paletteComposition = findViewById(R.id.palette_de_composition);

        if (is_search) {
            setComposer.setVisibility(View.GONE);
            paletteComposition.setVisibility(View.GONE);
            edit_search.setFocusable(true);
        }
    }
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.zero:
                tableView.getText().insert(tableView.getSelectionStart(), "+");
                break;
            case R.id.effacer:
                tableView.getText().clear();
                break;
        }
        reloadAdapter();
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.un:
                tableView.getText().insert(tableView.getSelectionStart(),"1");
                break;
            case R.id.deux:
                tableView.getText().insert(tableView.getSelectionStart(),"2");
                break;
            case R.id.trois:
                tableView.getText().insert(tableView.getSelectionStart(),"3");
                break;
            case R.id.quatre:
                tableView.getText().insert(tableView.getSelectionStart(),"4");
                break;
            case R.id.cinq:
                tableView.getText().insert(tableView.getSelectionStart(),"5");
                break;
            case R.id.six:
                tableView.getText().insert(tableView.getSelectionStart(),"6");
                break;
            case R.id.sept:
                tableView.getText().insert(tableView.getSelectionStart(),"7");
                break;
            case R.id.huit:
                tableView.getText().insert(tableView.getSelectionStart(),"8");
                break;
            case R.id.neuf:
                tableView.getText().insert(tableView.getSelectionStart(),"9");
                break;
            case R.id.zero:
                tableView.getText().insert(tableView.getSelectionStart(),"0");
                break;
            case R.id.etoile:
                    tableView.getText().insert(tableView.getSelectionStart(),"*");
                break;
            case R.id.diaize:
                    tableView.getText().insert(tableView.getSelectionStart(),"#");
                break;
            case R.id.effacer:
                int cursorPosition = tableView.getSelectionStart();
                if (cursorPosition > 0)
                    tableView.getText().delete(cursorPosition-1, cursorPosition);
                break;
        }
        reloadAdapter();
    }
    /**
     * Function to reload the content of the list.
     */
    private void reloadAdapter() {
        new ContactAsync().execute();
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.rv_contactselect) {
            if(!is_search) {
                paletteComposition.setVisibility(View.GONE);
                setComposer.setVisibility(View.VISIBLE);
            }
        }
        return false;
    }

    /**
     * Async task class to load the content.
     */
    @SuppressLint("StaticFieldLeak")
    private class ContactAsync extends AsyncTask<Void, Void, List<ModelContacts>> {
        @Override
        protected List<ModelContacts> doInBackground(Void... voids) {
            return getContact();
        }

        @Override
        protected void onPostExecute(List<ModelContacts> modelContacts) {
            contactsList.clear();
            contactsList.addAll(modelContacts);
            adapter.notifyDataSetChanged();
        }
    }
    /**
     * Function to set the list of all contact.
     */
    private List<ModelContacts> getContact(){
        String contenue_ecran = tableView.getText().toString().trim();
        List<ModelContacts> list = new ArrayList<>();
        if (!contenue_ecran.equals("")) {
            Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            assert cursor != null;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String image = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
                String formatPhoneNumber = "<font color='#42A5F5'><b>" + contenue_ecran + "</b></font>";
                String formatPhone = phoneNumber.replace(contenue_ecran, formatPhoneNumber);
                if (phoneNumber.contains(contenue_ecran)) {
                    list.add(new ModelContacts(name, image, formatPhone));
                }
            }
            cursor.close();
        }
        return list;
    }
}
