package com.giansoft.cryptedchat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String name, surname;
    private ConnectorService connectorService;
    private boolean isBound = false;
    private SynchronizedQueue<String> synchronizedQueue = new SynchronizedQueue<>();
    private SecurePreferences securePreferences;
    private ListView lvChat;


    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, ConnectorService.class),serviceConnection , Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        securePreferences = new SecurePreferences(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        TextView tvHeadName = (TextView)hView.findViewById(R.id.tvHeadName);
        TextView tvHeadLoginInfo = (TextView)hView.findViewById(R.id.tvHeadLoginInfo);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");
        tvHeadName.setText(name + " " + surname);
        new SQLiteManager(this).addUser("Nome1", "Cognome1", "Username1", "3458302965");

        Contact[] contacts = (Contact[]) new SQLiteManager(this).getUsers().toArray();
        ListAdapter contactAdapter = new ContactListAdapter(this, R.layout.contact_inflater, contacts);
        lvChat = (ListView) findViewById(R.id.lvChat);
        lvChat.setAdapter(contactAdapter);


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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ConnectorService.ConnectorBinder binder = (ConnectorService.ConnectorBinder) iBinder;
            connectorService = binder.getService();
            isBound = true;
            connectorService.comunicate(Utils.logIP(securePreferences.getString("tel")), Main.this, synchronizedQueue);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };
}
