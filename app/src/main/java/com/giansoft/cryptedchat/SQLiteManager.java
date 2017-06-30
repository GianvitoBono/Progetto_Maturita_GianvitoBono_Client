package com.giansoft.cryptedchat;

import android.content.Context;
import android.database.Cursor;

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
            sqLiteHelper.getWritableDatabase().execSQL("INSERT INTO users VALUES('" + Crypter.encrypt(tel) + "', '" + Crypter.encrypt(name) + "', '" + Crypter.encrypt(surname) + "', '" + Crypter.encrypt(username) + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String tel, String message, int sent) {
        try {
            sqLiteHelper.getWritableDatabase().execSQL("INSERT INTO messages (user_tel_fk, message, sent) VALUES('" + Crypter.encrypt(tel) + "', '" + Crypter.encrypt(message) + "', " + sent + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearUsers(boolean sure) {
        if(sure) {
            sqLiteHelper.getWritableDatabase().delete("users", null, null);
        }
    }

    public void clearMess(boolean sure) {
        if (sure) {
            sqLiteHelper.getWritableDatabase().delete("messages", null, null);
        }
    }


    public ArrayList<Contact> getUsers() {
        try {
            ArrayList<Contact> contacts = new ArrayList<>();
            Cursor c = sqLiteHelper.getReadableDatabase().rawQuery("SELECT name, surname, nickname, tel FROM users", null);
            if (c.moveToFirst()) {
                do {
                    String name = Crypter.decrypt(c.getString(c.getColumnIndex("name")));
                    String surname = Crypter.decrypt(c.getString(c.getColumnIndex("surname")));
                    String username = Crypter.decrypt(c.getString(c.getColumnIndex("nickname")));
                    String tel = Crypter.decrypt(c.getString(c.getColumnIndex("tel")));
                    contacts.add(new Contact(name, surname, username, tel));
                } while (c.moveToNext());
                return contacts;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public ArrayList<Msg> getMessages(String tel) throws Exception {
        ArrayList<Msg> messages = new ArrayList<>();
        Cursor c = sqLiteHelper.getReadableDatabase().rawQuery("SELECT message, sent FROM messages WHERE user_tel_fk LIKE '" + Crypter.encrypt(tel) + "'", null);
        if (c.moveToFirst()) {
            do {
                System.err.println(c.getString(0) + "   " + c.getString(1));
                Msg m = new Msg();
                if (c.getInt(c.getColumnIndex("sent")) == 1)
                    m.setId(777);
                else
                    m.setId(888);
                m.setMessage(Crypter.decrypt(c.getString(c.getColumnIndex("message"))));
                messages.add(m);
            } while (c.moveToNext());
        }
        return messages;
    }
}
