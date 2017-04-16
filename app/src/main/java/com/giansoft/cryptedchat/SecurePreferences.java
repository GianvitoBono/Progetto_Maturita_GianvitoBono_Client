package com.giansoft.cryptedchat;

/**
 * Created by Gianvito on 10/12/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;

public class SecurePreferences {
    private static final String PREFS_NAME = "sp_125486.bin";
    private Context ctx;
    private SharedPreferences prefs;

    public SecurePreferences(Context ctx){
        this.ctx = ctx;
        prefs = this.ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key){
        try {
            return Crypter.decrypt(prefs.getString(Crypter.encrypt(key), null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getInt(String key){
        try {
            return Integer.parseInt(Crypter.decrypt(prefs.getString(Crypter.encrypt(key), null)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean getBoolean(String key){
        try {
            String str = Crypter.decrypt(prefs.getString(Crypter.encrypt(key), Crypter.encrypt("false")));
            return str.equals("true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean putString(String key, String val){
        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Crypter.encrypt(key), Crypter.encrypt(val));
            return editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean putInt(String key, int val){
        try{
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Crypter.encrypt(key), Crypter.encrypt(String.valueOf(val)));
            return editor.commit();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean putBoolean(String key, boolean val){
        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Crypter.encrypt(key), Crypter.encrypt(String.valueOf(val)));
            return editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

