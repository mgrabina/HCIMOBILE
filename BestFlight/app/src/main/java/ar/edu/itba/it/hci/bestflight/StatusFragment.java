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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StatusFragment extends Fragment {


    private ProgressDialog progressDialog;
    private ArrayList<String> airlinesId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_status, container, false);
    }



    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        airlinesId = new ArrayList<String>();
        getAirlines();



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
                   // for(int i=0; i<response.getJSONArray("airlines").length(); i++) {
                   //     airlinesId.add((response.getJSONArray("airlines").getJSONObject(i)).getString("id"));
                   //
                   // }
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
