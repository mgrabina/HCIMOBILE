package ar.edu.itba.it.hci.bestflight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by juan on 6/23/2017.
 */

public class AlertManager {
    private static ArrayList<Alert> alerts = new ArrayList<Alert>();


    private static HashMap<Integer, Flight> notificationsMap = new HashMap<Integer, Flight>();


    //singleton
    private static AlertManager instance = null;

    public static AlertManager getInstance() {
        if(instance == null) {
            instance = new AlertManager();
        }
        return instance;
    }




    public static void addAlert(Flight flight){

        notificationsMap.put(flight.id, flight);


        //
        alerts.add(new Alert(flight.flightNumber, flight.airline));
    }



    public static void removeAlert(String id){

        Flight fl = notificationsMap.get(id);

        //
        Integer flightNumber = fl.flightNumber;
        String airline = fl.airline;
        //

        notificationsMap.remove(fl);



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

}
