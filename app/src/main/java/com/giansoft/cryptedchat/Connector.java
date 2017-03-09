package com.giansoft.cryptedchat;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
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

    private String request;
    private Context context;
    private SynchronizedQueue<String> synchronizedQueue;

    private SSLSocket socket;
    private IOManager ioManager;
    private String ip;
    private int port;

    public Connector(String request, Context context, SynchronizedQueue synchronizedQueue){
        this.ip = Utils.SERVER_IP;
        this.port = Utils.SERVER_PORT;
        this.request = request;
        this.context = context;
        this.synchronizedQueue = synchronizedQueue;
    }

    public Connector(String ip, int port, String request, Context context, SynchronizedQueue synchronizedQueue){
        this.ip = ip;
        this.port = port;
        this.request = request;
        this.context = context;
        this.synchronizedQueue = synchronizedQueue;
    }

    @Override
    public void run(){
        try {
            socket = getConnection(ip, port);
            ioManager = new IOManager(socket);
            System.out.println("[+] Connesso al server: " + ip + ":" + port);
            ioManager.write(request);
            String responce = ioManager.read();
            synchronizedQueue.add(responce);
            ioManager.close();
            socket.close();

        } catch(UnknownHostException e) {
            System.err.println("[-] Host non trovato");
        } catch (IOException e) {
            System.err.println("[-] Errore I/O");
        }
    }

    protected SSLSocket getConnection(String ip, int port) throws IOException {
        try {
            KeyStore trustStore = KeyStore.getInstance("BKS");
            InputStream trustStoreStream = context.getResources().openRawResource(R.raw.cacerts);
            trustStore.load(trustStoreStream, "38wVZQcJGLkyQSzKwuk4RuGE".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket(ip, port);
            return socket;
        } catch (GeneralSecurityException e) {
            Log.e(this.getClass().toString(), "Exception while creating context: ", e);
            throw new IOException("Could not connect to SSL Server", e);
        }
    }

}
