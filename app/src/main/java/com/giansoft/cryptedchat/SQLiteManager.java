package com.giansoft.cryptedchat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gianvito on 14/02/2017.
 */

public class SQLiteManager extends SQLiteOpenHelper{
    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "com.giansoft.cryptedchat.db";
    private final static String TB_USERS = "users";
        private final static String USERS_ID = "_id";
        private final static String USERS_NAME = "name";
        private final static String USERS_SURNAME = "surname";
        private final static String USERS_NICKNAME = "nickname";
        private final static String USERS_TEL = "tel";

    private final static String DB_CREATE = "CREATE TABLE " + TB_USERS + "(" +
            USERS_ID + " INTEGER PRYMARY KEY AUTOINCREMENT " +
            USERS_NAME + " TEXT " +
            USERS_SURNAME + " TEXT " +
            USERS_NICKNAME + " TEXT " +
            USERS_TEL + " TEXT" +
            ");";

    public SQLiteManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TB_USERS);
    }
}
