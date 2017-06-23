package ar.edu.itba.it.hci.bestflight;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;


public class SettingsFragment extends Fragment {
    private Integer[] intervalos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Spinner s = (Spinner) getActivity().findViewById(R.id.selectorDeTiempo);
        this.intervalos = new Integer[]{
                1, 15, 30, 60, 120
        };
        final int intervalos = Log.e("intervalos", this.intervalos.toString());
        final int a = Log.e("activity", getActivity().toString());


        final ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, this.intervalos);
        final int adap = Log.e("ADAPTER", adapter.toString());
        final int spppp = Log.e("s", s.toString());

        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Si selecciono uno
                Toast.makeText(getContext(), SettingsFragment.this.intervalos[position].toString(), LENGTH_LONG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ToggleButton toggle = (ToggleButton) getActivity().findViewById(R.id.selectorIdioma);
        toggle.setTextOff("EN");
        toggle.setTextOn("ES");
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Si cambie el idioma
                Toast.makeText(getContext(), ((Boolean)isChecked).toString(), LENGTH_LONG);
            }
        });

    }
}
