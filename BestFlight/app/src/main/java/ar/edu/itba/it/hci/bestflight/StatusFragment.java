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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_status, container, false);
    }



    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getAirlines();
        airlinesMap = new HashMap<String, String>();
        airlines = new ArrayList<String>();

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

                   }
                   else{
                       Toast.makeText(getActivity(), response.getJSONObject("error").getString("message") , Toast.LENGTH_LONG).show();
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

}
