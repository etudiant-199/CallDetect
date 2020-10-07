package com.example.calldetect.helpers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.calldetect.models.ModelContacts;
import com.example.calldetect.utils.Utils;

/**
 * Classe qui permettra gérer les informations sur un contact.
 */
public class ContactHelper {
    /**
     * Element that hold the
     */
    private ModelContacts contacts;

    /**
     * Application context.
     */
    private Activity activity;

    /**
     * Data projections.
     */
    public static final String[] PROJECTION =
            {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHONETIC_NAME,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            };
    /**
     * Selection clause.
     */
    public static final String SELECTION = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ? ";

//            + ContactsContract.Data.MIMETYPE + " = " + "'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE +"'"
    /**
     * Sort oder.
     */
    public static final String SORT_ORDER = ContactsContract.Data.MIMETYPE;
    /**
     * Id of the loader.
     */
    public static final int DETAILS_QUERY_ID = 0;
    /**
     * Look up key to make the search.
     */
    private String lookupKey;
    /**
     * Constructor of ths class.
     * @param activity ContactHelper.
     */
    public ContactHelper(Activity activity, String key) {
        this.activity = activity;
        this.lookupKey = key;
        this.contacts = new ModelContacts();
    }

//    private LoaderManager.LoaderCallbacks<Object> callback = new LoaderManager.LoaderCallbacks<Object>() {
//        @NonNull
//        @Override
//        public Loader<Object> onCreateLoader(int loaderId, @Nullable Bundle args) {
//            String[] selectionArgs = new String[]{lookupKey};
//            // Starts the query
//            return new CursorLoader(
//                    activity,
//                    ContactsContract.Data.CONTENT_URI,
//                    PROJECTION,
//                    SELECTION,
//                    selectionArgs,
//                    SORT_ORDER
//            );
//        }
//
//        @Override
//        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//            Cursor cursor = (Cursor) data;
//            int name_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//            int number_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//            int phonic_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHONETIC_NAME);
//            int contact_id_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
//
//            contacts.setName(cursor.getString(name_index));
//            contacts.setNumber(cursor.getString(number_index));
//            contacts.setImage(cursor.getString(phonic_index));
//            contacts.setUseWhatsApp(Utils.hasWhatsApp(cursor.getString(contact_id_index), activity) ? 1 : 0);
//        }
//
//        @Override
//        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//        }
//    };
    /**
     * Function to get the contact.
     * @return ModelContact.
     */
    public ModelContacts getContacts() {
        return contacts;
    }
}
