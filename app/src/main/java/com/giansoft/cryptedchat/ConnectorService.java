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

    public String comunicate(String ip, int port, String request, Context context) {
        try {
            Connector connector = new Connector(ip, port, request, context);
            connector.start();
            connector.join();
            return connector.getResponce();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class ConnectorBinder extends Binder {
        ConnectorService getService() {
            return ConnectorService.this;
        }
    }
}
