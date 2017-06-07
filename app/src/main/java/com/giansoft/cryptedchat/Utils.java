package com.giansoft.cryptedchat;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Gianvito on 13/11/2016.
 */

public class Utils {
    public static final String SERVER_IP = "192.168.0.2";
    public static final int SERVER_PORT = 3943;
    public static final String REGEX = ":";

    public final static int LOGIN = 0;
    public final static int ADD_TO_IP_LIST = 1;
    public final static int SEARCH_USER = 2;
    public final static int ADD_USER = 3;
    public final static int GET_USER_IP = 4;
    public final static int DEL_IP = 8;
    public final static int GEN_KEY = 9;
    public final static int GET_KEY = 11;
    public final static int DEL_KEY = 12;
    public final static int DEL_SESSION = 13;
    public final static int CHECK_SESSION = 14;
    public final static int SUCCESS = 3001;
    public final static int FAIL = 2206;
    public final static int NO_USERS = 151836;
    public final static int HELLO = 15052015;
    public final static int SECURE = 2112;

    public static Msg login(String email, String passwd) {
        ArrayList<Object> data = new ArrayList<>();
        data.add(email);
        data.add(passwd);
        return new Msg(LOGIN, data);
    }

    public static Msg register(String name, String surname, String tel, String password, String nickname, String email) {
        ArrayList<Object> data = new ArrayList<>();
        data.add(name);
        data.add(surname);
        data.add(nickname);
        data.add(email);
        data.add(password);
        data.add(tel);
        return new Msg(ADD_USER, data);
    }

    public static String echo(String message) {
        return ("7" + REGEX + message);
    }

    public static Msg checkUsers(ArrayList<Object> tel) {
        return new Msg(SEARCH_USER, tel);
    }

    public static void nameToast(Context ctx, String name, String surname) {
        Toast.makeText(ctx, String.format(ctx.getString(R.string.benvenuto), name, surname), Toast.LENGTH_SHORT).show();
    }

    public static void errLoginToast(Context ctx) {
        Toast.makeText(ctx, ctx.getString(R.string.err_login), Toast.LENGTH_SHORT).show();
    }

    public static void errRegisterToast(Context ctx) {
        Toast.makeText(ctx, "Registrazione fallita", Toast.LENGTH_SHORT).show();
    }

    public static void errServerConnToast(Context ctx) {
        Toast.makeText(ctx, ctx.getString(R.string.err_server_conn), Toast.LENGTH_SHORT).show();
    }

    public static void errNullUserOrPassToast(Context ctx) {
        Toast.makeText(ctx, ctx.getString(R.string.err_null_user_or_pass), Toast.LENGTH_SHORT).show();
    }

    public static void errPassNotEquals(Context ctx) {
        Toast.makeText(ctx, "Le password inserite non corrispondono", Toast.LENGTH_SHORT).show();
    }

    public static Msg logIP(String tel) {
        ArrayList<Object> data = new ArrayList<>();
        data.add(tel);
        return new Msg(ADD_TO_IP_LIST, data);
    }
    
    public static Msg delIP(String tel) {
    	ArrayList<Object> data = new ArrayList<>();
        data.add(tel);
        return new Msg(DEL_IP, data);
    }

    public static String getCurDate() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - kk:mm");
        return df.format(Calendar.getInstance().getTime());
    }

    public static Msg getIP(String tel) {
        ArrayList<Object> data = new ArrayList<>();
        data.add(tel);
        return new Msg(GET_USER_IP, data);
    }

    public static Msg genSessionKey(String tel) {
        ArrayList<Object> data = new ArrayList<>();
        data.add(tel);
        return new Msg(GEN_KEY, data);
    }

    public static boolean isKeyValid(String tel, Context ctx) {
        SecurePreferences securePreferences = new SecurePreferences(ctx);
        return true;
    }
}
