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

    public void comunicate(String request, Context context, SynchronizedQueue<String> synchronizedQueue) {
            Connector connector = new Connector(request, context, synchronizedQueue);
            connector.start();
    }

    public class ConnectorBinder extends Binder {
        ConnectorService getService() {
            return ConnectorService.this;
        }
    }
}
