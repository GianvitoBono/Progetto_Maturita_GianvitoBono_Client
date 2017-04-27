package com.giansoft.cryptedchat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ServerService extends Service {

    private final IBinder serverBinder = new ServerBinder();

    public ServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    protected SSLSocket getConnection(String ip, int port) throws IOException {
        try {
            KeyStore trustStore = KeyStore.getInstance("BKS");
            InputStream trustStoreStream = this.getResources().openRawResource(R.raw.cacerts);
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

    @Override
    public IBinder onBind(Intent intent) {
        return serverBinder;
    }

    public class ServerBinder extends Binder {
        ServerService getService() {
            return ServerService.this;
        }
    }

}
