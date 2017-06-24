package ar.edu.itba.it.hci.bestflight;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static android.widget.Toast.LENGTH_LONG;


public class DealsFragment extends Fragment {




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deals, container, false);
    }
    ArrayAdapter<String> adapter;
    private ArrayList<String> cities=new ArrayList<>();
    private ProgressDialog progressDialog;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.title_deals));
        ListView l = (ListView) getActivity().findViewById(R.id.list);
        adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, cities);
        l.setAdapter(adapter);
        getNearestCity();
    }

    private void getDeals(final String citi) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading"); // SETEAR LOS STRINGS Y LLAMARLOS TIPO getActivity().getString(R.string.loading_airlines)
        progressDialog.setCancelable(false);
        progressDialog.show();



        String url = "http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getflightdeals&from="+citi;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    // ACA SE TRABAJA CON LOS ELEMENTOS DEL JSON
                    for (int i = 0; i < response.getJSONArray("deals").length(); i++) {
                        adapter.add((response.getJSONArray("deals").getJSONObject(i)).getJSONObject("city").getString("name"));
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "error"+ citi, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error"+ citi, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        RequestsManager.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
        progressDialog.dismiss();
    }





    private static final String OFFERS_ITEMS = "items";

    private static final String CITIES_STRING_BASE = "http://hci.it.itba.edu.ar/v1/api/geo.groovy?method=getcitiesbyposition";

    private static final int RADIUS = 100;

    public void getNearestCity() {

        String location_stringEnd = "&latitude=" + MainActivity.getLatitude() + "&longitude=" + MainActivity.getLongitud()
                + "&radius=" + RADIUS;

        String url = CITIES_STRING_BASE + location_stringEnd;

        JsonObjectRequest
                jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    CityNearBy(response);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.toast_error_gps), Toast.LENGTH_LONG).show();
//                    progressDialog.dismiss();
                    getDeals("BUE");


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
            return;
        }

        getDeals(cityFrom);

    }






















}