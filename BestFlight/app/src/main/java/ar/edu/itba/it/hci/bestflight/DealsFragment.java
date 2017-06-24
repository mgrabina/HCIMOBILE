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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static android.widget.Toast.LENGTH_LONG;


public class DealsFragment extends Fragment {




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deals, container, false);
    }
    ArrayAdapter<String> adapter3;
    ArrayAdapter<String> adapter2;
    ArrayAdapter<String> adapter;
    private ArrayList<String> cities=new ArrayList<>();
    private ArrayList<String> citiesid=new ArrayList<>();
    private ArrayList<String> dealscities=new ArrayList<>();
    private ProgressDialog progressDialog;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView l = (ListView) getActivity().findViewById(R.id.list);
        final Spinner s= (Spinner) getActivity().findViewById(R.id.spinner);
        adapter2=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dealscities);
        adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, cities);
        s.setAdapter(adapter2);
        l.setAdapter(adapter);
        getCities();
        getDeals("BUE");
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Si selecciono uno
                String ids="";
                ids=citiesid.get(position);
                adapter.clear();
                getDeals(ids);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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


    private void getCities() {
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("loading"); // SETEAR LOS STRINGS Y LLAMARLOS TIPO getActivity().getString(R.string.loading_airlines)
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        String url = "http://hci.it.itba.edu.ar/v1/api/geo.groovy?method=getcities";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    // ACA SE TRABAJA CON LOS ELEMENTOS DEL JSON
                    for (int i = 0; i < response.getJSONArray("cities").length(); i++) {
                        cities.add((response.getJSONArray("cities").getJSONObject(i)).getString("name"));
                        adapter2.add((response.getJSONArray("cities").getJSONObject(i)).getString("name"));
                        citiesid.add((response.getJSONArray("cities").getJSONObject(i)).getString("id"));
                    }

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "error2", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        RequestsManager.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }


}