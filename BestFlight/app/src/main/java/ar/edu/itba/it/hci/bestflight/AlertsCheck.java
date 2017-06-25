package ar.edu.itba.it.hci.bestflight;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Santiago on 6/23/2017.
 */

public class AlertsCheck extends BroadcastReceiver {

    AlertManager alertManager = AlertManager.getInstance();
    HashMap<Integer, Flight> map;
    Flight flightA;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        String serializedMap = PreferenceManager.getDefaultSharedPreferences(context).getString("NotificationsMap", "empty");

        if(!serializedMap.equals("empty")) {
          // Log.d("ENTRO", serializedMap);
           Gson gson = new Gson();
           MapWrapper wrapper = gson.fromJson(serializedMap, MapWrapper.class);
           HashMap<Integer, Flight> map = wrapper.myMap;
           this.map = map;
            AlertManager.setNotificationsMap(map);

            checkChanges();
           //startNotification();
       }
       //else
       //     Log.d("NO-ENTRO", serializedMap);
    }




    private void checkChanges(){

        final HashMap<Integer, Flight> mapAux = new  HashMap<Integer, Flight>();
        mapAux.putAll(map);

        for (Integer id : map.keySet()) {

            flightA = map.get(id);
            String airline = flightA.airline;
            String flightN = flightA.flightNumber.toString();

            String url = "http://hci.it.itba.edu.ar/v1/api/status.groovy?method=getflightstatus&airline_id=" + airline + "&flight_number=" + flightN;

            //Log.d("url", url);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                            String status = response.getJSONObject("status").getString("status");
                            String airline = response.getJSONObject("status").getJSONObject("airline").getString("name");
                            String id = response.getJSONObject("status").getString("id");
                            String flightNumber = response.getJSONObject("status").getString("number");

                            String departureTime = response.getJSONObject("status").getJSONObject("departure").getString("scheduled_time");
                            String arrivalTime = response.getJSONObject("status").getJSONObject("arrival").getString("scheduled_time");
                            String departureTerminal = response.getJSONObject("status").getJSONObject("departure").getJSONObject("airport").getString("terminal");
                            String arrivalTerminal = response.getJSONObject("status").getJSONObject("arrival").getJSONObject("airport").getString("terminal");
                            String departureGate = response.getJSONObject("status").getJSONObject("departure").getJSONObject("airport").getString("gate");
                            String arrivalGate = response.getJSONObject("status").getJSONObject("arrival").getJSONObject("airport").getString("gate");
                            String baggageGate = response.getJSONObject("status").getJSONObject("arrival").getJSONObject("airport").getString("baggage");


                            Flight flightB = new Flight(Integer.parseInt(flightNumber), airline, status, Integer.parseInt(id), departureTime,
                                    arrivalTime, departureTerminal, arrivalTerminal, departureGate, arrivalGate, baggageGate);

                            if( !flightA.status.equals(flightB.status) || !flightA.departureTime.equals(flightB.departureTime) || !flightA.arrivalTime.equals(flightB.arrivalTime)
                                    || !flightA.departureTerminal.equals(flightB.departureTerminal) || !flightA.arrivalTerminal.equals(flightB.arrivalTerminal)
                                    || !flightA.departureGate.equals(flightB.departureGate) || !flightA.arrivalGate.equals(flightB.arrivalGate)
                                    || !flightA.baggageGate.equals(flightB.baggageGate)){


                                mapAux.put(flightB.id, flightB);

                                Gson gson = new Gson();
                                MapWrapper wrapper = new MapWrapper();
                                wrapper.myMap = mapAux;
                                String serializedMap = gson.toJson(wrapper);

                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("NotificationsMap", serializedMap).commit();

                                startNotification(flightB);


                            }

                    } catch (JSONException e) {


                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                }
            });
            RequestsManager.getInstance(context).addToRequestQueue(jsObjRequest);


        }
    }



    public void startNotification() {

       // map = AlertManager.getNotificationsMap();

        for (Integer id : map.keySet()) {

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_airplane_grey600_48dp)
                            .setContentTitle(map.get(id).airline + " - Flight: " + map.get(id).flightNumber)
                            .setContentText("The flight status has been updated")
                            .setAutoCancel(true);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, MainActivity.class);

            String airline = map.get(id).airline;
            String flightNumber = map.get(id).flightNumber.toString();
            resultIntent.putExtra("airline", airline);
            resultIntent.putExtra("flightNumber", flightNumber);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( id, PendingIntent.FLAG_UPDATE_CURRENT  );

            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            int mId = id;
            mNotificationManager.notify(mId, mBuilder.build());
        }
    }



    public void startNotification(Flight flight) {

        // map = AlertManager.getNotificationsMap();



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_airplane_grey600_48dp)
                        .setContentTitle(flight.airline + " - Flight: " + flight.flightNumber)
                        .setContentText("The flight status has been updated")
                        .setAutoCancel(true);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, MainActivity.class);

            String airline = flight.airline;
            String flightNumber =flight.flightNumber.toString();
            resultIntent.putExtra("airline", airline);
            resultIntent.putExtra("flightNumber", flightNumber);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( flight.id, PendingIntent.FLAG_UPDATE_CURRENT  );

            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            int mId = flight.id;
            mNotificationManager.notify(mId, mBuilder.build());

    }



    ///
    ///
    ///
    //
    //
    //
    //
    //
}
