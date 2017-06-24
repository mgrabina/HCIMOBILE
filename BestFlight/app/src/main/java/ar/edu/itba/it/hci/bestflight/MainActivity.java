package ar.edu.itba.it.hci.bestflight;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static FragmentManager fragmentManager;
    private static GPSTracker tracker;
    public static double getLatitude() {
        return tracker.getLatitude();
    }

    public static double getLongitud() {
        return tracker.getLongitude();
    }
    //
    private PendingIntent pendingIntent;


    String airlineSt = null;
    Integer flightNSt = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    //

        //Bundle bundl = new Bundle();
        //bundl.putString("airlineId", "id");
        //bundl.putInt("flightNumber", 0);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

           airlineSt = bundle.getString("airline");
            flightNSt = Integer.parseInt(bundle.getString("flightNumber"));

            Log.d("AIRLINE", airlineSt);
            Log.d("FLIGHTNUMBER", flightNSt.toString());
        }

    //
        Intent alarmIntent = new Intent(MainActivity.this, AlertsCheck.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
        cancel();
        start();
    }

    //
    //


    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 8000; //the update is every minute minium

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    //
    //




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

        if (id == R.id.nav_status) {
            // Handle the camera action
            Fragment fragment = new StatusFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "statusFragment").addToBackStack("statusFragment").commit();
        } else if (id == R.id.nav_deals) {
            Fragment fragment = new DealsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "dealsFragment").addToBackStack("dealsFragment").commit();

        } else if (id == R.id.nav_settings) {
            Fragment fragment = new SettingsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "settingsFragment").addToBackStack("settingsFragment").commit();

        }else if (id == R.id.nav_map) {
           // Intent i = new Intent(this, MapsActivity.class);
           // startActivity(i);
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

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);



            } else {
                // No explanation needed, we can request the permission.
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

}



