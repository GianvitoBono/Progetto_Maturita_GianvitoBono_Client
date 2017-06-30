package com.giansoft.cryptedchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.net.Socket;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by gianv on 08/06/2017.
 */

public class ServerThread extends Thread {
    private Socket socket;
    private IOManager ioManager;
    private Context ctx;
    private SecurePreferences securePreferences;
    private Handler handler;

    public ServerThread(Socket socket, Context ctx, Handler handler) {
        this.socket = socket;
        this.ctx = ctx;
        this.handler = handler;
        ioManager = new IOManager(socket, IOManager.JSON);
        securePreferences = new SecurePreferences(ctx);
    }

    @Override
    public void run() {
        try {
            Msg req = ioManager.readJSON();

            if (req.getId() == Utils.HELLO) {
                ArrayList<Object> d = new ArrayList<>();
                d.add(socket.getInetAddress().toString().substring(1));
                Connector ct = new Connector(new Msg(15, d), ctx, Connector.SYNC_W_RES);
                ct.start();
                ct.join();
                Msg mTel = ct.getRes();
                String tel = (String) mTel.getData().get(0);
                SecretKey key = null;
                if (key == null) {
                    String ip = socket.getInetAddress().toString().substring(1);
                    Connector c = new Connector(Utils.getSessionKey(ip, ctx), ctx, Connector.SYNC_W_RES);
                    c.start();
                    c.join();
                    Msg res = c.getRes();
                    if (res.getId() == Utils.SUCCESS) {
                        key = (SecretKey) res.getData().get(1);
                        securePreferences.putKey(tel, key);
                    }
                }
                ArrayList<Object> data = new ArrayList<>();
                data.add(tel);
                System.err.println();
                Connector c = new Connector(new Msg(Utils.GET_NAME, data), ctx, Connector.SYNC_W_RES);
                c.start();
                c.join();
                Msg res = c.getRes();
                String name = "";
                String surname = "";
                String fullName = "";
                if (res.getId() == Utils.SUCCESS) {
                    name = (String) res.getData().get(0);
                    surname = (String) res.getData().get(1);
                    fullName = name + " " + surname;
                }

                String message = Crypter.decryptWKey((String) req.getData().get(1), key);
                data = new ArrayList<>();
                data.add(Crypter.encryptWKey(message, key));
                ioManager.writeJSON(new Msg(Utils.SUCCESS, data));

                if (handler == null) {
                    new SQLiteManager(ctx).addMessage(tel, message, 0);
                    Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Intent i = new Intent(ctx, Chat.class)
                            .putExtra("name", name)
                            .putExtra("surname", surname)
                            .putExtra("tel", tel);
                    PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);
                    NotificationCompat.Builder n = new NotificationCompat.Builder(ctx)
                            .setContentTitle(fullName)
                            .setContentText(message)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setSound(sound)
                            .setContentIntent(pi)
                            .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, n.build());
                } else {
                    new SQLiteManager(ctx).addMessage(tel, message, 0);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("success", 1);
                    bundle.putString("message", message);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            Log.i("RM", "error", e);
            System.err.println("[-]  " + e.getStackTrace().toString());
        }
    }
}
