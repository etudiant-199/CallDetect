package com.example.calldetect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.calldetect.R;
import com.example.calldetect.adapters.ContactRvAdapter;
import com.example.calldetect.models.ModelContacts;
import com.example.calldetect.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListContact extends AppCompatActivity {
    private List<ModelContacts> contactList=new ArrayList<>();
    private List<Boolean> viewPosition = new ArrayList<>();

    private void setViewPosition () {
        for (ModelContacts ignored : contactList)
            viewPosition.add(false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contact);
        RecyclerView recycler = findViewById(R.id.recycler);

        contactList=sortList(allContact());
        setViewPosition();
       ContactRvAdapter adapter = new ContactRvAdapter(viewPosition, contactList);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new ContactRvAdapter.OnItemClickListener() {
            @Override
            public void damaskElement(int position) {
                Utils.setToastMessage(getApplicationContext(), "Demask element on position " + position);
                if (viewPosition.get(position)) {
                    viewPosition.remove(position);
                    viewPosition.add(position, false);
                }else {
                    viewPosition.remove(position);
                    viewPosition.add(position, true);
                }

                Log.d("User_Fragment", "damaskElement: " + viewPosition.get(position));
            }

            @Override
            public void sendCall(int position) {
                Utils.setToastMessage(getApplicationContext(), "Send call" + position);
                Intent intent=new Intent(getApplicationContext(),CallActivity.class);
                intent.putExtra("nom",contactList.get(position).getName());
                intent.putExtra("numero",contactList.get(position).getNumber());
                intent.putExtra("image",contactList.get(position).getImage());
                startActivity(intent);

            }

            @Override
            public void sendMessage(int position) {
                Utils.setToastMessage(getApplicationContext(), "Send message" + position);
            }

            @Override
            public void contactDetail(int position) {
                Utils.setToastMessage(getApplicationContext(), "Contact details" + position);
            }
        });
    }
    private List<ModelContacts> allContact(){
        List<ModelContacts> contact2=new ArrayList<>();
        int result= ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        if(result!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }
//        String [] projection={ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        Cursor cursor=getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        assert cursor != null;
        while(cursor.moveToNext()){
            String nom=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String numero=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String photo=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            ModelContacts user=new ModelContacts(nom,photo,numero);
            contact2.add(user);

        }
        cursor.close();
        return contact2;
    }
    //fonction de trier des contacts par ordre alphabetique
    private List<ModelContacts>sortList(List<ModelContacts>person){
        Collections.sort(person, new Comparator<ModelContacts>() {
            @Override
            public int compare(ModelContacts modelContacts, ModelContacts t1) {
                return modelContacts.getName().compareTo(t1.getName());
            }

        });
        return person;
    }
    @SuppressLint("NewApi")
    public void call(String numero ){
        System.out.println(numero);
        Intent intent=new Intent(Intent.ACTION_DIAL);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setData(Uri.parse(numero));
        Utils.setToastMessage(getApplicationContext(),numero);
        if(ContextCompat.checkSelfPermission(getApplicationContext()
                , Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            Utils.setToastMessage(getApplicationContext(),"Svp veuillez obtenir une permission");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1);
        }else{
            startActivity(intent);
        }
    }
}
