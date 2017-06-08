package com.giansoft.cryptedchat;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.net.Socket;

import javax.crypto.SecretKey;

/**
 * Created by gianv on 08/06/2017.
 */

public class ServerThread extends Thread {
    private Socket socket;
    private int mode;
    private IOManager ioManager;
    private Context ctx;
    private Handler handler;

    public ServerThread(Socket socket, Context ctx, Handler handler) {
        this.socket = socket;
        this.ctx = ctx;
        this.handler = handler;
        ioManager = new IOManager(socket);
    }

    @Override
    public void run() {
        try {
            Msg o = (Msg) ioManager.read();
            if(o.getClass().equals(String.class)) {
                mode = Utils.HELLO;
            } else {
                Msg req = (Msg)o;
                mode = req.getId();
            }
            switch (mode) {
                case Utils.HELLO:
                    String ip = socket.getInetAddress().toString().substring(1);
                    Connector c = new Connector(Utils.getSessionKey(ip, ctx), ctx, false);
                    c.start();
                    c.join();
                    Msg res = c.getRes();
                    if (res.getId() == Utils.SUCCESS) {
                        SecretKey key = (SecretKey) res.getData().get(0);
                        SecurePreferences securePreferences = new SecurePreferences(ctx);
                        String tel = securePreferences.getString("tel");
                        securePreferences.putKey(tel, key);
                        ioManager.writeString("OK");
                    } else {
                        Message msg = new Message();
                        msg.arg1 = Utils.FAIL;
                        handler.sendMessage(msg);
                        ioManager.writeString("NO");
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("[-] " + e.getStackTrace());
        }
    }
}
