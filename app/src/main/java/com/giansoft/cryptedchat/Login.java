package com.giansoft.cryptedchat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class Login extends AppCompatActivity {

    private Button bLogin;
    private ProgressBar progressBar;
    private EditText etEmail, etPassword;
    private ConnectorService connectorService;
    private boolean isBound = false;
    private String name;
    private String surname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindService(new Intent(this, ConnectorService.class),serviceConnection , Context.BIND_AUTO_CREATE);

        //---Init---------------------------------------------------
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


    private boolean login() {
        String responce = connectorService.comunicate(Utils.SERVER_IP, Utils.SERVER_PORT, Utils.login(etEmail.getText().toString(), etEmail.getText().toString()), this);
        if(responce.split(Utils.REGEX)[1].equals("success")) {
            name = responce.split(Utils.REGEX)[2];
            surname = responce.split(Utils.REGEX)[3];
            return true;
        }
        return false;
    }

    public void bLoginClicked(View view){
        connectorService.doSomething();
    }

    private boolean register() {
        return false;
    }
}
