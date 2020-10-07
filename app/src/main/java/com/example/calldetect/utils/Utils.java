package com.example.calldetect.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.calldetect.R;
import com.example.calldetect.firebase_data_base_manager.SignalNumberHelper;
import com.example.calldetect.messageriePrincipale.messageCustomiser.Activity_message_personaliser;
import com.example.calldetect.models.SignalNumber;
import com.example.calldetect.services.CallService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.TELECOM_SERVICE;

 /** Class qui peremttra de faire des fonctions utils pour cette application.
 *
 * @author Ronald Tchuekou.
 */

public class Utils {
     public static final String EXTRA_ACTIVITY = "Activity_provider";
     public static final String USER_EXTRA = "User_extra";
     private static final String TAG = "Utils";
     private static final String APP_DOWNLOAD_LINK = "liendetelechargement.com/CallDetect"; // TODO Change to app link.
     public static final String SHARE_LINK_TEXT = "Utilisons CallDetect, une application de détection de numeros inconnu et demasquer les arnaqueurs. Téléchargez-la sur " + APP_DOWNLOAD_LINK;

     /**
      * Function to open whatsApp on a number.
      * @param activity Activity
      * @param phone_number Phone number.
      */
     public static void openWhatsApp(Activity activity, String phone_number) {
         setToastMessage(activity, phone_number);
         // Checked if whatsApp is installed on this device.
         if (checkApp(activity, "com.whatsapp")) {
             Intent whatsAppIntent = new Intent(Intent.ACTION_VIEW);
             Uri uri = Uri.parse("https://wa.me/" + phone_number);
             whatsAppIntent.setData(uri);
             whatsAppIntent.setPackage("com.whatsapp");
             activity.startActivity(whatsAppIntent);
         }else {
             setToastMessage(activity, activity.getString(R.string.whatsapp_not_install));
         }
     }

