package ee364h.wearablehealthtracker;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


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
        return inflater.inflate(R.layout.home_page_layout, container, false);
    }

    public void onStart(){
        super.onStart();

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
