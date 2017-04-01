package com.giansoft.cryptedchat;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Gianvito on 13/11/2016.
 */

public class Utils {
    public static final String SERVER_IP = "192.168.0.2";
    public static final int SERVER_PORT = 3943;
    public static final String REGEX = ":";

    public static String login(String email, String passwd) {
        return ("0" + REGEX + email + REGEX + passwd);
    }

    public static String register(String name, String surname, String tel, String password, String nickname) {
        return "3" + REGEX + name + REGEX + surname + REGEX + nickname + REGEX + password + REGEX + tel;
    }

    public static String echo(String message) {
        return ("7" + REGEX + message);
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

    public static String logIP(String tel) {
        return "1:" + tel;
    }
    
    public static String DelIP(String tel) {
    	return "8: " + tel;
    }
}