     /**
      * Function to open facebook on a number.
      * @param activity Activity of application.
      * @param phone_number Phone number.
      */
     public static void openFacebook(Activity activity, String phone_number) {
         Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW);
         Uri uri;
         // Checked if facebook is installed on this device.
         if (checkApp(activity,"com.facebook.katana")) {
             uri = Uri.parse("fb://page/" + phone_number);
         }else {
             uri = Uri.parse("https://www.facebook.com/" + phone_number);
             setToastMessage(activity, activity.getString(R.string.facebook_not_install));
         }
         facebookAppIntent.setData(uri);
         activity.startActivity(facebookAppIntent);
     }

     /**
      * Function to open Skype app on a number.
      * @param activity Activity of application.
      * @param phone_number Phone number.
      */
     public static void openSkype(Activity activity, String phone_number) {
         // Checked if skype is installed on this device.
         if (checkApp(activity, "com.skype.raider") || checkApp(activity, "com.skype.raider.Main")) {
             Intent skypeIntent = new Intent("android.intent.action.VIEW");
             skypeIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
             skypeIntent.setData(Uri.parse("skype:" + phone_number + "?chat"));
         }else{
             Utils.setToastMessage(activity, activity.getString(R.string.skype_not_install));
         }
     }

     /**
      * Function to checked if an application is install or not on the device.
      * @param context Application context.
      * @param url Url of the app package.
      * @return boolean result.
      */
     public static boolean checkApp (Context context, String url) {
         PackageManager packageManager = context.getPackageManager();
         try {
             packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
             return true;
         } catch (PackageManager.NameNotFoundException e) {
             return false;
         }
     }

     /**
      * Call log types.
      */
     public static class CallLogType {
         public static int OUTGOING = 0;
         public static int INCOMING = 1;
         public static int MISSED = 2;
         public static int REJECTED = 3;
     }

     /**
      * Function to set the toast message.
      * @param context Application context.
      * @param message Message.
      */
     public static void setToastMessage(Context context, String message) {
         Toast toast = new Toast(context);
         toast.setGravity(Gravity.CENTER, 0, -150);
         toast.setDuration(Toast.LENGTH_LONG);
         @SuppressLint("InflateParams") View toastLayout = LayoutInflater.from(context)
                 .inflate(R.layout.toast_layout_custom, null, false);
         toast.setView(toastLayout);
         ((TextView)toast.getView().findViewById(R.id.toast_message)).setText(message);
         toast.show();
     }

    /**
     * Function to added number to mal number list.
     * @param number Phone number.
     * @param context Application context.
     */
    public static void signalNumber(final String number, final Context context) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SignalNumberHelper.isSignal(number)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.i(TAG, "Is signal : " + documentSnapshot.toObject(SignalNumber.class));
                        if (documentSnapshot.toObject(SignalNumber.class) == null) {
                            if (user != null) {
                                Date currentDate = Calendar.getInstance().getTime();
                                SignalNumberHelper.addSignalNumber(new SignalNumber(number, user.getPhoneNumber(), currentDate))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Utils.setToastMessage(context, context.getString(R.string.is_signal));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (e instanceof FirebaseNetworkException)
                                                    Utils.setToastMessage(context, context.getString(R.string.error_not_connect));
                                                else
                                                    Utils.setToastMessage(context, context.getString(R.string.not_signal));
                                            }
                                        });
                            }else{
                                Log.e(TAG, "Signalisation, User not set. ");
                            }
                        } else {
                            Utils.setToastMessage(context, context.getString(R.string.this_number_is_already_signal));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseNetworkException)
                            Utils.setToastMessage(context, context.getString(R.string.error_not_connect));
                        else
                            Utils.setToastMessage(context, context.getString(R.string.not_signal));
                    }
                });
    }

    /**
     * Function to added a number to favorite list.
     * @param number Phone number.
     * @param context Application context.
     */
    public static void addToFavorite(String number, Context context) {
        // TODO implement the module to add to the favorite.
        Utils.setToastMessage(context, "Module pour ajouter un contact: " + number + " dans les favoris, pas encore pret.");
    }

    /**
     * Function to deleted a number to the data base.
     * @param context Application Context.
     * @param phoneNumber Phone Number.
     */
    public static boolean deletedContact(Context context, String phoneNumber) {
        try {
            ContentValues values = new ContentValues();
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
            Uri contact_id = context.getContentResolver().insert(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    values);

            if (contact_id == null)
                return false;

            context.getContentResolver().delete(contact_id, null, null);
            return true;
        }catch (SecurityException e) {
            Log.e("Utils", "deleteContact: ", e);
            Utils.setToastMessage(context, context.getString(R.string.error_has_provide));
        }catch (UnsupportedOperationException e) {
            Log.e(TAG, "deletedContact: ", e);
            Utils.setToastMessage(context, context.getString(R.string.error_has_provide));
        }
        return false;
    }

     /**
      * Function to add contact.
      * @param context Application context.
      * @param phoneNumber Phone number.
      */
    public static void addContact (Context context, String phoneNumber) {
        Intent addIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        addIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        addIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
        addIntent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        context.startActivity(addIntent);
    }

    /**
     * Function to modify a phone number.
     * @param context Application context.
     * @param phoneNumber Phone number.
     */
    public static void updateContact (Context context, String phoneNumber) {
        try {
            // The Cursor that contains the Contact row
            Cursor mCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = ? ", new String[]{phoneNumber}, null, null);
            // The index of the lookup key column in the cursor
            int lookupKeyIndex;
            // The index of the contact's _ID value
            int idIndex;
            // The lookup key from the Cursor
            String currentLookupKey;
            // The _ID value from the Cursor
            long currentId;
            // A content URI pointing to the contact
            Uri selectedContactUri;
            /*
             * Once the user has selected a contact to edit,
             * this gets the contact's lookup key and _ID values from the
             * cursor and creates the necessary URI.
             */
            // Gets the lookup key column index
            assert mCursor != null;
            mCursor.moveToFirst();
            lookupKeyIndex = mCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
            // Gets the lookup key value
            currentLookupKey = mCursor.getString(lookupKeyIndex);
            // Gets the _ID column index
            idIndex = mCursor.getColumnIndex(ContactsContract.Contacts._ID);
            currentId = mCursor.getLong(idIndex);
            selectedContactUri =
                    ContactsContract.Contacts.getLookupUri(currentId, currentLookupKey);
            // Creates a new Intent to edit a contact
            Intent editIntent = new Intent(Intent.ACTION_EDIT);
            /*
             * Sets the contact URI to edit, and the data type that the
             * Intent must match
             */
            editIntent.setDataAndType(selectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            // Sets the special extended data for navigation
            editIntent.putExtra("finishActivityOnSaveCompleted", true);
            // Sends the Intent
            context.startActivity(editIntent);
            mCursor.close();
        }catch (CursorIndexOutOfBoundsException exception) {
            Utils.setToastMessage(context, context.getString(R.string.number_not_register));
        }
    }
    /**
     * Function to share the number to an user.
     * @param number Phone number.
     * @param context Application context.
     */
    public static void shareNumber(String number, Context context) {
        setToastMessage(context, "Partage de numero de téléphone : " + number + " pas encore au point.");
        // TODO implement this.
    }

    /**
     * Function to set an invitation to an users.
     * @param number Phone number.
     * @param context Application context.
     */
    public static void inViteNumber(String number, Context context) {
        Intent share = new Intent(context, Activity_message_personaliser.class);
        share.putExtra("number", number);
        share.putExtra(Intent.EXTRA_TEXT, SHARE_LINK_TEXT);
        context.startActivity(share);
    }

     /**
      * Function to invite a person to use this application.
      * @param context Application context.
      */
    public static void invite (Context context) {
        try {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_SUBJECT, "CallDetect");
            share.putExtra(Intent.EXTRA_TEXT, SHARE_LINK_TEXT);
            context.startActivity(Intent.createChooser(share, context.getString(R.string.shose_app)));
        }catch (Exception e) {
            Log.e(TAG, "invite: Error in function : ", e);
            Utils.setToastMessage(context, context.getString(R.string.error_has_provide));
        }
    }

    /**
     * Function to deleted a contact to a call log .
     * @param number Phone number.
     * @param context Application context.
     */
    public static boolean deleteCallLog(String number, Context context) {
        try {
            ContentValues values = new ContentValues();
            Uri contact_id;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number);
                values.put(BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER, number);
                contact_id = context.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                        values);
                if (contact_id == null) return false;
                else context.getContentResolver().delete(contact_id, null, null);
                return true;
            } else {
                Utils.setToastMessage(context, context.getString(R.string.error_has_provide));
            }
            return false;
        }catch (SecurityException e) {
            Log.e("Utils", "deleteCallLog: ", e);
            return false;
        }
    }

    /**
     * Function that remove phone number from favorite contacts.
     * @param number Phone number.
     * @param activity Activity of application.
     */
    public static void removeToFavorite(String number, Activity activity) {
        Log.i(TAG, "removeToFavorite: " + number);
        setToastMessage(activity, activity.getString(R.string.remove_success));
        // TODO implement this function to remove a phone number to the favorite list.
    }

    /**
     * Function that send to the edited activity.
     * @param activity Activity application.
     * @param phoneNumber Phone number.
     */
    public static void editContact(Activity activity, String phoneNumber) {
        Utils.updateContact(activity, phoneNumber);
    }
    /**
     * Function to set the dialog not network available.
     */
    public static void setDialog(Context context, String message) {
        new MaterialAlertDialogBuilder(context)
                .setMessage(message)
                .setPositiveButton(R.string.OK, null)
                .show();
    }

     /**
      * Function to make a call.
      * @param context Application context.
      * @param number Phone number.
      */
    public static void call(Context context, String number) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[] {Manifest.permission
                    .CALL_PHONE}, 1);
            return;
        }
        // TEST OF CALL SERVICE.
        context.startService(new Intent(context, CallService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(TELECOM_SERVICE);
            Uri uri = Uri.fromParts("tel", number, null);
            Bundle extra = new Bundle();
            assert telecomManager != null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                telecomManager.placeCall(uri, extra);
            }else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                context.startActivity(callIntent);
            }
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        }
    }

    /**
     * Function to format the time seconds.
     * @param second Time second
     * @return format.
     */
    public static String SecondsToTime (int second) {
        int hours = second / 3600;
        int minutes = (second % 3600) / 60;
        int seconds = second % 60;
        if (hours == 0 && minutes == 0 && seconds == 0)
            return "0s";
        if (hours == 0 && minutes == 0)
            return seconds+"s";
        if (hours == 0)
            return minutes + "m" + seconds +"s";
        return hours + "h" + minutes + "m" + seconds + "s";
    }

    /**
     * Function to get the file extension.
     * @param uri Uri of the file.
     * @param context Application context.
     */
    public static String getFileExtension(Uri uri, Context context) {
        ContentResolver cr = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String result = mime.getExtensionFromMimeType(cr.getType(uri));
        if (result == null)
            return "";
        else
            return "." + result;
    }

    /**
     * Function to get the string format date.
     * @param date Date.
     * @param context Application context.
     */
    public static String getDateAlias (Date date, Context context) {
        String dateStr;
        Calendar calendar = Calendar.getInstance(), calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_WEEK, -1);
        String callDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        String todayDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        String yesterday = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar1.getTime());
        if (todayDate.equals(callDate))
            dateStr = context.getResources().getString(R.string.today);
        else if (yesterday.equals(callDate)){
            dateStr = context.getResources().getString(R.string.yesterday);
        }else
            dateStr = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        return dateStr;
    }

    /**
     * Function to get the string format time.
     * @param date Date
     */
    public static String getTimeAlias (Date date) {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    /**
     * Function to get if the number argument exist or not.
     * @param context Application context.
     * @param phoneNumber Phone number.
     */
    public static boolean numberExist(Context context, String phoneNumber) {
        String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER +"=?";
        String[] args = new String[] {phoneNumber};
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone
                .CONTENT_URI, projection, selection, args, null);
        assert cursor != null;
        boolean isThere = cursor.moveToNext();
        cursor.close();
        return isThere;
    }

    /**
     * Function to put a number to a blocked list.
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static void blockNumber (Context context, String phoneNumber) {
//        if (BlockedNumberContract.canCurrentUserBlockNumbers(context)) {
//            ContentValues values = new ContentValues();
//            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber);
//            if (phoneNumber.contains("+"))
//                values.put(BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER, phoneNumber);
//            TelecomManager telecomManager = (TelecomManager) context.getSystemService(TELECOM_SERVICE);
//            assert telecomManager != null;
//            String dialPackage = telecomManager.getDefaultDialerPackage();
//            Log.e("Utils", "Default dial package: "+ dialPackage, null);
//            return context.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
//                    values);
//        }else {
//            Utils.setToastMessage(context, context.getString(R.string.not_allow_to_block_numbers),
//                    Toast.LENGTH_SHORT).show();
//            return null;
//        }
        Log.d("Utils", "blockNumber: " + phoneNumber);
        Utils.launchBlockedNumber(context);
    }
//
//    /**
//     * Function to check if number is locked or not.
//     * @param context Application context.
//     * @param phoneNumber Phone number of the user.
//     */
//    @SuppressLint("InlinedApi")
//    public static boolean checkIfNumberIsLocked (Context context, String phoneNumber) {
//        return BlockedNumberContract.isBlocked(context, phoneNumber);
//    }

    /**
     * Function to unlock one number.
     * @param context Application context.
     * @param phoneNumber Phone Number.
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static void unblockPhoneNumber(Context context, String phoneNumber) {
        BlockedNumberContract.unblock(context, phoneNumber);
    }

//    /**
//     * Function to get all locked numbers.
//     */
//    @TargetApi(Build.VERSION_CODES.N)
//    public static Cursor queryBlockNumbers(Context context) {
//        return context.getContentResolver().query(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
//                new String[]{BlockedNumberContract.BlockedNumbers.COLUMN_ID,
//                        BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
//                        BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER},
//                null, null, null);
//    }

    /**
     * Function qui permet de lancer le gesetionnaire de numero bloqués dans cette activité.
     * @param context Application context.
     */
    @SuppressLint("NewApi")
    public static void launchBlockedNumber (Context context) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(TELECOM_SERVICE);
        assert telecomManager != null;
        context.startActivity(telecomManager.createManageBlockedNumbersIntent(), null);
    }

    /**
     * Function to now if a contact has WhatsApp.
     * @param activity Application activity.
     */
    public static boolean hasWhatsApp (String contact_id, Context activity) {
        String[] projection = new String[] { ContactsContract.RawContacts._ID };
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
        String[] selectionArgs = new String[] { contact_id, "com.whatsapp" };
        Cursor cursor = activity.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                projection, selection, selectionArgs, null);
        assert cursor != null;
        boolean has = cursor.moveToNext();
        cursor.close();
        return has;
    }

//    /**
//     * Function to get the contact number registered in WhatsApp.
//     * @param rawContactId Contact id.
//     * @param activity Application activity.
//     */
//    public static String getWhatsAppNumber (String rawContactId, Activity activity) {
//        String[] projection = new String[] { ContactsContract.Data.DATA3 };
//        String selection = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.Data.
//                RAW_CONTACT_ID + " = ? ";
//        String[] selectionArgs = new String[] { "vnd.android.cursor.item/vnd.com.whatsapp.profile",
//                rawContactId };
//        Cursor cursor = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
//                projection, selection, selectionArgs, "1 LIMIT 1");
//        String phoneNumber = null;
//        assert cursor != null;
//        if (cursor.moveToNext()) {
//            phoneNumber = cursor.getString(0);
//        }
//        cursor.close();
//        return phoneNumber;
//    }
}



