package com.giansoft.cryptedchat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.crypto.SecretKey;

public class ConnectorService extends Service {

    private final IBinder connectorBinder = new ConnectorBinder();
    private ServerSocket serverSocket;
    private int port = Utils.SERVER_PORT;
    private Handler handler;
    private Socket socket;
    private SynchronizedQueue<Object> synchronizedQueue = new SynchronizedQueue<>();
    private boolean flag = false;

    public ConnectorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return connectorBinder;
    }

    public void comunicate(Msg request, Context context, SynchronizedQueue<Object> synchronizedQueue) {
        Connector connector = new Connector(request, context, synchronizedQueue);
        connector.start();
    }

    public void comunicate(String ip, int port, Msg request, Context context, SynchronizedQueue<Object> synchronizedQueue) {
        Connector connector = new Connector(ip, port, request, context, synchronizedQueue);
        connector.start();
    }

    public void comunicate(Msg request, Context context) {
        Connector connector = new Connector(request, context);
        connector.start();
    }

    public void comunicate(String tel, String message, Handler handler) {
        try {
            Message msg = new Message();
            msg.arg1 = Utils.FAIL;
            SecurePreferences securePreferences = new SecurePreferences(this);

            Connector c = new Connector(Utils.getIP(tel), this, false);
            c.start();
            c.join();
            Msg res = c.getRes();
            String ip = null;
            if (res.getId() == Utils.SUCCESS) {
                if (securePreferences.getKey(tel) == null) {
                    ip = (String) res.getData().get(0);
                    c = new Connector(Utils.genSessionKey(tel), this, false);
                    c.start();
                    c.join();
                    res = c.getRes();
                    if (res.getId() == Utils.SUCCESS) {
                        SecretKey key = (SecretKey) res.getData().get(0);
                        securePreferences.putKey(tel, key);
                    } else
                        handler.sendMessage(msg);
                }
                if (ip == null) return;
                c = new Connector(ip, new Msg(Utils.HELLO, null), this, false);
                c.start();
                c.join();
                res = c.getRes();
            } else
                handler.sendMessage(msg);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class ConnectorBinder extends Binder {
        ConnectorService getService() {
            return ConnectorService.this;
        }
    }
}