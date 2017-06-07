package com.giansoft.cryptedchat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    private ConnectorService connectorService;
    private boolean isBound = false;
    private SecurePreferences securePreferences;
    private String tel;
    private SynchronizedQueue<Object> synchronizedQueue = new SynchronizedQueue<>();
    private TextView messageBox;

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, ConnectorService.class), serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        connectorService.comunicate(Utils.delIP(securePreferences.getString("tel")), this);
        if (connectorService != null)
            unbindService(serviceConnection);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        securePreferences = new SecurePreferences(this);

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String surname = i.getStringExtra("surname");
        tel = i.getStringExtra("tel");
        View chatBox = findViewById(R.id.chatBox);
        ImageView send = (ImageView) chatBox.findViewById(R.id.send);
        messageBox = (TextView) chatBox.findViewById(R.id.messageBox);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageBox.getText().toString();
                messageBox.setText("");
                if (message != null) {
                    connectorService.comunicate(tel, message, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {

                        }
                    });
                }
            }
        });

        this.setTitle(name + " " + surname);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ConnectorService.ConnectorBinder binder = (ConnectorService.ConnectorBinder) iBinder;
            connectorService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };
}
