package com.example.calldetect.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.adapters.CallLogAdapter;
import com.example.calldetect.firebase_data_base_manager.SignalNumberHelper;
import com.example.calldetect.messageriePrincipale.messageCustomiser.Activity_message_personaliser;
import com.example.calldetect.models.ModelCalls;
import com.example.calldetect.models.SignalNumber;
import com.example.calldetect.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CallInfoActivity extends AppCompatActivity {
    private static final String TAG = "CallInfoActivity";
    ModelCalls contacts;
    private TextView phone_number, phone_type, localisation;
    private RecyclerView log_recycler;
    private CallLogAdapter adapter;
    private boolean isLocked = false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_info);
        contacts = getIntent().getParcelableExtra("Contact");
        List<com.example.calldetect.models.CallLog> callLogs = new ArrayList<>();
        final MaterialToolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        ArrayList<Parcelable> c = getIntent().getParcelableArrayListExtra("CallLogs");
        assert c != null;
        for (Parcelable parcelable : c)
            callLogs.add((com.example.calldetect.models.CallLog) parcelable);
        adapter = new CallLogAdapter(callLogs);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(contacts.getName());
        }
        SignalNumberHelper.isSignal(contacts.getNumber())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.i(TAG, "Signalisation Document: " + documentSnapshot.toObject(SignalNumber.class));
                        if (documentSnapshot.toObject(SignalNumber.class) != null)
                            findViewById(R.id.is_signal).setVisibility(View.VISIBLE);
                        else
                            findViewById(R.id.is_signal).setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("CallInfoActivity", "getting signalisation of number ", e);
                    }
                });
        initializeViews();
        setViewsValues();
    }
    private void setViewsValues() {
        phone_number.setText(contacts.getNumber());
        phone_type.setText(getString(R.string.mobil));
        log_recycler.setLayoutManager(new LinearLayoutManager(this));
        log_recycler.setHasFixedSize(true);
        log_recycler.setAdapter(adapter);
    }
    private void initializeViews() {
        phone_number = findViewById(R.id.phone_number);
        phone_type = findViewById(R.id.phone_type);
        localisation = findViewById(R.id.localisation);
        log_recycler = findViewById(R.id.log_recycler);
        findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.call(CallInfoActivity.this, contacts.getNumber());
            }
        });
        findViewById(R.id.btn_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CallInfoActivity.this, Activity_message_personaliser.class);
                intent.putExtra("number", contacts.getNumber());
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_call_log, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.signal_option:
                Utils.signalNumber(contacts.getNumber(), this);
                break;
            case R.id.favorite_option:
                Utils.addToFavorite(contacts.getNumber(), this);
                break;
            case R.id.block_option:
                lockManager(item);
                break;
            case R.id.share_option:
                Utils.shareNumber(contacts.getNumber(), this);
                break;
            case R.id.invite_option:
                Utils.inViteNumber(contacts.getNumber(), this);
                break;
            case R.id.delete_option:
                Utils.deleteCallLog(contacts.getNumber(), this);
                break;
            default:
                finish();
                break;
        }
        return true;
    }

    private void lockManager(MenuItem item) {
        if (isLocked) {
            Utils.unblockPhoneNumber(this, contacts.getNumber());
            item.setIcon(R.drawable.ic_lock_on_option);
        } else {
            Utils.blockNumber(this, contacts.getNumber());
            item.setIcon(R.drawable.ic_lock_off_option);
        }
    }
}
