package ee364h.wearablehealthtracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ee364h.wearablehealthtracker.HomePageFragment.OnGraphSelectedListener} interface
 * to handle interaction events.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class HomePageFragment extends Fragment {

    private OnGraphSelectedListener graphListener;
    private OnSettingsSelectedListener settingsListener;    //TODO: merge the two listeners, interfaces, methods into one that just decides how the activity responds to button presses in general
    private OnBluetoothSelectedListener bluetoothListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * @return A new instance of fragment HomePageFragment.
     */
    public static HomePageFragment newInstance() {
        HomePageFragment fragment = new HomePageFragment();
        return fragment;
    }
    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.NoActionBarTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // inflate the layout using the cloned inflater, not default inflater
        return localInflater.inflate(R.layout.home_page_layout, container, false);
    }

    public void onStart(){
        super.onStart();
        getActivity().getActionBar().hide();
        updateStepCount();
        updateMeasurements();

        /******Buttons*******/
        ImageButton pedometer = (ImageButton ) getView().findViewById(R.id.pedometer_image);
        pedometer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGraph(GraphType.PEDOMETER);
            }
        });

        ImageButton pulse = (ImageButton) getView().findViewById(R.id.pulse_image);
        pulse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGraph(GraphType.PULSE);
            }
        });

        ImageButton bloodOx = (ImageButton) getView().findViewById(R.id.bloodOx_image);
        bloodOx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGraph(GraphType.BLOODOX);
            }
        });

        ImageButton temperature = (ImageButton) getView().findViewById(R.id.temperature_image);
        temperature.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGraph(GraphType.TEMPERATURE);
            }
        });

        ImageButton settings = (ImageButton) getView().findViewById(R.id.settings_image);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchSettings();
            }
        });

        ImageButton bluetooth = (ImageButton) getView().findViewById(R.id.bluetooth_image);
        bluetooth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchBluetooth();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            graphListener = (OnGraphSelectedListener) activity;
            settingsListener = (OnSettingsSelectedListener) activity;
            bluetoothListener = (OnBluetoothSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement all Listeners");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        graphListener = null;
        settingsListener = null;
        bluetoothListener = null;
    }

    public void updateStepCount(){
        TextView pedometer_value = (TextView) getView().findViewById(R.id.pedometer_value);
        pedometer_value.setText(String.format("%.0f",(((MainActivity) getActivity()).getStepCount())));
    }
    public void updateMeasurements(){    
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        
        TextView pulse_value = (TextView) getView().findViewById(R.id.pulse_value);
        int current_pulse = ((MainActivity) getActivity()).getCurrentData().getInt("pulse");
        pulse_value.setText("" + current_pulse);

        TextView temperature_value = (TextView) getView().findViewById(R.id.temperature_value);
        double current_temp = ((MainActivity) getActivity()).getCurrentData().getDouble("temperature");
        temperature_value.setText(String.format("%.2f",current_temp));
        
        TextView bloodOx_value = (TextView) getView().findViewById(R.id.bloodOx_value);
        int current_bloodOx = ((MainActivity) getActivity()).getCurrentData().getInt("bloodox");
        bloodOx_value.setText("" + current_bloodOx);

        
        ProgressBar pulse_progress = (ProgressBar) getView().findViewById(R.id.pulse_progress);
        pulse_progress.setMax(Integer.valueOf(prefs.getString("pref_key_goal_pulse", "70")));
        pulse_progress.setProgress(current_pulse);
        
        ProgressBar bloodOx_progress = (ProgressBar) getView().findViewById(R.id.bloodOx_progress);
        bloodOx_progress.setMax(Integer.valueOf(prefs.getString("pref_key_goal_bloodox", "100")));
        bloodOx_progress.setProgress(current_bloodOx);
        
        ProgressBar temperature_progress = (ProgressBar) getView().findViewById(R.id.temperature_progress);
        temperature_progress.setMax(Integer.valueOf(prefs.getString("pref_key_goal_temperature", "37"))*100);
        temperature_progress.setProgress((int) Math.floor(current_temp*100));
    }

    /****LaunchGraph****
     * Launches a graph fragment depending on which button was pressed
     * by notifying the main activity and passing it the graphType
     * @param graphType - type of graph to launch
     */
    public void launchGraph(GraphType graphType){
        graphListener.onGraphSelected(graphType);
    }

    /****LaunchSettings****
     * Launches the Settings fragment
     */
    public void launchSettings(){
        settingsListener.onSettingsSelected();
    }

    /****LaunchBluetooth****
     * Launches the Bluetooth fragment
     */
    public void launchBluetooth(){
        bluetoothListener.onBluetoothSelected();
    }



    /****OnGraphSelectedListener****
     * Tells the activity to open a graphFragment and
     * pass the graphFragment the type of graph based
     * on which button was pressed.
     */
    public interface OnGraphSelectedListener {
        public void onGraphSelected(GraphType graphType);
    }

    /****OnSettingsSelectedListener****
     * Tells the activity to open the Settings Fragment
     */
    public interface OnSettingsSelectedListener {
        public void onSettingsSelected();
    }

    /****OnBluetoothSelectedListener****
     * Tells the activity to open the Bluetooth Fragment
     */
    public interface OnBluetoothSelectedListener {
        public void onBluetoothSelected();
    }

}
