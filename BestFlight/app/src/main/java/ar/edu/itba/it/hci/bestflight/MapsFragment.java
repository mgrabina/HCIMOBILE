package ar.edu.itba.it.hci.bestflight;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private ProgressDialog progressDialog;
    static private HashMap<City, Double> cities;
    private GoogleMap mMap;
    MapView mapView;
    //List<MarkerOptions> markers;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);


        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cities = new HashMap<City, Double>();
        //markers = new ArrayList<MarkerOptions>();
        getActivity().setTitle(getResources().getString(R.string.title_maps));
        //setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
       //         .findFragmentById(R.id.map);
      // MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.content_frame);
        //MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragmentm);

       // mapFragment.getMapAsync(this);

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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        getNearestCity();




       // mMap.moveCamera(CameraUpdateFactory.zoomBy(0));



    }
    private void getCities(String locationFrom) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading)); // SETEAR LOS STRINGS Y LLAMARLOS TIPO getActivity().getString(R.string.loading_airlines)
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                        //
                        //add the marker
                        MarkerOptions m = new MarkerOptions();
                        m.position(new LatLng(newCity.getLatitude(), newCity.getLongitude()));
                        BitmapDescriptor icon = null;

                        if (cities.get(newCity) < 500.0) {
                            Drawable d = getResources().getDrawable(R.drawable.aiportgreen);
                            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                            icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                        } else if (cities.get(newCity) > 500.00 && cities.get(newCity) < 1000.00) {
                            Drawable d = getResources().getDrawable(R.drawable.aiportyellow);
                            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                            icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                        } else {
                            Drawable d = getResources().getDrawable(R.drawable.aiportred);
                            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                            icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                        }
                        m.icon(icon);
                        m.title(newCity.getName() + " " + cities.get(newCity).toString());
                        mMap.addMarker(m);
                        //
                    }//

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
        RequestsManager.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private static final String CITIES_STRING_BASE = "http://hci.it.itba.edu.ar/v1/api/geo.groovy?method=getcitiesbyposition";

    private static final int RADIUS = 100;

    public void getNearestCity() {

        String location_stringEnd = "&latitude=" + MainActivity.getLatitude() + "&longitude=" + MainActivity.getLongitud()
                + "&radius=" + RADIUS;

        String url = CITIES_STRING_BASE + location_stringEnd;
        MarkerOptions m = new MarkerOptions();
        m.position(new LatLng(MainActivity.getLatitude(), MainActivity.getLongitud()));
        BitmapDescriptor icon = null;
        Drawable d = getResources().getDrawable(R.drawable.mylocation);//cambiar a algun icono que sea desde
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        m.icon(icon);
        CameraUpdateFactory.newLatLng(m.getPosition());
        CameraUpdateFactory.zoomBy(5);
        JsonObjectRequest
                jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                   CityNearBy(response);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.toast_error_gps), Toast.LENGTH_LONG).show();
                    getCities("BUE");
//                    progressDialog.dismiss();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                // progressDialog.dismiss();

            }
        });
        RequestsManager.getInstance(getActivity()).addToRequestQueue(jsObjRequest);


    }

    private void CityNearBy(JSONObject data) throws JSONException {

        // First city in array is took as "From" for GoogleMap.

        JSONArray cities = data.getJSONArray("cities");

        JSONObject Firstelem = cities.getJSONObject(0);

        String cityFrom = Firstelem.getString("id");

        if (cityFrom == null) {
            //mainText.setText(R.string.city_not_found);
        }
        getCities(cityFrom);

    }











}
