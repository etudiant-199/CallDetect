package com.example.calldetect.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calldetect.R;
import com.example.calldetect.database.DataBaseManager;
import com.example.calldetect.firebase_data_base_manager.UserHelper;
import com.example.calldetect.messageriePrincipale.infoUser.InfoUser;
import com.example.calldetect.models.User;
import com.example.calldetect.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.TimeUnit;

public class Confirm extends AppCompatActivity {
    private static final String TAG = "Confirm";
    private EditText code;
    private TextView phone_code_message;
    TextView error;
    private TextView text_counter;
    private Button send, resend_code;
    private String verificationCodeBySystem;
    private String phone_number = "none";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private int time = 10;
    private Handler handlerTime = new Handler();
    private int[] timer = {0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        //initialisation
        initViews();
        //recuperation de l'extra
        Intent intentExtract = getIntent();
        if (intentExtract.hasExtra("Phone_number"))
            phone_number = getIntent().getStringExtra("Phone_number");
        String text_message = getString(R.string.send_code_msm) + " "
                + "<font color='#000'><b>" + phone_number + "</b></font>";
        phone_code_message.setText(Html.fromHtml(text_message));
        sendSMS(phone_number);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code_user = code.getText().toString().trim();
                if (code_user.length() != 6) {
                    code.requestFocus();
                    code.setError(getString(R.string.wrong_code));
                    return;
                }
                verifyCode(code_user);
            }
        });
    }
    private void initViews() {
        code = findViewById(R.id.code);
        send = findViewById(R.id.send);
        text_counter = findViewById(R.id.time_counter);
        resend_code = findViewById(R.id.resend_code_btn);
        phone_code_message = findViewById(R.id.phone_code_message);
        TextView not_mine = findViewById(R.id.not_mine);
        error = findViewById(R.id.error);
        resend_code.setEnabled(false);
        text_counter.setText(Utils.SecondsToTime(time));
        not_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Confirm.this, Login.class));
                finish();
            }
        });
        resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time += 5;
                sendSMS(phone_number);
            }
        });
    }
    public void sendSMS(String phoneNumber){
        Log.i(TAG, "EnvoieSms: Phone number :  " + phoneNumber);
        resend_code.setEnabled(false);
        PhoneAuthProvider.getInstance(FirebaseAuth.getInstance()).verifyPhoneNumber(
                phoneNumber,
                time,
                TimeUnit.SECONDS,
                this,
                mCallBack
        );
        timer[0] = time;
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider
            .OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                signWithCredential(phoneAuthCredential);
            }
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.e(TAG, "Error has provided with number => "+ phone_number +" : ", e);
            String message;
            if (e instanceof FirebaseNetworkException)
                message = getString(R.string.require_network);
            else
                message = getString(R.string.error_has_provide) + " " +
                        getString(R.string.verify_phone_number);
            new MaterialAlertDialogBuilder(Confirm.this)
                    .setMessage(message +" error ====> " + e.getMessage())
                    .setPositiveButton(R.string.OK, null)
                    .show();
            resend_code.setEnabled(true);
        }
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider
                .ForceResendingToken forceResendingToken) {
            verificationCodeBySystem = s;
            runnable.run();
            Log.e(TAG, "onCodeSent: The SMS has sent to this user. "+ s);
        }
        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            Log.e(TAG, "onCodeAutoRetrievalTimeOut: Time is over.");
            resend_code.setEnabled(true);
        }

    };
    /**
     * Sing the user with the credential.
     * @param credential Credential.
     */
    private void signWithCredential(PhoneAuthCredential credential) {
        final ProgressDialog pd = ProgressDialog.show(this, null, getString(R.string.wait));
        auth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if (task.isSuccessful()) { // Si l'authentification est OK.
                            UserHelper.getUserById(auth.getUid())
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                if (task.getException() instanceof FirebaseFirestoreException)
                                                    Utils.setDialog(Confirm.this,
                                                            getString(R.string.error_not_connect));
                                                Log.e(TAG, "onFailure: Error : ", task.getException());
                                            }
                                            if (task.getResult() != null) {
                                                User user = task.getResult().toObject(User.class);
                                                if (user == null) {
                                                    Intent intent = new Intent(Confirm.this, GetProfile.class);
                                                    startActivity(intent);
                                                } else {
                                                    sauvegardeInfoUser();// je sauvegarde les inf user
                                                    Intent intent = new Intent(Confirm.this, ConditionUtilisation.class);
                                                    startActivity(intent);
                                                }
                                                finish();
                                            }
                                        }
                                    });
                        } else { // En cas d'erreur
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Utils.setDialog(Confirm.this,
                                        getString(R.string.error_invalid_code));
                            }
                            else if (task.getException() instanceof FirebaseNetworkException) {
                                Utils.setDialog(Confirm.this,
                                        getString(R.string.error_not_connect));
                            }
                            else  {
                                Log.e(TAG, "onFailure: Error when login the user.", task.getException());
                                Utils.setToastMessage(Confirm.this, getString(R.string.error_has_provide));

                                Intent intent = new Intent(Confirm.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
    }
    // Update the time counter.
     private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timer[0]--;
            if (timer[0] < 0){
                handlerTime.removeCallbacks(runnable);
                resend_code.setEnabled(true);
                return;
            }
            text_counter.setText(Utils.SecondsToTime(timer[0]));
            handlerTime.postDelayed(runnable, 1000);
        }
    };
    /**
     * Function to make the user authentification.
     * @param codeByUser User code generation.
     */
    private void verifyCode(String codeByUser){
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signWithCredential(credential);
        }catch (Exception e) {
            Log.e(TAG, "verifyCode: Error coming ", e);
        }
    }


    /**
     * petite focntion pour sauvegarder des info de lutilissateur dans le sqlite
     */
    public void sauvegardeInfoUser(){
        DataBaseManager manager = new DataBaseManager(this);
        manager.insertUserInfo(new InfoUser(getIntent().getStringExtra("Phone_number"),getIntent().getStringExtra("pays"), getIntent().getStringExtra("codepays")));
        manager.close();
    }
}
