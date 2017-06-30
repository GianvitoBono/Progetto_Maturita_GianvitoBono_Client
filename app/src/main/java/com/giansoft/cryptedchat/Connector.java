package com.giansoft.cryptedchat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Gianvito on 12/11/2016
 */

public class Connector extends Thread {

    public final static int ASYNC_W_HANDLER = 1;
    public final static int ASYNC_NO_HANDLER = 2;
    public final static int USE_JSON_SERIALIZATION_CON_RES_ASYNC = 3;
    public final static int USE_JSON_SERIALIZATION_NO_RES = 4;
    public final static int SYNC_W_RES = 5;
    public final static int USE_JSON_SERIALIZATION_CON_RES_SYNC = 6;

    private Msg req;
    private Context context;
    private Handler handler;

    private Socket socket;
    private IOManager ioManager;
    private String ip;
    private int port;
    private Msg res;
    private int mode;

    public Connector(Msg req, Context context, Handler handler) {
        ip = Utils.SERVER_IP;
        port = Utils.SERVER_PORT;
        this.req = req;
        this.context = context;
        this.handler = handler;
        mode = ASYNC_W_HANDLER;
    }

    public Connector(Msg req, Context context) {
        ip = Utils.SERVER_IP;
        port = Utils.SERVER_PORT;
        this.req = req;
        this.context = context;
        mode = ASYNC_NO_HANDLER;
    }

    public Connector(Msg req, Context context, int mode) {
        ip = Utils.SERVER_IP;
        port = Utils.SERVER_PORT;
        this.req = req;
        this.context = context;
        this.mode = mode;
    }

    public Connector(String ip, Msg req, Context context, Handler handler, int mode) {
        this.ip = ip;
        this.port = Utils.SERVER_PORT;
        this.req = req;
        this.context = context;
        this.handler = handler;
        this.mode = mode;
    }

    public Connector(String ip, Msg req, Context context, int mode) {
        this.ip = ip;
        this.port = Utils.SERVER_PORT;
        this.req = req;
        this.context = context;
        this.mode = mode;
    }

    @Override
    public void run() {
        super.run();
        try {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            System.out.println("--------------------------------------------------------------------");
            Log.d("Connector: IP", " " + ip);
            System.out.println("--------------------------------------------------------------------");

            switch (mode) {
                case ASYNC_W_HANDLER:
                    socket = getConnection(ip, port);
                    ioManager = new IOManager(socket);
                    ioManager.write(req);
                    res = (Msg) ioManager.read();
                    bundle.putSerializable("res", res);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    break;
                case ASYNC_NO_HANDLER:
                    socket = getConnection(ip, port);
                    ioManager = new IOManager(socket);
                    ioManager.write(req);
                    break;
                case SYNC_W_RES:
                    socket = getConnection(ip, port);
                    ioManager = new IOManager(socket);
                    ioManager.write(req);
                    res = (Msg) ioManager.read();
                    break;
                case USE_JSON_SERIALIZATION_CON_RES_ASYNC:
                    socket = new Socket(ip, port);
                    ioManager = new IOManager(socket, IOManager.JSON);
                    ioManager.writeJSON(req);
                    res = ioManager.readJSON();
                    bundle.putSerializable("res", res);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    break;
                case USE_JSON_SERIALIZATION_NO_RES:
                    socket = new Socket(ip, port);
                    ioManager = new IOManager(socket, IOManager.JSON);
                    ioManager.writeJSON(req);
                    break;
                case USE_JSON_SERIALIZATION_CON_RES_SYNC:
                    socket = new Socket(ip, port);
                    ioManager = new IOManager(socket, IOManager.JSON);
                    ioManager.writeJSON(req);
                    res = ioManager.readJSON();
                    break;
                default:
            }
            socket.close();
        } catch (Exception e) {
            System.err.println("[-] Error: " + e.getStackTrace());
            Log.i("Connector --- --- ---", "error", e);
        }
    }

    protected SSLSocket getConnection(String ip, int port) throws IOException {
        try {
            KeyStore trustStore = KeyStore.getInstance("BKS");
            InputStream trustStoreStream = context.getResources().openRawResource(R.raw.cacerts);
            trustStore.load(trustStoreStream, "38wVZQcJGLkyQSzKwuk4RuGE".toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket(ip, port);
            return socket;
        } catch (GeneralSecurityException e) {
            Log.e(this.getClass().toString(), "Errore nella creazione del context: ", e);
            throw new IOException("Impossibile connettersi al server", e);
        }
    }

    public Msg getRes() {
        return res;
    }
}