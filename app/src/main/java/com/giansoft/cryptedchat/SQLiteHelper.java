package com.giansoft.cryptedchat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.StrictMode;

/**
 * Created by Gianvito on 14/02/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper{
    private final static int DB_VERSION = 2;
    private final static String DB_NAME = "com.giansoft.cryptedchat.db";
    private final static String TB_USERS = "users";
        private final static String USERS_NAME = "name";
        private final static String USERS_SURNAME = "surname";
        private final static String USERS_USERNAME = "nickname";
        private final static String USERS_TEL = "tel";

    private final static String TB_MESSAGES = "messages";
        private final static String MESSAGES_ID = "_id";
        private final static String MESSAGES_USER_FK = "user_tel_fk";
        private final static String MESSAGES_MESSAGE = "message";
        private final static String MESSAGES_SENT = "sent";

    private final static String TB_USERS_CREATE = "CREATE TABLE " + TB_USERS + "(" +
            USERS_TEL + " TEXT PRYMARY KEY, " +
            USERS_NAME + " TEXT, " +
            USERS_SURNAME + " TEXT, " +
            USERS_USERNAME + " TEXT " +
            ");";

    private final static String TB_MESSAGES_CREATE = "CREATE TABLE " + TB_MESSAGES + "(" +
            MESSAGES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MESSAGES_USER_FK + " TEXT, " +
            MESSAGES_MESSAGE + " TEXT, " +
            MESSAGES_SENT + " TEXT, " +
            " FOREIGN KEY (" + MESSAGES_USER_FK + ") REFERENCES " + TB_USERS + "(" + USERS_TEL + ") " +
            ");";

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TB_USERS_CREATE);
        sqLiteDatabase.execSQL(TB_MESSAGES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TB_USERS);
        onCreate(sqLiteDatabase);
    }
}
