package ee364h.wearablehealthtracker;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * For a given BLE device, this Fragment provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Fragment
 * communicates with {@code BluetoothLEService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlFragment extends Fragment {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final static String TAG = DeviceControlFragment.class.getSimpleName();

    // private TextView mConnectionState;
    // private TextView mDataField;
    private String mDeviceName;
    private static final String UUID_SERIAL_PORT_PROFILE 
                           = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
    private static final String HEADSET_MAC_ADDRESS = "FC:C5:72:23:A7:D8";
    private static final String HEADSET_NAME = "UART";
    private String mDeviceAddress;
    // private ExpandableListView mGattServicesList;
    private BluetoothLEService mBluetoothLEService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param myName - device name.
     * @param myAddress - device address.
     * @return A new instance of fragment DeviceControlFragment.
     */
    public static DeviceControlFragment newInstance(String myName, String myAddress) {
        DeviceControlFragment fragment = new DeviceControlFragment();
        Bundle args = new Bundle();
        args.putString(EXTRAS_DEVICE_NAME, myName);
        args.putString(EXTRAS_DEVICE_ADDRESS, myAddress);
        fragment.setArguments(args);
        return fragment;
    }

    public DeviceControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // setHasOptionsMenu(true);
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // inflate the layout using the cloned inflater, not default inflater
        return inflater.inflate(R.layout.gatt_services_characteristics, container, false);
    }*/

    @Override
    public void onStart(){
        super.onStart();

        if (getArguments() != null) {
            mDeviceName = getArguments().getString(EXTRAS_DEVICE_NAME);
            mDeviceAddress = getArguments().getString(EXTRAS_DEVICE_ADDRESS);
        }

/*        // Sets up UI references.
        ((TextView) getView().findViewById(R.id.device_address)).setText(HEADSET_MAC_ADDRESS);
        mGattServicesList = (ExpandableListView) getView().findViewById(R.id.gatt_services_list_expandable);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) getView().findViewById(R.id.connection_state);
        mDataField = (TextView) getView().findViewById(R.id.data_value);

        // Bring back the necessary view components
        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.device_address_linear_layout);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout = (LinearLayout) getView().findViewById(R.id.connection_state_linear_layout);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout = (LinearLayout) getView().findViewById(R.id.data_value_linear_layout);
        linearLayout.setVisibility(View.VISIBLE);;*/

        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLEService.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE);

        getActivity().invalidateOptionsMenu();

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLEService != null) {
            final boolean result = mBluetoothLEService.connect(HEADSET_MAC_ADDRESS);
            Log.d(TAG, "Connect request result=" + result);
            if(!result){
                Toast.makeText(getActivity(), "Failed to Connect to UART", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).toggleBluetoothFragment();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
        ((MainActivity)getActivity()).updateBluetoothConnectionStatus(false);
        Log.d(TAG,"DeviceControlFragment Paused");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection);
        mBluetoothLEService = null;
        Log.d(TAG,"DeviceControlFragment Destroyed");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"DeviceControlFragment Detached");
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLEService = ((BluetoothLEService.LocalBinder) service).getService();
            if (!mBluetoothLEService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                getActivity().finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            boolean result = mBluetoothLEService.connect(HEADSET_MAC_ADDRESS);
            if(!result){
                Toast.makeText(getActivity(), "Failed to Connect to UART", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).toggleBluetoothFragment();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLEService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                // updateConnectionState(R.string.connected);
                // getActivity().invalidateOptionsMenu();
                ((MainActivity)getActivity()).updateBluetoothConnectionStatus(true);

            } else if (BluetoothLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                // updateConnectionState(R.string.disconnected);
                // getActivity().invalidateOptionsMenu();
                ((MainActivity)getActivity()).updateBluetoothConnectionStatus(false);
                // clearUI();
                Toast.makeText(getActivity(), "Disconnected from UART", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).toggleBluetoothFragment();
            } else if (BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                // displayGattServices(mBluetoothLEService.getSupportedGattServices());
                mBluetoothLEService.enableTXNotification();
            } else if (BluetoothLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                Bundle bundle = intent.getBundleExtra(BluetoothLEService.EXTRA_DATA);
                Log.d(TAG,"Data Received: " + bundle.getString("values"));
                // displayData(bundle.getString("values"));
                writeDataToFile(bundle.getString("values"));
                ((MainActivity) getActivity()).updateCurrentData(bundle);
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    /*private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLEService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLEService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLEService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };*/

    // private void clearUI() {
    //     mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
    //     mDataField.setText(R.string.no_data);
    // }


/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.gatt_services, menu);
        getActivity().getActionBar().setTitle(HEADSET_NAME);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().show();
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
//        return ;
    }*/

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLEService.connect(HEADSET_MAC_ADDRESS);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLEService.disconnect();
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

/*    private void updateConnectionState(final int resourceId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }*/

/*    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }
*/
    private void writeDataToFile(String data){
        if(data != null){
            String filename = ((MainActivity) getActivity()).getDataFilename();
            FileOutputStream outputStream;
            try {
              outputStream = getActivity().openFileOutput(filename, Context.MODE_APPEND);
              outputStream.write(data.getBytes());
              outputStream.write("\n".getBytes());
              outputStream.close();
            } catch (Exception e) {
              e.printStackTrace();
            }
        }

    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
/*    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, HeadsetAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, HeadsetAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                getActivity(),
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }*/

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    
}
