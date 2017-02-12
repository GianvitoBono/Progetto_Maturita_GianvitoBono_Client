package com.giansoft.cryptedchat;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Gianvito on 13/11/2016.
 */

public class Utils {
    public static final String SERVER_IP = "192.168.0.128";
    public static final int SERVER_PORT = 3943;
    public static final String REGEX = ":";

    public static String login(String email, String passwd) {
        return ("0" + REGEX + email + REGEX + passwd);
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

    public static void errServerConnToast(Context ctx) {
        Toast.makeText(ctx, ctx.getString(R.string.err_server_conn), Toast.LENGTH_SHORT).show();
    }

    public static void errNullUserOrPassToast(Context ctx) {
        Toast.makeText(ctx, ctx.getString(R.string.err_null_user_or_pass), Toast.LENGTH_SHORT).show();
    }
}
