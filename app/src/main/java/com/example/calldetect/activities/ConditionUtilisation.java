package com.example.calldetect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;

import com.example.calldetect.R;
import com.example.calldetect.utils.Utils;
import com.google.android.material.checkbox.MaterialCheckBox;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ConditionUtilisation extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition_utilisation);
        final Button nextBtn = findViewById(R.id.next_btn);
        MaterialCheckBox checkBox = findViewById(R.id.check_condition);
        WebView webView = findViewById(R.id.webView);
        nextBtn.setEnabled(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    nextBtn.setEnabled(true);
                else
                    nextBtn.setEnabled(false);
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the user has the permission to read callLogs, and contacts.

                if (ActivityCompat.checkSelfPermission(ConditionUtilisation.this, Manifest.permission.READ_CONTACTS)
                        != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ConditionUtilisation.this,
                        Manifest.permission.READ_CALL_LOG) != PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ConditionUtilisation.this,
                            new String[]{Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.READ_CALL_LOG}, 1);
                } else {
                    startActivity(new Intent(ConditionUtilisation.this, Home.class));
                    finish();
                }

            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.condition_utilisation);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        webView.loadData("<html>\n" +
                        "    <body>\n" +
                        "        <h1>Conditions générales d'utilisation de l’application Mobile Call Detect</h1>\n" +
                        "        <p>En vigueur au 01/07/2020</p>\n" +
                        "        <br/>\n" +
                        "        <h2>ARTICLE 1 : INTRODUCTION </h2>\n" +
                        "        <p>La société DIGITECH, SAS au capital de 1 000 000 XAF, exploite l’Application Mobile « Call Detect» ci-après dénommée « l’Application Mobile » ou « le Service »;\n" +
                        "            <br/>Le directeur de la publication du Service est le Département des Opérations DiGITECH SAS de l’adresse électronique du contact est: dop@digit.tech\n" +
                        "            <br/>L’Hébergeur de l’Application Mobile est Google LLC Société immatriculée dans l'État du Delaware (États-Unis) et régie par la législation américaine /1600 Amphitheatre Parkway Mountain View, Californie, 94043 États-Unis.\n" +
                        "        </p>\n" +
                        "        <br/><br/>\n" +
                        "        <h2>ARTICLE 2 : OBJET ET CHAMP D’APPLICATION DES CGU</h2>\n" +
                        "        <p>Les présentes Conditions Générales d’Utilisation décrites ci-après et ci-après dénommées « CGU », régissent les rapports entre CALL DETECT et les Utilisateurs, ci-après dénommés « l’Utilisateur » ou « les Utilisateurs » de l’Application Mobile, et s’appliquent sans restriction ni réserve pour toute utilisation ou téléchargement de l’Application Mobile.\n" +
                        "            <br/>L’Utilisateur est tenu d’accepter les présentes CGU pour tout téléchargement et pour toute utilisation de l’Application Mobile en cochant la case « J’atteste avoir lu et accepté les Conditions Générales d’Utilisation ».\n" +
                        "            <br/>Les présentes CGU sont accessibles à tout moment dans l’Application Mobile dans la rubrique « Conditions et Pol. de confidentialité » qui se trouve dans l’onglet «Paramètres » menu principal et prévaudront, le cas échéant, sur toute autre version ou tout autre document contradictoire.\n" +
                        "            <br/>CALL DETECT pourra modifier les présentes CGU à tout moment, sans préavis et sans avoir à le communiquer préalablement aux Utilisateurs, dès lors que cela lui semble opportun. Les Utilisateurs seront informés de la modification des présentes par le biais de la publication des CGU actualisées. Les CGU modifiées prendront effet à partir de leur publication. La poursuite de l’utilisation du Service suite à cette publication constituera une acceptation de la nouvelle version des présentes.\n" +
                        "            <br/>Il est donc conseillé aux Utilisateurs de lire très attentivement les présentes CGU et de consulter régulièrement les CGU mises à jour, dans la Rubrique « Conditions et Pol. de confidentialité » de l’Application Mobile ou sur l’onglet « Mention Légale» du site CALL DETECT (www.Call Detect.com).\n" +
                        "            <br/>Dans le cas où un Utilisateur n’accepterait pas les présentes CGU ou refuserait de s’y conformer, il ne doit pas utiliser l’Application Mobile. Dans ce cas, il est recommandé de désinstaller l’Application Mobile. En procédant à son Inscription, l’Utilisateur confirme avoir pris connaissance des présentes CGU et accepte de s'y soumettre sans réserve.\n" +
                        "            <br/>Sauf preuve contraire, les données enregistrées dans le système informatique de l’Application Mobile constituent la preuve de l’ensemble des transactions conclues avec les Utilisateurs.\n" +
                        "        </p>\n" +
                        "        <br/><br/>\n" +
                        "        <h2>ARTICLE 3: PRÉSENTATION GÉNÉRALE DU SERVICE </h2>\n" +
                        "        <h3><u>3.1 Objet du service</u></h3>\n" +
                        "        <p>Call Detect est une application de renseignement électronique. Elle permet de de lutter contre l’insécurité électronique, et le renseignement électronique.</p>\n" +
                        "        <h3><u>3.2 Fonctionnement du service</u></h3>\n" +
                        "        <p>L’Application Mobile CALL DETECT peut être téléchargée via les boutiques d’applications mobiles (telles que notamment Google Play Store, Apple AppStore) au moyen des technologies disponibles, et notamment d'un terminal mobile de type smartphone.\n" +
                        "            <br/>Pour bénéficier du Service, l’Utilisateur doit donc disposer d’un smartphone, d’un accès internet et d’un Compte personnel d’Utilisateur (cf. Article 4).\n" +
                        "        </p>\n" +
                        "        <p>Les fonctionnalités de l'application mobile CallDetect sont les suivantes :</p>\n" +
                        "        <ul>\n" +
                        "            <li>Langue: Français / Anglais</li>\n" +
                        "            <li>Création d'un compte ou possibilité de se connecter facilement grâce à ses identifiants Facebook, Twitter, Google, ...</li>\n" +
                        "            <li>Gestion du répertoire</li>\n" +
                        "            <li>Gestion des appels</li>\n" +
                        "            <li>Gestion des SMS</li>\n" +
                        "            <li>Détection de l'identité des contacts inconnus</li>\n" +
                        "            <li>Gestion du profil utilisateur</li>\n" +
                        "            <li>Gestion des parametres</li>\n" +
                        "            <li>Signalement de contact</li>\n" +
                        "        </ul>\n" +
                        "<!--        TODO Continuer la suite de la construction de ce fichier *Condition d'utilisation de cette application* -->\n" +
                        "    </body>\n" +
                        "</html>",
                "text/html", "UTF-8");

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RESULT_OK){
            if (permissions[0].equals(Manifest.permission.READ_CONTACTS) && permissions[1].equals(Manifest.permission.READ_CALL_LOG)) {
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                    startActivity(new Intent(ConditionUtilisation.this, Home.class));
                    finish();
                }else {
                    Utils.setToastMessage(this, getString(R.string.permission_rejected));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}
