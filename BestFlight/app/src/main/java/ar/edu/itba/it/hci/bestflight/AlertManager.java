package ar.edu.itba.it.hci.bestflight;

import java.util.HashSet;
import java.util.ArrayList;

/**
 * Created by juan on 6/23/2017.
 */

public class AlertManager {
    private static ArrayList<Alert> alerts = new ArrayList<Alert>();

    //singleton
    private static AlertManager instance = null;

    public static AlertManager getInstance() {
        if(instance == null) {
            instance = new AlertManager();
        }
        return instance;
    }




    public static void addAlert(Integer flight, String airline){
        alerts.add(new Alert(flight, airline));
    }
    public static void removeAlert(Integer flight, String airline){
        for(Alert a : alerts)
            if(a.equals(new Alert(flight, airline)))
                alerts.remove(a);
    }
    public static ArrayList<Alert> getAlerts() {
        return alerts;
    }
}
