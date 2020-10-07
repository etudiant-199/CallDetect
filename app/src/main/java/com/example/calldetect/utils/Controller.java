package com.example.calldetect.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.calldetect.models.Temp_information_user;
import com.example.calldetect.models.Users_Simple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {


    private static final String FILE_NAME = "user_Information";

    public static void delete_file(Context context){
        context.deleteFile(FILE_NAME);
    }


    private static boolean verif_file_exist(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        return file.isFile();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean verif_file(Context context) {
        if (verif_file_exist(context)) {
            String content_of_file = Controller.take_information_of_file_users(context);
            return content_of_file != null && !content_of_file.equals(" ");
        } else {
            return false;
        }
    }

    public static void create_file(Users_Simple usersSimple, Context context) {
        if (usersSimple != null) {
            String number = usersSimple.getLogin();
            String user_name = usersSimple.getName() + " " + usersSimple.getPrenom();
            String contenu_ecrit = number + "  " + user_name;
            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                fileOutputStream.write(contenu_ecrit.getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            String contenu_ecrit = " ";
            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                fileOutputStream.write(contenu_ecrit.getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String take_information_of_file_users(Context context) {
        String information_user_take;

        FileInputStream fis = null;
        try {
            fis = context.openFileInput(FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert fis != null;
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }

            reader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            information_user_take = stringBuilder.toString();
        }
        return information_user_take;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Temp_information_user Trans_to_Temp_information_user(Context context){
        Temp_information_user temp_information_user;
        String information_user = Controller.take_information_of_file_users(context);
        if(information_user != null){
            String [] partition = information_user.split("  ");
            temp_information_user = new Temp_information_user(partition[0], partition[1]);
        }else {
            temp_information_user = new Temp_information_user();
        }
        return temp_information_user;
    }

    public static boolean isNumberValid(String number)
    {
        /* In Cameroun, Mobile is valid if below 3 condition is specified
         * 1) Begins with 6
         * 2) Then contains 9 digits
         * You can change this validation as you want
         */

        Pattern p = Pattern.compile("(6)?(5/6/7/8/9)?[0-9]{8}");

        Matcher matcher  = p.matcher(number);

        return (matcher.matches());
    }







}
