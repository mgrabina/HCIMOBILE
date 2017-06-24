package ar.edu.itba.it.hci.bestflight;

import android.content.Context;
import android.content.res.Configuration;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;


public class SettingsFragment extends Fragment {
    private Integer[] intervalos;
    private static String displayLanguage = "EN";
    private static boolean EN = true;
    private static Integer interval=0;

    public String getDisplayLanguage() {
        return displayLanguage;
    }

    public Integer getInterval() {
        return intervalos[interval];
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        displayLanguage = Locale.getDefault().getDisplayLanguage();
        getActivity().setTitle(getResources().getString(R.string.settings_title));

        final Spinner s = (Spinner) getActivity().findViewById(R.id.selectorDeTiempo);
        this.intervalos = new Integer[]{
                1, 5, 10, 30, 60, 120, 500, 1000
        };



        final ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, this.intervalos);

        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Si selecciono uno
                interval = intervalos[position];
                Toast.makeText(getActivity(), getString(R.string.interval_change_toast)+(intervalos[position]).toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Si no debo agarrar el que tenia
                parent.setSelection(interval);
            }
        });
        final Button languageS = (Button) getActivity().findViewById(R.id.selectorIdioma);
        languageS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(EN) {
                    Locale locale = new Locale("es");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.setLocale(locale);
                    getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());
                    Toast.makeText(getActivity(), getString(R.string.language_change_notification), Toast.LENGTH_SHORT).show();

                    displayLanguage = "ES";
                    EN = false;
                }else{
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.setLocale(locale);
                    getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());
                    Toast.makeText(getActivity(), getString(R.string.language_change_notification), Toast.LENGTH_SHORT).show();

                    displayLanguage = "EN";

                    EN = true;
                }


                MainActivity.rebootFragment(new SettingsFragment(), "settingsFragment");

            }
        });

                ListView alertsList = (ListView) getActivity().findViewById(R.id.alertsList);
        ArrayList<Alert> a = AlertManager.getAlerts();
        ArrayAdapter<Alert> adapt = new ArrayAdapter<Alert>(getContext(), android.R.layout.simple_list_item_1, a);
        alertsList.setAdapter(adapt);
        alertsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Si clickeo un item.
                //Borrar item
                //AlertManager.removeAlert(flight, airline);
            }
        });

    }
}
