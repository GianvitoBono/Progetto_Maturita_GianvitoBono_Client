package com.giansoft.cryptedchat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String name, surname;
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

        if(connectorService != null)
            unbindService(serviceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        securePreferences = new SecurePreferences(this);
        //securePreferences.putBoolean("firstLog", true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        TextView tvHeadName = (TextView) hView.findViewById(R.id.tvHeadName);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");
        tvHeadName.setText(name + " " + surname);

        ArrayList<Contact> contacts = new ArrayList<>();
        ContactListAdapter contactAdapter = new ContactListAdapter(this, contacts);
        ListView lvChat = (ListView) findViewById(R.id.lvChat);
        lvChat.setAdapter(contactAdapter);

         ArrayList<Contact> newContacts = new SQLiteManager(this).getUsers();
        if(newContacts != null && !newContacts.isEmpty())
            contactAdapter.addAll(newContacts);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getUsers() {
        //Popolazione tabella user database
        try {
            Cursor phones = getContentResolver()
                 .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            ArrayList<Object> telNumbers = new ArrayList<>();
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(
                        phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
                if (!telNumbers.contains(phoneNumber))
                    if (phoneNumber.charAt(0) == '+')
                        telNumbers.add(phoneNumber.substring(3));
                    else
                        telNumbers.add(phoneNumber);
            }
            phones.close();

            connectorService.comunicate(Utils.checkUsers(telNumbers), Main.this, synchronizedQueue);

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
                        if (responce != null && responce.getId() == 151836) {
                            if (!responce.getData().isEmpty()) {
                                for (Object c : responce.getData()) {
                                    Contact contact = (Contact) c;
                                    new SQLiteManager(Main.this).addUser(contact.getName(),
                                                                         contact.getSurname(),
                                                                         contact.getUsername(),
                                                                         contact.getTel());
                                }
                            }
                        }
                    } else {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            //--------------------------------------------------------------------------------------

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
            //connectorService.comunicate(Utils.logIP(securePreferences.getString("tel")), Main.this, synchronizedQueue);
            //if(securePreferences.getBoolean("firstLog")) {
            getUsers();
            //securePreferences.putBoolean("firstLog", false);
            //}
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };
}