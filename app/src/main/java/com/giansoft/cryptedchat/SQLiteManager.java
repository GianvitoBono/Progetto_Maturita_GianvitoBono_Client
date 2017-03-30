package com.giansoft.cryptedchat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

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
            sqLiteHelper.getWritableDatabase().execSQL("DELETE from users WHERE tel LIKE '" + Crypter.encrypt("Nome1") + "'");
            sqLiteHelper.getWritableDatabase().execSQL("INSERT INTO users VALUES('" + Crypter.encrypt(tel) + "', '" + Crypter.encrypt(name) + "', '" + Crypter.encrypt(surname) + "', '" + Crypter.encrypt(username) + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessages(String tel, String message, String sent) {
        try {
            sqLiteHelper.getWritableDatabase().execSQL("INSERT INTO messages VALUES('" + Crypter.encrypt(tel) + "', '" + Crypter.encrypt(message) + "', '" + Crypter.encrypt(sent) + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Contact> getUsers() {
        try {
            ArrayList<Contact> contacts = new ArrayList<>();
            Cursor c = sqLiteHelper.getReadableDatabase().rawQuery("SELECT name, surname, nickname FROM users", null);
            if (c.moveToFirst()) {
                do {
                    String name = Crypter.decrypt(c.getString(c.getColumnIndex("name")));
                    String surname = Crypter.decrypt(c.getString(c.getColumnIndex("surname")));
                    String username = Crypter.decrypt(c.getString(c.getColumnIndex("nickname")));
                    System.out.println(" ------------------------- " + name + "     " + surname + "       " + username);
                    contacts.add(new Contact(name, surname, username));
                } while (c.moveToNext());
                return contacts;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
