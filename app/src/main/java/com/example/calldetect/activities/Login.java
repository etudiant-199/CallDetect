package com.example.calldetect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calldetect.R;
import com.example.calldetect.utils.Utils;
import com.hbb20.CountryCodePicker;

public class Login extends AppCompatActivity {
    private boolean is_logout = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText phone_number = findViewById(R.id.phone_number);
        final CountryCodePicker ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phone_number);

        Button submitBtn = findViewById(R.id.sauvegarde);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_number.getText().toString().equals("")){
                    phone_number.requestFocus();
                    phone_number.setError(getResources().getString(R.string.empty_phone_value));
                    return;
                }
                Intent confirmIntent = new Intent(Login.this, Confirm.class);
                confirmIntent.putExtra("Phone_number", ccp.getFullNumberWithPlus());
                confirmIntent.putExtra("pays", ccp.getSelectedCountryName());
                confirmIntent.putExtra("codepays" , ccp.getSelectedCountryCode());
                startActivity(confirmIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (is_logout)
            System.exit(RESULT_OK);
        else
            finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (data.getCharSequenceExtra(Utils.EXTRA_ACTIVITY) == "Profile"){
                is_logout = true;
            }
        }
    }



}
