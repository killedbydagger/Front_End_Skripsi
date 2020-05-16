package com.example.skripsi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

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
    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";
    public static final String GENDER = "GENDER";
    public static final String DOB = "DOB";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String STATUS = "STUS";


    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LOGIN",PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id,String email, String name, String phone, String gender, String dob, String description, String user_status){
        editor.putBoolean(LOGIN,true);
        editor.putString(ID,id);
        editor.putString(EMAIL,email);
        editor.putString(NAME,name);
        editor.putString(PHONE,phone);
        editor.putString(GENDER,gender);
        editor.putString(DOB,dob);
        editor.putString(DESCRIPTION,description);
        editor.putString(STATUS,user_status);

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
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(PHONE, sharedPreferences.getString(PHONE, null));
        user.put(GENDER, sharedPreferences.getString(GENDER, null));
        user.put(DOB, sharedPreferences.getString(DOB, null));
        user.put(DESCRIPTION, sharedPreferences.getString(DESCRIPTION, null));
        user.put(STATUS, sharedPreferences.getString(STATUS, null));
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
