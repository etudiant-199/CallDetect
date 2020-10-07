package com.example.calldetect.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.calldetect.R;
import com.example.calldetect.firebase_data_base_manager.ActionAboutUsers;
import com.example.calldetect.firebase_data_base_manager.ImageStorageHelper;
import com.example.calldetect.firebase_data_base_manager.UserHelper;
import com.example.calldetect.models.User;
import com.example.calldetect.models.Users_Simple;
import com.example.calldetect.utils.Utils;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile";
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    ShapeableImageView avatar;
    MaterialToolbar toolbar;
    TextView user_name, user_number, user_email;
    private User user = null;
    private Uri imageUri;
    private boolean profileIsSet = false;
    private int requestProfileCode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        // Init the support action bar.
        setActionBar();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
        }
        avatar = findViewById(R.id.avatar);
        avatar.setShapeAppearanceModel(ShapeAppearanceModel.builder()
                .setAllCornerSizes(ShapeAppearanceModel.PILL)
                .build());
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickProfile();
            }
        });
        user_name = findViewById(R.id.user_name);
        user_number = findViewById(R.id.user_number);
        user_email = findViewById(R.id.user_email);
        setViewValue();
    }
    private void setActionBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        backHome();
        return true;
    }
    /**
     * Function to back to the home page.
     */
    private void backHome() {
        startActivity(new Intent(this, Home.class));
        finish();
    }
    /**
     * Function to set the views values.
     */
    private void setViewValue() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null,
                getString(R.string.wait));
        UserHelper.getUserById(mCurrentUser.getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                           updateUserProfileViews();
                           profileIsSet = true;
                        }
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Utils.setDialog(Profile.this,
                                getString(R.string.error_not_connect));
                        Log.e(TAG, "onFailure: error has provided. ===> ", e);
                    }
                });

    }
    public void disconnected(View view) {
        mAuth.signOut();
        sendUserToLogin();
    }
    private void sendUserToLogin() {
        Intent loginIntent = new Intent(this, Login.class);
        Bundle bundle = new Bundle();
        bundle.putCharSequence(Utils.EXTRA_ACTIVITY, "Profile");
        startActivity(loginIntent, bundle);
        finish();
    }
    @Override
    public void onBackPressed() {
        backHome();
    }
    /**
     * Function to pick an image to user profile.
     */
    private void pickProfile() {
        if (!profileIsSet) {
            Utils.setDialog(Profile.this, getString(R.string.wait_for_profile));
            return;
        }
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
                    uploadUserProfile();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    Log.e(TAG, "onActivityResult: Error where cropping the image : ", result.getError());
        }
    }
    /**
     * Function to upload the profile of the user to fireStorage.
     */
    private void uploadUserProfile () {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null,
                getString(R.string.wait));
        UploadTask uploadTask ;
        if (user.getProfile().equals("")) {
            String imagePath = System.currentTimeMillis() + Utils.getFileExtension(imageUri, this);
            user.setProfile(imagePath);
            uploadTask = ImageStorageHelper.add(imagePath, imageUri, this);
        }else
            uploadTask = ImageStorageHelper.uploadProfile(user.getProfile(), imageUri, this);
        if (uploadTask != null) {
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    if (e instanceof FirebaseNetworkException)
                        Utils.setDialog(Profile.this, getString(R.string.error_not_connect));
                    else
                        Utils.setDialog(Profile.this, getString(R.string.error_has_provide));
                    Log.e(TAG, "error has provided : ", e);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserHelper.updateUser(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Utils.setToastMessage(Profile.this, getString(R.string.update));
                                    updateUserProfileViews();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    if (e instanceof FirebaseFirestoreException)
                                        Utils.setDialog(Profile.this,
                                                getString(R.string.error_not_connect));
                                    Log.e(TAG, "onFailure: Error : ", e);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.setToastMessage(Profile.this,
                                    getString(R.string.error_has_provide));
                        }
                    });
                }
            });
        }
    }
    /**
     * Function to update the user information.
     */
    private void updateUserProfileViews() {
        String user_nameStr = user.getName() + " " + user.getSurname();
        user_number.setText(user.getEmail());
        user_email.setText(user.getEmail());
        user_name.setText(user_nameStr);

        toolbar.setTitle(user.getSurname() + " " + user.getName());

        if (!user.getProfile().equals("")) {
            if (imageUri != null) {
                Glide.with(Profile.this)
                        .load(imageUri)
                        .into(avatar);
            } else {
                ImageStorageHelper.getStorageReference().child(user.getProfile())
                        .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (!task.isSuccessful()) {
                            if (task.getException() instanceof FirebaseNetworkException)
                                Utils.setDialog(Profile.this, getString(R.string.error_not_connect));
                            else
                                Utils.setDialog(Profile.this, getString(R.string.error_has_provide));
                            return;
                        }
                        Glide.with(Profile.this)
                                .load(task.getResult())
                                .placeholder(R.mipmap.image_profile_foreground)
                                .error(R.mipmap.image_profile_foreground)
                                .into(avatar);
                    }
                });
            }
        }
    }
    /**
     * Click listener to edit name, phoneNumber button.
     * @param view View.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void edit_name_number(View view) {
        new MaterialAlertDialogBuilder(this)
                .setView(R.layout.layout_edit_name_number)
                .setBackground(getResources().getDrawable(R.drawable.bg_dialog, getTheme()))
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog view = (AlertDialog) dialog;
                        EditText edit_name = view.findViewById(R.id.edit_name);
                        EditText edit_surname = view.findViewById(R.id.edit_surname);
                        CountryCodePicker ccp = view.findViewById(R.id.ccp);
                        EditText edit_phone_number = view.findViewById(R.id.edit_phone_number);
                        assert ccp != null;
                        ccp.registerCarrierNumberEditText(edit_phone_number);
                        assert edit_name != null;
                        assert edit_surname != null;
                        String number;
                        assert edit_phone_number != null;
                        if (edit_phone_number.getText().toString().trim().equals(""))
                            number = "";
                        else
                            number = ccp.getFullNumberWithPlus();
                        checkFromValidation(edit_name.getText().toString(), edit_surname.getText().toString(),
                                number);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
    /**
     * Function to checked the validation of the edit name number form.
     */
    private void checkFromValidation(String name, String surname, String phoneNumber) {
        if (!profileIsSet || (name.equals("") && surname.equals("") && phoneNumber.equals(""))){
            Utils.setDialog(Profile.this, getString(R.string.wait_for_profile));
            return;
        }
        if (!name.equals(""))
            user.setName(name);
        if (!surname.equals(""))
            user.setSurname(surname);
        if (phoneNumber.length() > 7)
            user.setPhone2(phoneNumber);
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.wait));
        UserHelper.updateUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (!task.isSuccessful()){
                    Utils.setDialog(Profile.this, getString(R.string.error_not_connect));
                    return;
                }
                String userName = user.getName() + " " + user.getSurname();
                user_name.setText(userName);
                user_number.setText(user.getEmail());
                Utils.setToastMessage(Profile.this, getString(R.string.update));
            }
        })
        .addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                progressDialog.dismiss();
                Utils.setToastMessage(Profile.this, "The operation is cancelled");
            }
        });
    }
}
