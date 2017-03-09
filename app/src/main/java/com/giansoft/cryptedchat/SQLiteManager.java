package com.giansoft.cryptedchat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by gianv on 09/03/2017.
 */

public class SQLiteManager {

    private SQLiteHelper sqLiteHelper;

    public SQLiteManager(Context context) {
        sqLiteHelper = new SQLiteHelper(context);
    }

    public void addUser(String name, String surname, String username, String tel) {
        try {
            sqLiteHelper.getWritableDatabase().execSQL("INSERT INTO users VALUES(" + Crypter.encrypt(name) + ", "+ Crypter.encrypt(surname) + ", " + Crypter.encrypt(username) + ", " + Crypter.encrypt(tel) + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessages(String tel, String message, String sent) {
        try {
            sqLiteHelper.getWritableDatabase().execSQL("INSERT INTO messages VALUES(" + Crypter.encrypt(tel)+ ", " + Crypter.encrypt(message) + ", " + Crypter.encrypt(sent) + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
