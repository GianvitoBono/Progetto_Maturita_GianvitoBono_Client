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
    private Socket socket;
    private SynchronizedQueue<Object> synchronizedQueue = new SynchronizedQueue<>();
    private boolean flag = false;
    private Context ctx = this;
    private Handler handler = null;


    public ConnectorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(Utils.SERVER_PORT);
                    while (true) {
                        new ServerThread(serverSocket.accept(), ctx, handler).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return connectorBinder;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void unsetHandler() {
        handler = null;
    }

    public void comunicate(Msg request, Context context, Handler handler) {
        Connector connector = new Connector(request, context, handler);
        connector.start();
    }

    public void comunicate(String ip, Msg request, Context context, Handler handler) {
        Connector connector = new Connector(ip, request, context, handler, Connector.ASYNC_W_HANDLER);
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
            Connector c = new Connector(Utils.getIP(tel), this, Connector.SYNC_W_RES);
            c.start();
            c.join();
            Msg res = c.getRes();
            String ip = (String) res.getData().get(0);
            if (ip.subSequence(0, 1).equals("/"))
                ip = ip.substring(1);

            if (res.getId() == Utils.SUCCESS) {
                c = new Connector(Utils.genSessionKey(tel), this, Connector.SYNC_W_RES);
                c.start();
                c.join();
                res = c.getRes();
                if (res.getId() == Utils.SUCCESS) {
                    SecretKey key = (SecretKey) res.getData().get(0);
                    securePreferences.putKey(tel, key);
                    ArrayList<Object> data = new ArrayList<>();
                    data.add(Crypter.encrypt(securePreferences.getString("tel")));
                    data.add(Crypter.encryptWKey(message, key));
                    data.add(Crypter.encryptWKey(Utils.getCurDate(), key));
                    c = new Connector(ip, new Msg(Utils.HELLO, data), this, Connector.USE_JSON_SERIALIZATION_CON_RES_SYNC);
                    c.start();
                    msg.arg1 = Utils.SUCCESS;
                    handler.sendMessage(msg);
                } else
                    handler.sendMessage(msg);
            } else
                handler.sendMessage(msg);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ConnectorBinder extends Binder {
        ConnectorService getService() {
            return ConnectorService.this;
        }
    }
}