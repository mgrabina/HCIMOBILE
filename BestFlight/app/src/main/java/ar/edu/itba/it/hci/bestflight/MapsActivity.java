package ar.edu.itba.it.hci.bestflight;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.app.PendingIntent.getActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private ProgressDialog progressDialog;
    static private HashMap<City, Double> cities;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cities = new HashMap<City, Double>();
        getCities();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for(City c : cities.keySet()){
            MarkerOptions m = new MarkerOptions();
            m.position(new LatLng(c.getLatitude(), c.getLongitude()));
            BitmapDescriptor icon = null;
            if(cities.get(c) < 500.0){
                Drawable d = getResources().getDrawable(R.drawable.aiportgreen);
                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                icon = BitmapDescriptorFactory.fromBitmap(bitmap);
            }else if (cities.get(c) > 500.00 && cities.get(c) < 1000.00){
                Drawable d = getResources().getDrawable(R.drawable.aiportyellow);
                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                icon = BitmapDescriptorFactory.fromBitmap(bitmap);
            }else{
                Drawable d = getResources().getDrawable(R.drawable.aiportred);
                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                icon = BitmapDescriptorFactory.fromBitmap(bitmap);
            }
            m.icon(icon);
            m.title(c.getName() + " " + cities.get(c).toString());
            mMap.addMarker(m);
        }

        mMap.moveCamera(CameraUpdateFactory.zoomBy(0));
    }
    private void getCities() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading"); // SETEAR LOS STRINGS Y LLAMARLOS TIPO getActivity().getString(R.string.loading_airlines)
        progressDialog.setCancelable(false);
        progressDialog.show();
        String locationFrom = "BUE";
        String  url = "http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getflightdeals&from="+locationFrom;


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    // ACA SE TRABAJA CON LOS ELEMENTOS DEL JSON
                    for(int i=0; i<response.getJSONArray("deals").length(); i++) {
                        String countryId = (response.getJSONArray("deals").getJSONObject(i)).getJSONObject("city").getJSONObject("country").getString("id");
                        String id = (response.getJSONArray("deals").getJSONObject(i)).getJSONObject("city").getString("id");
                         String name = (response.getJSONArray("deals").getJSONObject(i)).getJSONObject("city").getString("name");
                         double longitude = (response.getJSONArray("deals").getJSONObject(i)).getJSONObject("city").getDouble("longitude");
                         double latitude = (response.getJSONArray("deals").getJSONObject(i)).getJSONObject("city").getDouble("latitude");
                        double price = (response.getJSONArray("deals").getJSONObject(i)).getDouble("price");
                        City newCity = new City(id, countryId, name, longitude, latitude);
                        Log.e("City", newCity.toString());
                        cities.put(newCity, price);
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Log.e("Error", e.toString());
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "EEee");
                progressDialog.dismiss();
            }
        });
        RequestsManager.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
