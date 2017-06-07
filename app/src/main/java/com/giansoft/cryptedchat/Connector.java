package com.giansoft.cryptedchat;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Gianvito on 12/11/2016.
 */

public class Connector extends Thread {

    private Msg request;
    private Context context;
    private SynchronizedQueue<Object> synchronizedQueue;

    private SSLSocket socket;
    private IOManager ioManager;
    private String ip;
    private int port;
    private boolean flag = true;
    private boolean async = true;
    private Msg res;

    public Connector(Msg request, Context context, SynchronizedQueue synchronizedQueue){
        this.ip = Utils.SERVER_IP;
        this.port = Utils.SERVER_PORT;
        this.request = request;
        this.context = context;
        this.synchronizedQueue = synchronizedQueue;
    }

    public Connector(String ip, int port, Msg request, Context context, SynchronizedQueue synchronizedQueue){
        this.ip = ip;
        this.port = port;
        this.request = request;
        this.context = context;
        this.synchronizedQueue = synchronizedQueue;
    }

    public Connector(Msg request, Context context){
        this.ip = Utils.SERVER_IP;
        this.port = Utils.SERVER_PORT;
        this.request = request;
        this.context = context;
        this.flag = false;
    }

    public Connector(Msg request, Context context, boolean async){
        this.ip = Utils.SERVER_IP;
        this.port = Utils.SERVER_PORT;
        this.request = request;
        this.context = context;
        this.async = async;
    }

    public Connector(String ip, Msg request, Context context, boolean async){
        this.ip = ip;
        this.port = Utils.SERVER_PORT;
        this.request = request;
        this.context = context;
        this.async = async;
    }

    @Override
    public void run(){
        try {
            socket = getConnection(ip, port);
            ioManager = new IOManager(socket);
            System.out.println("[+] Connesso all'host: " + ip + ":" + port);
            ioManager.write(request);
            if(flag) {
                if(async) {
                    Object responce = ioManager.read();
                    synchronizedQueue.add(responce);
                } else {
                    res = (Msg)ioManager.read();
                    System.out.println(res.getId());
                }
            }
            ioManager.close();
            socket.close();

        } catch(UnknownHostException e) {
            System.err.println("[-] Host non trovato");
        } catch (IOException e) {
            System.err.println("[-] Errore I/O");
        }
    }

    public Msg getRes() {
        return res;
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

}
