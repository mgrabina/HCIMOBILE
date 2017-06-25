package ar.edu.itba.it.hci.bestflight;

import android.app.Activity;
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
    private static ArrayList<Alert> alerts;
    private static boolean getNotificaciones = true;
    public String getDisplayLanguage() {
        return displayLanguage;
    }

    public Integer getInterval() {
        return intervalos[interval];
    }

    public static boolean getNotifications() {
        return getNotificaciones;
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(getString(R.string.settings_title));

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
        ToggleButton  tb = (ToggleButton) getActivity().findViewById(R.id.toggleNotis);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getNotificaciones = isChecked;
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
        alerts = AlertManager.getAlerts();
        ArrayAdapter<Alert> adapt = new ArrayAdapter<Alert>(getContext(), android.R.layout.simple_list_item_1, alerts);
        alertsList.setAdapter(adapt);
        alertsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Si clickeo un item.
                //Borrar item
                //AlertManager.removeAlert(position);
                //MainActivity.rebootFragment(new SettingsFragment(), "settingsFragment");

                String airline = alerts.get(position).getAirline();
                String flightNumber = alerts.get(position).getFlight().toString();

                getActivity().getIntent().putExtra("airline", airline);
                getActivity().getIntent().putExtra("flightNumber", flightNumber);

                Bundle bundle = getActivity().getIntent().getExtras();

                getActivity().getIntent().removeExtra("airline");
                getActivity().getIntent().removeExtra("flightNumber");

                Fragment fragment = new StatusFragment();
                fragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "statusFragment").addToBackStack("statusFragment").commit();

            }
        });

    }

    public static void checkLanguage(String displayLanguage, Activity a, Fragment current, String currentFragment, Context c) {
        Log.e("Idioma de entrada", displayLanguage);
        if (displayLanguage == "en" && !EN){
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            a.getBaseContext().getResources().updateConfiguration(config, a.getBaseContext().getResources().getDisplayMetrics());

            displayLanguage = "EN";

            EN = true;
            if(current != null)
                MainActivity.rebootFragment(current, currentFragment);
        }else if(displayLanguage == "es" && EN){
            Locale locale = new Locale("es");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            a.getBaseContext().getResources().updateConfiguration(config, a.getBaseContext().getResources().getDisplayMetrics());

            displayLanguage = "ES";
            EN = false;
            if(current != null)
                MainActivity.rebootFragment(current, currentFragment);
        }else{
            //No tengo ese idioma, ingles default
        }


    }
}
