package com.example.calldetect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.calldetect.R;
import com.example.calldetect.firebase_data_base_manager.UserHelper;
import com.example.calldetect.models.User;
import com.example.calldetect.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetProfile extends AppCompatActivity {

    private static final String TAG = "GetProfile";
    private TextInputEditText edit_name, edit_surname;
    private AutoCompleteTextView edit_prof;
    private User user = new User();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser userF = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_profile);

        edit_name = findViewById(R.id.edit_name);
        edit_surname = findViewById(R.id.edit_surname);
        edit_prof = findViewById(R.id.edit_prof);

        String[] materialList = getResources().getStringArray(R.array.allProfessions);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.item_drop_down_profession, materialList);
        edit_prof.setAdapter(adapter);

        findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    public boolean validate () {
        boolean validate = true;
        String name = "", surname = "";
        if (edit_name.getText() != null)
            name = edit_name.getText().toString();
        if (edit_surname.getText() != null)
            surname = edit_surname.getText().toString();
        String profession = this.edit_prof.getText().toString();

        user.setId(userF.getUid());
        user.setPhone(userF.getPhoneNumber());
        user.setName(name);
        user.setSurname(surname);
        user.setProfession(profession);

        if (name.equals("")) {
            validate = false;
            edit_name.setError(getString(R.string.this_field_can_be_empty));
        }
        if (surname.equals("")) {
            validate = false;
           edit_surname.setError(getString(R.string.this_field_can_be_empty));
        }
        if (profession.equals("")) {
            validate = false;
            edit_prof.setError(getString(R.string.this_field_can_be_empty));
        }
        return validate;
    }

    public void submit () {
        if (validate()) {
            // Sauvegarde de l'utilisateur.
            UserHelper.addSimpleUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(GetProfile.this, ConditionUtilisation.class));
                        finish();
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseNetworkException)
                            Utils.setDialog(GetProfile.this, getString(R.string.require_network));
                        else{
                            Utils.setToastMessage(GetProfile.this, getString(R.string.error_has_provide));
                            onBackPressed();
                        }
                        Log.e(TAG, "onComplete: ", e);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        // Log out this user.
        auth.signOut();
        finish();
    }
}