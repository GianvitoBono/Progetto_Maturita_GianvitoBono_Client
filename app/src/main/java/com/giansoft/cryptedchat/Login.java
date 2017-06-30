package com.giansoft.cryptedchat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class Login extends AppCompatActivity {

    private Button bLogin, bReg;
    private ProgressBar progressBar;
    private EditText etEmail, etPassword;
    private ConnectorService connectorService;
    private boolean isBound = false;
    private SynchronizedQueue<Object> synchronizedQueue = new SynchronizedQueue<>();
    private SecurePreferences securePreferences;
    private Handler handler;

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, ConnectorService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (connectorService != null)
            unbindService(serviceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        securePreferences = new SecurePreferences(this);
        new SQLiteManager(this).clearMess(true);
        /*if(securePreferences.getBoolean("logged")) {
            String name = securePreferences.getString("name");
            String surname = securePreferences.getString("surname");
            startActivity(new Intent(Login.this, Main.class)
                    .putExtra("name", name)
                    .putExtra("surname", surname)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            finish();
        }*/

        //---Init---------------------------------------------------
        bLogin = (Button) findViewById(R.id.bLogin);
        bReg = (Button) findViewById(R.id.bReg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        etEmail = (EditText) findViewById(R.id.etEmail);
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
            bReg.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            connectorService.comunicate(Utils.login(etEmail.getText().toString(), etPassword.getText().toString()), this, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData();
                    Msg responce = (Msg) bundle.getSerializable("res");

                    String result = (String) responce.getData().get(0);
                    if (result.equals("success")) {
                        String name = (String) responce.getData().get(1);
                        String surname = (String) responce.getData().get(2);
                        securePreferences.putBoolean("logged", true);
                        securePreferences.putString("name", name);
                        securePreferences.putString("surname", surname);
                        startActivity(new Intent(Login.this, Main.class)
                                .putExtra("name", name)
                                .putExtra("surname", surname)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        finish();
                    } else {
                        Utils.errLoginToast(Login.this);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register(View view) {
        startActivity(new Intent(Login.this, Register.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

}
