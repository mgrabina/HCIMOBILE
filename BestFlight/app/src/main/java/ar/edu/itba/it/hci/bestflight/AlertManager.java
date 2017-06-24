package ar.edu.itba.it.hci.bestflight;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by juan on 6/23/2017.
 */

public class AlertManager extends Application {
    private static ArrayList<Alert> alerts = new ArrayList<Alert>();


    private static HashMap<Integer, Flight> notificationsMap = new HashMap<Integer, Flight>();

    private static Context context;



    //singleton
    private static AlertManager instance = null;

    public static AlertManager getInstance() {
        if(instance == null) {
            instance = new AlertManager();
        }
        return instance;
    }




    public static void addAlert(Flight flight, Context context){

        notificationsMap.put(flight.id, flight);
        //


        Gson gson = new Gson();
        MapWrapper wrapper = new MapWrapper();
        wrapper.myMap = notificationsMap;
        String serializedMap = gson.toJson(wrapper);

        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("NotificationsMap", serializedMap).commit();




        //
        alerts.add(new Alert(flight.flightNumber, flight.airline));
    }



    public static void removeAlert(Integer id){

        Flight fl = notificationsMap.get(id);

        //
        Integer flightNumber = fl.flightNumber;
        String airline = fl.airline;
        //

        notificationsMap.remove(id);



        //
        for(Alert a : alerts)
            if(a.equals(new Alert(flightNumber, airline)))
                alerts.remove(a);
    }



    public static ArrayList<Alert> getAlerts() {
        return alerts;
    }

    public static HashMap<Integer, Flight> getNotificationsMap() {
        return notificationsMap;
    }

    public static void setNotificationsMap(HashMap<Integer, Flight> map) {
        notificationsMap.putAll(map);
    }

}
