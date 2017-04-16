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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;


public class Register extends AppCompatActivity {

    private Button bReg;
    private ProgressBar progressBar;
    private EditText etUsername, etName, etSurname, etEmail, etCell, etPassword, etRPassword;
    private SynchronizedQueue<Object> synchronizedQueue = new SynchronizedQueue<>();
    private ConnectorService connectorService;
    private boolean isBound = false;
    private Handler handler;
    private SecurePreferences securePreferences;

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, ConnectorService.class),serviceConnection , Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(connectorService != null)
            unbindService(serviceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        securePreferences = new SecurePreferences(this);
        bReg = (Button) findViewById(R.id.bReg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etCell = (EditText) findViewById(R.id.etCell);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etRPassword = (EditText) findViewById(R.id.etRPasswords);

    }

    public void register(View view) {
        try {
            bReg.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            if(     etUsername.getText() == null &&
                    etName.getText() == null &&
                    etSurname.getText() == null &&
                    etEmail.getText() == null &&
                    etCell.getText() == null &&
                    etPassword.getText() == null &&
                    etRPassword.getText() == null)
                if(!etPassword.getText().toString().equals(etRPassword.getText().toString()))
                    Utils.errPassNotEquals(this);
                else
                    Utils.errNullUserOrPassToast(this);
            else {
                connectorService.comunicate(Utils.register(etName.getText().toString(),
                        etSurname.getText().toString(),
                        etCell.getText().toString(),
                        etPassword.getText().toString(),
                        etUsername.getText().toString(),
                        etEmail.getText().toString()),
                        this, synchronizedQueue);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (synchronizedQueue.isEmpty())
                            try {
                                Thread.sleep(3);
                            } catch (InterruptedException e) {
                                System.err.println("[-] Error: " + e);
                            }
                        handler.sendEmptyMessage(0);
                    }

                }).start();

                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        ArrayList<Object> results = synchronizedQueue.getAll(true);
                        if (results.size() == 1) {
                            Msg responce = (Msg) results.get(0);
                            if ((int) responce.getData().get(0) == 1) {
                                securePreferences.putString("tel", etCell.getText().toString());
                                securePreferences.putBoolean("reg", true);
                                startActivity(new Intent(Register.this, Main.class).putExtra("name", etName.getText().toString())
                                        .putExtra("surname", etSurname.getText().toString()));
                                finish();
                            } else {
                                Utils.errRegisterToast(Register.this);
                            }
                        }
                        bReg.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
