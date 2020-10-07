package com.example.calldetect.firebase_data_base_manager;

import android.content.Context;
import android.net.Uri;

import com.example.calldetect.utils.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ImageStorageHelper {

   public static StorageReference getStorageReference () {
      return FirebaseStorage.getInstance().getReference("User_Profiles");
   }

   public static UploadTask add(String imagePath, Uri mImageUri, Context context) {
      if (mImageUri != null) {
         StorageReference fileReference = getStorageReference().child(imagePath);
         return fileReference.putFile(mImageUri);
      } else {
         Utils.setToastMessage(context, "No file selected");
         return null;
      }
   }

   /**
    * Function to update the user profile.
    */
   public static UploadTask uploadProfile (String url, Uri newImage, Context context){
      if (newImage != null) {
         StorageReference fileReference = getStorageReference().child(url);
         return fileReference.putFile(newImage);
      }else{
         Utils.setToastMessage(context, "No file selected");
         return null;
      }
   }

}
