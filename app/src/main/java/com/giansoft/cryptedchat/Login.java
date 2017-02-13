package com.giansoft.cryptedchat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collection;

public class Login extends AppCompatActivity {

    private Button bLogin;
    private ProgressBar progressBar;
    private EditText etEmail, etPassword;
    private ConnectorService connectorService;
    private boolean isBound = false;
    private String name;
    private String surname;
    private SynchronizedQueue<String> synchronizedQueue = new SynchronizedQueue<>();

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //---Init---------------------------------------------------
        synchronizedQueue = new SynchronizedQueue<>();
        bindService(new Intent(this, ConnectorService.class),serviceConnection , Context.BIND_AUTO_CREATE);
        bLogin = (Button) findViewById(R.id.bLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        etEmail = (EditText) findViewById(R.id.etEmail) ;
        etPassword = (EditText) findViewById(R.id.etPassword);
        //----------------------------------------------------------
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


    public void login(View view) {
        try {
            bLogin.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            connectorService.comunicate(Utils.login(etEmail.getText().toString(), etPassword.getText().toString()), this, synchronizedQueue);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(synchronizedQueue.isEmpty())
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            System.err.println("[-] Error: " + e);
                        }
                    handler.sendEmptyMessage(0);
                }

            }).start();

            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    ArrayList<String> results = synchronizedQueue.getAll(true);
                    if(results.size() == 1) {
                        String res_splitted[] = results.get(0).split(Utils.REGEX);
                        if(res_splitted[0] != null && res_splitted[0].equals("success")) {
                            name = res_splitted[1];
                            surname = res_splitted[2];
                            startActivity(new Intent(Login.this, Main.class)
                                    .putExtra("name", name)
                                    .putExtra("surname", surname)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        }

                    } else {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    bLogin.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
