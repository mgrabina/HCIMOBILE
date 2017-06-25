package ar.edu.itba.it.hci.bestflight;

import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Santiago on 6/23/2017.
 */

public class AlertsCheck extends BroadcastReceiver {

    AlertManager alertManager = AlertManager.getInstance();
    HashMap<Integer, Flight> map;

    @Override
    public void onReceive(Context context, Intent intent) {

        String serializedMap = PreferenceManager.getDefaultSharedPreferences(context).getString("NotificationsMap", "empty");

        if(!serializedMap.equals("empty")) {
          // Log.d("ENTRO", serializedMap);
           Gson gson = new Gson();
           MapWrapper wrapper = gson.fromJson(serializedMap, MapWrapper.class);
           HashMap<Integer, Flight> map = wrapper.myMap;
           this.map = map;



           startNotification(context);
       }
       //else
       //     Log.d("NO-ENTRO", serializedMap);
    }








    public void startNotification(Context context) {

       // map = AlertManager.getNotificationsMap();

        for (Integer id : map.keySet()) {

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_menu_camera)
                            .setContentTitle("My notification")
                            .setContentText("Hello Waldo!")
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
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            int mId = id;
            mNotificationManager.notify(mId, mBuilder.build());
        }
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
