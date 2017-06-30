package com.giansoft.cryptedchat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.MessageView;

import java.util.ArrayList;


public class Chat extends AppCompatActivity {

    private ConnectorService connectorService;
    private boolean isBound = false;
    private SecurePreferences securePreferences;
    private String tel;
    private SynchronizedQueue<Object> synchronizedQueue = new SynchronizedQueue<>();
    private TextView messageBox;
    private MessageView mChatView;
    private com.github.bassaer.chatmessageview.models.User me;
    private com.github.bassaer.chatmessageview.models.User you;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectorService.unsetHandler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        securePreferences = new SecurePreferences(this);
        bindService(new Intent(this, ConnectorService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String surname = i.getStringExtra("surname");
        tel = i.getStringExtra("tel");
        View chatBox = findViewById(R.id.chatBox);
        ImageView send = (ImageView) chatBox.findViewById(R.id.send);
        messageBox = (TextView) chatBox.findViewById(R.id.messageBox);
        mChatView = (MessageView) findViewById(R.id.chatMessageView);
        me = new User(0, "Io", null);
        you = new User(1, name, null);
        this.setTitle(name + " " + surname);

        mChatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        mChatView.setLeftBubbleColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mChatView.setBackgroundColor(Color.WHITE);
        mChatView.setRightMessageTextColor(Color.BLACK);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.BLACK);
        mChatView.setSendTimeTextColor(Color.BLACK);
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);


        try {
            ArrayList<Msg> messages = new SQLiteManager(this).getMessages(tel);
            for (Msg m : messages) {
                System.out.println(m);
                if (m.getId() == 777) {
                    com.github.bassaer.chatmessageview.models.Message message = new com.github.bassaer.chatmessageview.models.Message.Builder()
                            .setUser(me)
                            .setRightMessage(true)
                            .setMessageText(m.getMessage())
                            .build();
                    mChatView.setMessage(message);
                } else {
                    com.github.bassaer.chatmessageview.models.Message message = new com.github.bassaer.chatmessageview.models.Message.Builder()
                            .setUser(you)
                            .setRightMessage(false)
                            .setMessageText(m.getMessage())
                            .build();
                    mChatView.setMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = messageBox.getText().toString();
                new SQLiteManager(Chat.this).addMessage(tel, message, 1);
                messageBox.setText("");
                if (message != null) {
                    connectorService.comunicate(tel, message, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.arg1 == Utils.FAIL)
                                System.out.println("Errore nella connessione");
                            else {
                                com.github.bassaer.chatmessageview.models.Message mess = new com.github.bassaer.chatmessageview.models.Message.Builder()
                                        .setUser(me)
                                        .setRightMessage(true)
                                        .setMessageText(message)
                                        .build();
                                mChatView.setMessage(mess);
                            }
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

            connectorService.setHandler(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    super.handleMessage(msg);
                    Bundle data = msg.getData();
                    String mess = data.getString("message");

                    com.github.bassaer.chatmessageview.models.Message message = new com.github.bassaer.chatmessageview.models.Message.Builder()
                            .setUser(you)
                            .setRightMessage(false)
                            .setMessageText(mess)
                            .build();
                    mChatView.setMessage(message);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };
}
