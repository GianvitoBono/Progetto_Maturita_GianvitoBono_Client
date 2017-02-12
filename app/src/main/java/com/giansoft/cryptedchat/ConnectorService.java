package com.giansoft.cryptedchat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ConnectorService extends Service {

    private final IBinder connectorBinder = new ConnectorBinder();

    public ConnectorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return connectorBinder;
    }

    public void doSomething(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 100000; i++)
                    System.out.println("=================================================================================");
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String comunicate(String ip, int port, String request, Context context, SynchronizedQueue<String> synchronizedQueue) {
            Connector connector = new Connector(ip, port, request, context, synchronizedQueue);
            connector.start();
            return connector.getResponce();
    }

    public class ConnectorBinder extends Binder {
        ConnectorService getService() {
            return ConnectorService.this;
        }
    }
}
