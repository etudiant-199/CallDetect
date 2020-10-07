package com.example.calldetect.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.calldetect.R;
import com.example.calldetect.firebase_data_base_manager.ImageStorageHelper;
import com.example.calldetect.firebase_data_base_manager.UserHelper;
import com.example.calldetect.fragment.DatePickerFragment;
import com.example.calldetect.models.User;
import com.example.calldetect.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

public class CertificateUser extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "GetProfile";
    private Button submit;
    private EditText birthday, deliver_date;
    private TextInputEditText name, surname, email, other_phone_number, address, cni;
    private AutoCompleteTextView profession;
    private TextInputLayout layout_name, layout_surname, layout_mail, layout_profession;
    private CountryCodePicker ccp;
    private ImageButton pick_image_profile;
    private ImageView profileImage;
    private boolean nameOK = true, surnameOK = true, emailOK = false, professionOK = true;
    private Uri imageUri;
    private boolean date_is_deliver = false;
    private int requestProfileCode = 2;
    private String sex = "F";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_user);

        // User to extrat intent.
        user = getIntent().getParcelableExtra(Utils.USER_EXTRA);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.profil_config);
        }

        initViews();

        // Listeners.
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_is_deliver = false;
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date picker");
            }
        });
        deliver_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_is_deliver = true;
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date picker");
            }
        });

        initTheTextWatchers();

        // register the phone number picker.
        ccp.registerCarrierNumberEditText(other_phone_number);

        setListener();
    }

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() < 3){
                layout_name.setError(getString(R.string.error_name));
                nameOK = false;
            }else{
                layout_name.setError(null);
                nameOK = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextWatcher surnameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() < 3){
                layout_surname.setError(getString(R.string.error_surname));
                surnameOK = false;
            }else{
                layout_surname.setError(null);
                surnameOK = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String mail = s.toString().trim();
            if (!mail.contains("@") || !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                layout_mail.setError(getString(R.string.error_mail));
                emailOK = false;
            }else{
                layout_mail.setError(null);
                emailOK = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextWatcher professionWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() < 3){
                layout_profession.setError(getString(R.string.error_profession));
                professionOK = false;
            }else{
                layout_profession.setError(null);
                professionOK = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void initTheTextWatchers() {
        name.addTextChangedListener(nameWatcher);
        surname.addTextChangedListener(surnameWatcher);
        email.addTextChangedListener(emailWatcher);
        profession.addTextChangedListener(professionWatcher);
    }

    /**
     * Function to initialize the listener.
     */
    private void setListener() {
        submit.setOnClickListener(this);
        pick_image_profile.setOnClickListener(this);
    }

    /**
     * Function to initialize the views.
     */
    private void initViews() {
        submit = findViewById(R.id.submit_btn);
        name = findViewById(R.id.edit_name);
        surname = findViewById(R.id.edit_surname);
        email = findViewById(R.id.edit_email);
        profession = findViewById(R.id.edit_prof);
        cni = findViewById(R.id.edit_cni);
        other_phone_number = findViewById(R.id.edit_phone_number);
        layout_name = findViewById(R.id.layout_name);
        layout_surname = findViewById(R.id.layout_surname);
        layout_mail = findViewById(R.id.layout_email);
        layout_profession = findViewById(R.id.layout_prof);
        ccp = findViewById(R.id.ccp);
        pick_image_profile = findViewById(R.id.pick_image);
        profileImage = findViewById(R.id.profile_image);
        address = findViewById(R.id.edit_address);
        birthday = findViewById(R.id.edit_birthday);
        deliver_date = findViewById(R.id.edit_deliver_date);
        RadioButton sex_F = findViewById(R.id.sex_F);
        RadioButton sex_M = findViewById(R.id.sex_M);

        // Default values.
        name.setText(user.getName());
        surname.setText(user.getSurname());
        profession.setText(user.getProfession(), true);

        sex_F.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    sex = "F";
            }
        });
        sex_M.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    sex = "M";
            }
        });

        String[] materialList = getResources().getStringArray(R.array.allProfessions);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.item_drop_down_profession, materialList);

        profession.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit_btn) {
            submitForm();
        } else {
            pickProfile();
        }
    }

    /**
     * Function to pick an image to user profile.
     */
    private void pickProfile() {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
        pickIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(pickIntent, getString(R.string.chose_app)),
                requestProfileCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestProfileCode) {
            if (resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();

                Log.i(TAG, "Content of image uri : " + imageUri);

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null)
                if (resultCode == RESULT_OK) {
                    imageUri = result.getUri();
                    Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.mipmap.image_profile_foreground)
                            .into(profileImage);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    Log.e(TAG, "onActivityResult: Error where cropping the image : ", result.getError());
        }
    }

    /**
     * Function to submit the form.
     */
    @SuppressLint("NewApi")
    private void submitForm() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String number = this.ccp.getFullNumberWithPlus();
        String name = Objects.requireNonNull(this.name.getText()).toString().trim();
        String surname = Objects.requireNonNull(this.surname.getText()).toString().trim();
        String email = Objects.requireNonNull(this.email.getText()).toString().trim();
        String profession = this.profession.getText().toString().trim();
        String phone_number = number.length() > 7 ? number : "";
        String image_profile = imageUri != null ? imageUri.toString() : "";
        String address = this.address.getText().toString().trim();
        String birthday = this.birthday.getText().toString().trim();
        String cni = this.cni.getText().toString().trim();
        String deliver_date = this.deliver_date.getText().toString().trim();

        User user = new User(auth.getUid(), name, surname, birthday, email, this.user.getPhone(),
                phone_number, sex, address, "", profession, true);
        user.setCni (cni);
        user.setDeliver_date(deliver_date);

        if (address.equals("")) {
            this.address.setError(getString(R.string.this_field_can_be_empty));
            return;
        }
        if (cni.equals("")) {
            this.cni.setError(getString(R.string.this_field_can_be_empty));
            return;
        }
        if (birthday.equals("")) {
            this.birthday.setError(getString(R.string.this_field_can_be_empty));
            return;
        }
        if (deliver_date.equals("")) {
            this.deliver_date.setError(getString(R.string.this_field_can_be_empty));
            return;
        }

        if (nameOK && surnameOK && emailOK && professionOK) {
            if (image_profile.equals("")) {
                final ProgressDialog progressDialog = ProgressDialog.show(this, null, getString(R.string.wait));
                UserHelper.certifyUser(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        if (e instanceof FirebaseFirestoreException)
                            Utils.setDialog(CertificateUser.this,
                                    getString(R.string.error_not_connect));
                        Log.e(TAG, "onFailure: Error : ", e);
                    }
                });
            } else {
                uploadUserProfile(user);
            }
        }else {
            Utils.setDialog(this, getString(R.string.requireFields));
        }
    }

    /**
     * Function to upload the profile of the user to fireStorage.
     */
    private void uploadUserProfile (final User user) {

        final ProgressDialog progressDialog = ProgressDialog.show(this, null,
                getString(R.string.wait));
        final String imagePath = System.currentTimeMillis() + Utils.getFileExtension(imageUri, this);
        UploadTask uploadTask = ImageStorageHelper.add(imagePath, imageUri, this);
        if (uploadTask != null) {
            uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            if (e instanceof FirebaseNetworkException)
                                Utils.setDialog(CertificateUser.this, getString(R.string.error_not_connect));
                            else
                                Utils.setDialog(CertificateUser.this, getString(R.string.error_has_provide));
                            Log.e(TAG, "error has provided : ", e);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    user.setProfile(imagePath);
                                    UserHelper.certifyUser(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            if (e instanceof FirebaseFirestoreException)
                                                Utils.setDialog(CertificateUser.this,
                                                        getString(R.string.error_not_connect));
                                            Log.e(TAG, "onFailure: Error : ", e);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Utils.setToastMessage(CertificateUser.this,
                                            getString(R.string.error_has_provide));
                                }
                            });
                        }
                    });
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String value = dayOfMonth + "/" + (month + 1) + "/" + year;
        if (date_is_deliver)
            deliver_date.setText(value);
        else
            birthday.setText(value);
    }
}
