package com.example.calldetect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calldetect.R;
import com.example.calldetect.models.ModelCalls;
import com.example.calldetect.utils.Utils;

public class CallActivity extends AppCompatActivity {
   private ImageView imageContact;
   private TextView nomContact;
   private TextView chronoAppel;
   String nom;
   ModelCalls calls;
   private Button Micro,haut_parleur,clavier,ajouter,enregistrer,attente, accepter,rejeter;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
       initViews();
        Intent intent = getIntent();

        recreate();

        if (intent.hasExtra("Contact")){
            calls = intent.getParcelableExtra("Contact");
        }

        Utils.setToastMessage(this, "Call this contact : " + calls);

         imageContact.setImageDrawable(Drawable.createFromPath(intent.getStringExtra("image")));
         nom=intent.getStringExtra("nom");

         if(nom == null){
             nomContact.setText(intent.getStringExtra("numero"));
         }else{
             nomContact.setText(nom);
         }

    }

    private void initViews() {
        imageContact=findViewById(R.id.icon);
        nomContact=findViewById(R.id.nomAppelant);
        chronoAppel=findViewById(R.id.Chrono);
    }


}
