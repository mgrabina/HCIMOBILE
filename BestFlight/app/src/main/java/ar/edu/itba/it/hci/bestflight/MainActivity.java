package ar.edu.itba.it.hci.bestflight;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private long notificationInterval = 60000;
    private static FragmentManager fragmentManager;
    private static GPSTracker tracker;
    public static double getLatitude() {
        return tracker.getLatitude();
    }

    public static double getLongitud() {
        return tracker.getLongitude();
    }

    private PendingIntent pendingIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateNotificationsMap();


        SettingsFragment.checkLanguage(Locale.getDefault().getLanguage(), this, null, null, this);      //Setea el lenguaje del dispositivo
        checkLocationPermission();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LocationManager location = (LocationManager) getSystemService(LOCATION_SERVICE);
        tracker=new GPSTracker(location);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        fragmentManager = getFragmentManager();
        AlertManager.getInstance();


        if(getIntent().hasExtra("airline") && getIntent().hasExtra("flightNumber")){

            Bundle bundle = getIntent().getExtras();
            getIntent().removeExtra("airline");
            getIntent().removeExtra("flightNumber");
            Fragment fragment = new StatusFragment();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "statusFragment").addToBackStack("statusFragment").commit();

        }
       else{

            Fragment fragment = new DealsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "dealsFragment").addToBackStack("dealsFragment").commit();
        }

        Intent alarmIntent = new Intent(MainActivity.this, AlertsCheck.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
        start();
    }
    public static  void rebootFragment(Fragment f, String name){
        popBackstack();
        fragmentManager.beginTransaction().replace(R.id.content_frame, f, name).addToBackStack(name).commit();
    }


    public void setNotificationInterval(long number){
        this.notificationInterval = number;
        start();
    }

    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = notificationInterval; //the update is every minute minium

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        //Toast.makeText(this, R.string.alarmset, Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        //Toast.makeText(this, R.string.alarmcanceled, Toast.LENGTH_SHORT).show();
    }





    @Override
    public void onBackPressed() {

        Integer count = getFragmentManager().getBackStackEntryCount();

        if (count == 1) {
            finish();
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }



    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_status) {

            Fragment fragment = new StatusFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "statusFragment").addToBackStack("statusFragment").commit();
        } else if (id == R.id.nav_deals) {
            Fragment fragment = new DealsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "dealsFragment").addToBackStack("dealsFragment").commit();

        } else if (id == R.id.nav_settings) {
            Fragment fragment = new SettingsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "settingsFragment").addToBackStack("settingsFragment").commit();

        }else if (id == R.id.nav_map) {
            Fragment fragment = new MapsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "mapsFragment").addToBackStack("mapsFragment").commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)) {


                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }



    private static void backStackAdd(Fragment fragment, String tag) {

        if ( fragmentManager.findFragmentByTag(tag) != null) {

            fragmentManager.popBackStack(tag, fragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, tag).addToBackStack(tag).commit();
    }

    public static void AddtoBackStack(Fragment fragment, String tag) {backStackAdd(fragment, tag); }
    public static void popBackstack() { fragmentManager.popBackStack(); }


    public void updateNotificationsMap(){


        String serializedMap = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("NotificationsMap", "empty");

        if(!serializedMap.equals("empty")) {
            Gson gson = new Gson();
            MapWrapper wrapper = gson.fromJson(serializedMap, MapWrapper.class);
            HashMap<Integer, Flight> map = wrapper.myMap;
            AlertManager.setNotificationsMap(map);

            for(Integer id : map.keySet()){
                Log.d("AEROLINEA: " + AlertManager.getNotificationsMap().get(id).airline, AlertManager.getNotificationsMap().get(id).baggageGate);

            }

            ArrayList<Alert> a = new ArrayList<Alert>();
            for(Integer id : AlertManager.getNotificationsMap().keySet()){
                Alert alert = new Alert(AlertManager.getNotificationsMap().get(id).flightNumber, AlertManager.getNotificationsMap().get(id).airline);
                if(AlertManager.getAlerts().contains(alert)){

                }
                else{
                    AlertManager.getAlerts().add(alert );
                }

            }


        }
       
    }




}



