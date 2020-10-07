package com.example.calldetect.messageriePrincipale.BannierePersonnaliser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.calldetect.R;

public class BanniereCustomiser extends Dialog {

    private TextView nomInconnue, numeroInconue, letterCircleImage;
    private ImageButton confirmer, refuser, signaler, bloquer , appeller, message, ajouter, cancel;
    private Button validerNom;
    private EditText saisirNomInconue;
    private String strnomInconnue, strnumeroInconue, strletterCircleImage;
    private int couleur;
    private LinearLayout linearLayout;
    Context context;

    //definition du constructeur
    public  BanniereCustomiser (Activity activity){
        super(activity, R.style.Theme_AppCompat_Dialog);
        setContentView(R.layout.item_banniere_customiser);

        // ici on instancie les attributs
        context = activity;

        this.strnomInconnue = "callDetect";
        this.strnumeroInconue = "237";
        this.strletterCircleImage = "+";
        this.couleur = R.color.colorAccentLight;

        this.letterCircleImage = findViewById(R.id.textViewCircleImageView);
        this.nomInconnue = findViewById(R.id.label_numero);
        this.numeroInconue = findViewById(R.id.numero_banniere);
        this.confirmer = findViewById(R.id.banniereConfirmer);
        this.refuser = findViewById(R.id.banniereRefus);
        this.signaler = findViewById(R.id.banniereSignaler);
        this.bloquer = findViewById(R.id.banniereBlocquer);
        this.validerNom = findViewById(R.id.btnSgdNom);
        this.appeller = findViewById(R.id.banniereappeller);
        this.message = findViewById(R.id.banniereMessage);
        this.ajouter = findViewById(R.id.banniereAjout);
        this.saisirNomInconue = findViewById(R.id.EditTextNom);
        this.cancel = findViewById(R.id.cancelBanniere);
        this.linearLayout = findViewById(R.id.cadre);
    }

    public void setNomInconne(String nom){
        this.strnomInconnue = nom;
    }

    public void setNumeroInconue(String Numero){
        this.strnumeroInconue = Numero;
    }

    public  void setfirtletterInconne(String letter){
        this.strletterCircleImage = letter;
    }

    public void setCouleurCardre(int couleur){
        this.couleur = couleur;

    }

    public String getSaisieNomInconue(){
        return saisirNomInconue.getText().toString();
    }

    public ImageButton btnConfirmer(){
        return confirmer;
    }

    public  ImageButton btnRefuser(){
        return refuser;
    }

    public ImageButton btnBloquer(){
        return bloquer;
    }

    public ImageButton btnSignaler(){
        return signaler;
    }

    public  Button BtnValiderNom(){
        return validerNom;
    }

    public  ImageButton btnAppeller(){
        return appeller;
    }

    public ImageButton btnMessage(){
        return message;
    }

    public ImageButton btnCancel(){
        return cancel;
    }

    public ImageButton btnAjouterContact(){
        return ajouter;
    }


    public  void build(){
        show();
        nomInconnue.setText(strnomInconnue);
        numeroInconue.setText(strnumeroInconue);
        letterCircleImage.setText(strletterCircleImage);
        linearLayout.setBackgroundColor(context.getResources().getColor( couleur));

    }
}
