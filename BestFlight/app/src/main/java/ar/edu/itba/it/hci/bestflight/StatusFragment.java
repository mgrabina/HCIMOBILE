package ar.edu.itba.it.hci.bestflight;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StatusFragment extends Fragment {


    private ProgressDialog progressDialog;
    private Button searchButton;
    private Spinner airlinesSpinner;
    private EditText flightNumberET;
    private Map<String, String> airlinesMap;
    ArrayAdapter<String> adapter;
    List<String> airlines;
    private LinearLayout resultLayout;
    private FloatingActionButton fabAddNot;
    AlertManager alertManager;
    Flight flightA;

    TextView info;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        return inflater.inflate(R.layout.fragment_status, container, false);
    }



    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getAirlines();

        info = (TextView) getView().findViewById(R.id.infoFlightET);

        airlinesMap = new HashMap<String, String>();
        airlines = new ArrayList<String>();
        alertManager = AlertManager.getInstance();

        searchButton = (Button) getView().findViewById(R.id.searchButton);
        flightNumberET = (EditText) getView().findViewById(R.id.flightNumberET);
        airlinesSpinner = (Spinner) getView().findViewById(R.id.airlinesSpinner);

        if (searchButton != null) {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    checkStatus();

                }
            });
        }


            resultLayout = (LinearLayout) getView().findViewById(R.id.resultLayout);
            resultLayout.setVisibility(View.INVISIBLE);
            fabAddNot = (FloatingActionButton) getView().findViewById(R.id.fabAddNot);


            if (fabAddNot != null) {
                fabAddNot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        addNotification();

                    }
                });

            }
        //
        //


    }





    private void checkStatus(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading"); // SETEAR LOS STRINGS Y LLAMARLOS TIPO getActivity().getString(R.string.loading_airlines)
        progressDialog.setCancelable(false);
        progressDialog.show();

        String flightN = flightNumberET.getText().toString();
        String airline = airlinesMap.get(airlinesSpinner.getSelectedItem().toString());

        String  url = "http://hci.it.itba.edu.ar/v1/api/status.groovy?method=getflightstatus&airline_id=" +airline+ "&flight_number=" + flightN ;

        //Log.d("url", url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                   if (response.has("status") ) {

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



                       flightA = new Flight(Integer.parseInt(flightNumber), airline, status, Integer.parseInt(id), departureTime,
                               arrivalTime, departureTerminal, arrivalTerminal, departureGate, arrivalGate, baggageGate);
                       showResult();

                   }
                   else{
                       Toast.makeText(getActivity(), response.getJSONObject("error").getString("message") , Toast.LENGTH_LONG).show();
                       resultLayout.setVisibility(View.GONE);
                   }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "error" , Toast.LENGTH_LONG).show();
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



    private void getAirlines() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading"); // SETEAR LOS STRINGS Y LLAMARLOS TIPO getActivity().getString(R.string.loading_airlines)
        progressDialog.setCancelable(false);
        progressDialog.show();

        String  url = "http://hci.it.itba.edu.ar/v1/api/misc.groovy?method=getairlines";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    // ACA SE TRABAJA CON LOS ELEMENTOS DEL JSON
                   for(int i=0; i<response.getJSONArray("airlines").length(); i++) {

                       airlinesMap.put(response.getJSONArray("airlines").getJSONObject(i).getString("name"),response.getJSONArray("airlines").getJSONObject(i).getString("id") );
                       airlines.add(response.getJSONArray("airlines").getJSONObject(i).getString("name"));
                    }

                    adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item, airlines);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    airlinesSpinner.setAdapter(adapter);

                    setSearch();

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "error" , Toast.LENGTH_LONG).show();
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
        //progressDialog.dismiss();
    }


    private void showResult(){


        resultLayout.setVisibility(View.VISIBLE);
        setFabImage();
        String flightInfo = "AIRLINE: " +flightA.airline + "\nFLIGHT NUMBER: "+ flightA.flightNumber +"\n\ndeparture: " +flightA.departureTime + "\nTerminal: " +flightA.departureTerminal + ", Gate: " +flightA.departureGate + "\narrival: " +flightA.arrivalTime + "\nTerminal: " +flightA.arrivalTerminal + ", Gate: " +flightA.arrivalGate + "\nBagagge Gate: " + flightA.baggageGate;
        info.setText(flightInfo);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


    private void addNotification() {

    /*
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading"); // SETEAR LOS STRINGS Y LLAMARLOS TIPO getActivity().getString(R.string.loading_airlines)
        progressDialog.setCancelable(false);
        progressDialog.show();



        while( !((MainActivity)getActivity()).updated ){
            Log.d("acatpy","acatoy");
        }

        progressDialog.dismiss();
    */
        if (AlertManager.getNotificationsMap().containsKey(flightA.id)) {

            AlertManager.removeAlert(flightA.id, getContext());
            fabAddNot.setImageResource(R.drawable.ic_plus_white_48dp);
            Toast.makeText(getActivity(), "removed", Toast.LENGTH_LONG).show();

        } else {
            AlertManager.addAlert(flightA, getContext());
            fabAddNot.setImageResource(R.drawable.ic_minus_white_48dp);
            Toast.makeText(getActivity(), "added", Toast.LENGTH_LONG).show();

        }
    }

    private void setFabImage(){


        if( AlertManager.getNotificationsMap().containsKey(flightA.id)){
           // Log.d("SETFABIMAGE", "PLUS (contain)");
            fabAddNot.setImageResource(R.drawable.ic_minus_white_48dp);


        }
        else{
           // Log.d("SETFABIMAGE", "MINUS (notContain)");
            fabAddNot.setImageResource(R.drawable.ic_plus_white_48dp);

        }


    }






    public void setSearch(){

        if(this.getArguments() != null){

            String airline = this.getArguments().getString("airline");

            int position = airlines.indexOf(airline);
            airlinesSpinner.setSelection(position);

            flightNumberET.setText(this.getArguments().getString("flightNumber"));

            searchButton.performClick();


        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if(progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();



        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
