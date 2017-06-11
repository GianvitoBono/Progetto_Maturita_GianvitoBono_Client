package com.giansoft.cryptedchat;

import android.content.Context;
import android.content.res.ObbInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.Socket;

import javax.crypto.SecretKey;

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
            System.out.println("--------------------------------------------------------------------");
            Log.d("ServerThread: req", (String) req.getData().get(1));
            System.out.println("--------------------------------------------------------------------");

            if (req.getId() == Utils.HELLO) {
                String tel = (String) req.getData().get(0);
                SecretKey key = null;
                if (key == null) {
                    String ip = socket.getInetAddress().toString().substring(1);
                    Connector c = new Connector(Utils.getSessionKey(ip, ctx), ctx, Connector.SYNC_W_RES);
                    c.start();
                    c.join();
                    Msg res = c.getRes();
                    System.out.println("--------------------------------------------------------------------");
                    Log.d("ServerThread: key", (String) req.getData().get(1));
                    System.out.println("--------------------------------------------------------------------");
                    if (res.getId() == Utils.SUCCESS) {
                        key = (SecretKey) res.getData().get(1);
                        securePreferences.putKey(tel, key);
                    }
                }
                String message = Crypter.decryptWKey((String) req.getData().get(1), key);
                System.out.println("--------------------------------------------------------------------");
                Log.d("ServerThread: message", message);
                System.out.println("--------------------------------------------------------------------");
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("success", 1);
                bundle.putString("message", message);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            Log.i("RM", "error", e);
            System.err.println("[-]  " + e.getStackTrace().toString());
        }
    }
}
