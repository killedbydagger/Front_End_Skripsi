package com.example.skripsi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String ID = "ID";
    public static final String EMAIL = "EMAIL";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String PHONE = "PHONE";
    public static final String GENDER = "GENDER";
    public static final String DOB = "DOB";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String STATUS = "STATUS";
    public static final String EDUCATION_ID = "EDUCATION_ID";
    public static final String EDUCATION_NAME = "EDUCATION_NAME";
    public static final String LOCATION_ID = "LOCATION_ID";
    public static final String LOCATION_NAME = "LOCATION_NAME";
    public static final String LOCATION_DATA = "LOCATION_DATA";
    public static final String EDUCATION_DATA = "EDUCATION_DATA";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LOGIN",PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id,String email, String firstName, String lastName, String phone, String gender, String dob, String description, String user_status, String education_id, String education_name,
                              String location_id, String location_name){
        editor.putBoolean(LOGIN,true);
        editor.putString(ID,id);
        editor.putString(EMAIL,email);
        editor.putString(FIRST_NAME,firstName);
        editor.putString(LAST_NAME,lastName);
        editor.putString(PHONE,phone);
        editor.putString(GENDER,gender);
        editor.putString(DOB,dob);
        editor.putString(DESCRIPTION,description);
        editor.putString(STATUS,user_status);
        editor.putString(EDUCATION_ID,education_id);
        editor.putString(EDUCATION_NAME,education_name);
        editor.putString(LOCATION_ID,location_id);
        editor.putString(LOCATION_NAME,location_name);

        editor.apply();
    }

    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){
        if (!this.isLogin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainMenu) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(FIRST_NAME, sharedPreferences.getString(FIRST_NAME, null));
        user.put(LAST_NAME, sharedPreferences.getString(LAST_NAME, null));
        user.put(PHONE, sharedPreferences.getString(PHONE, null));
        user.put(GENDER, sharedPreferences.getString(GENDER, null));
        user.put(DOB, sharedPreferences.getString(DOB, null));
        user.put(DESCRIPTION, sharedPreferences.getString(DESCRIPTION, null));
        user.put(STATUS, sharedPreferences.getString(STATUS, null));
        user.put(EDUCATION_ID, sharedPreferences.getString(EDUCATION_ID,null));
        user.put(EDUCATION_NAME, sharedPreferences.getString(EDUCATION_NAME,null));
        user.put(LOCATION_ID, sharedPreferences.getString(LOCATION_ID,null));
        user.put(LOCATION_NAME, sharedPreferences.getString(LOCATION_NAME,null));
        user.put(LOCATION_ID, sharedPreferences.getString(LOCATION_ID,null));
        user.put(LOCATION_NAME, sharedPreferences.getString(LOCATION_NAME,null));
        user.put(LOCATION_DATA, sharedPreferences.getString(LOCATION_DATA,null));
        user.put(EDUCATION_DATA, sharedPreferences.getString(EDUCATION_DATA,null));
        return user;
    }

    public void logout(){

        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainMenu) context).finish();
    }
}
