package com.giansoft.cryptedchat;

import android.content.Context;
import android.content.res.ObbInfo;
import android.os.Bundle;
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
    private SecurePreferences securePreferences;

    public ServerThread(Socket socket, Context ctx, Handler handler) {
        this.socket = socket;
        this.ctx = ctx;
        this.handler = handler;
        ioManager = new IOManager(socket);
        securePreferences = new SecurePreferences(ctx);
    }

    @Override
    public void run() {
        try {
            String tmp = (String) ioManager.read();
            if(tmp.equals("1"))
                mode = Utils.HELLO;
            else if (tmp.equals("2"))
                mode = Utils.SECURE;
            else if (tmp.equals("3"))
                mode = 3;
            switch (mode) {
                case Utils.HELLO:
                    String ip = socket.getInetAddress().toString().substring(1);
                    Connector c = new Connector(Utils.getTel(ip), ctx, false);
                    c.start();
                    c.join();
                    Msg res = c.getRes();
                    String tel = (String) res.getData().get(0);
                    c = new Connector(Utils.getSessionKey(ip, ctx), ctx, false);
                    c.start();
                    c.join();
                    res = c.getRes();
                    if (res.getId() == Utils.SUCCESS) {
                        SecretKey key = (SecretKey) res.getData().get(0);
                        securePreferences.putKey(tel, key);
                        new Connector(ip, "2", ctx, false).start();
                    } else {
                        Message msg = new Message();
                        msg.arg1 = Utils.FAIL;
                        handler.sendMessage(msg);
                        return;
                    }
                    break;
                case Utils.SECURE:
                    //Connector c2 = new Connector();
                    String dest = securePreferences.getString("dest");
                    System.out.println(dest + " //////////////////////////////////////////////////////");
                    String message = securePreferences.getString("message");
                    Connector c2 = new Connector(Utils.getIP(dest), ctx, false);
                    c2.start();
                    c2.join();
                    Msg res2 = c2.getRes();
                    String ip2 = (String) res2.getData().get(0);
                    new Connector(ip2, Crypter.encrypt(message), ctx, false, true).start();
                    System.out.println(message + "--------fhd----------------dfh----------------dfh-------");
                    break;
                case 3:
                    String mess = (String) ioManager.read();
                    String clearMess = Crypter.decrypt(mess);
                    System.out.println("[+] Arrivato messaggio: " + clearMess);
                    Message msg = new Message();
                    Bundle b = new Bundle();
                    b.putInt("id", Utils.SUCCESS);
                    b.putString("mess", clearMess);
                    msg.setData(b);
                    handler.sendMessage(msg);
                    break;

            }
        } catch (Exception e) {
            System.err.println("[-] " + e.getStackTrace());
        }
    }
}
