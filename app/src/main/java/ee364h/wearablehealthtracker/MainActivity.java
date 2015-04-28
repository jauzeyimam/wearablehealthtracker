package ee364h.wearablehealthtracker;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends Activity
        implements GraphFragment.OnFragmentInteractionListener,
        HomePageFragment.OnGraphSelectedListener,
        HomePageFragment.OnSettingsSelectedListener, HomePageFragment.OnBluetoothSelectedListener, 
        DeviceScanFragment.OnBLEDeviceSelectedListener, SensorEventListener {

    private final static String HOME_FRAGMENT_TAG = "HomePageFragment";
    private final static String GRAPH_FRAGMENT_TAG = "GraphFragment";
    private final static String BLUETOOTH_FRAGMENT_TAG = "DeviceScanFragment";
    private final static String BLUETOOTH_DEVICE_FRAGMENT_TAG = "DeviceControlFragment";
    private final static String SETTINGS_FRAGMENT_TAG = "SettingsFragment";

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String DATA_FILENAME = "HealthTrackerData.txt";
    private float stepCount = 0;
    private SensorManager sensorManager;
    static PackageManager packageManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Started");
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            HomePageFragment home = new HomePageFragment();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, home, HOME_FRAGMENT_TAG)
                    .commit();
        }

/*        final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mDevice = mBluetoothAdapter.getRemoteDevice(HEADSET_MAC_ADDRESS);
        Log.e(TAG,"mDevice received: " + mDevice.toString());*/

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Log.d(TAG,"onCreate Finished");

        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void onGraphSelected(GraphType graphType){
        // New GraphFragment of the correct type
        GraphFragment graphFragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(GraphFragment.ARG_GRAPHTYPE,graphType.name());
        graphFragment.setArguments(args);

        //Replace HomePageFragment with GraphFragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, graphFragment, GRAPH_FRAGMENT_TAG);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    };

    public void onSettingsSelected(){
        Log.d(TAG,"SETTINGS SELECTED");
        // New SettingsFragment
        SettingsFragment settingsFragment = new SettingsFragment();

        //Replace HomePageFragment with GraphFragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, settingsFragment, SETTINGS_FRAGMENT_TAG);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        Log.d(TAG,"SETTINGS FRAGMENT INITIATED");
    };

    public void onBluetoothSelected(){
        Log.d(TAG,"BLUETOOTH SELECTED");
        // New SettingsFragment
        DeviceScanFragment bluetoothFragment = new DeviceScanFragment();

        //Replace HomePageFragment with GraphFragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, bluetoothFragment, BLUETOOTH_FRAGMENT_TAG);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        Log.d(TAG,"SCANNING FRAGMENT INITIATED");
    };

    public void onBLEDeviceSelected(String name, String address){
        Log.d(TAG,"DEVICE SELECTED");
        // New SettingsFragment
        DeviceControlFragment deviceControlFragment = new DeviceControlFragment();
        Bundle args = new Bundle();
        args.putString(DeviceControlFragment.EXTRAS_DEVICE_NAME, name);
        args.putString(DeviceControlFragment.EXTRAS_DEVICE_ADDRESS, address);
        deviceControlFragment.setArguments(args);

        //Replace HomePageFragment with GraphFragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, deviceControlFragment, BLUETOOTH_DEVICE_FRAGMENT_TAG);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        Log.d(TAG,"CONTROL FRAGMENT INITIATED");
    };

    @Override
    public void onResume(){
        super.onResume();
        /*try{
            openDeviceConnection(mDevice);
        }catch(IOException e) {
            Log.d(TAG, "Failed to openDeviceConnection(mDevice);");
        }*/
        packageManager = getPackageManager();

        if(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)){
            Toast.makeText(this, "Pedometer Feature is available.",Toast.LENGTH_LONG).show();
        } 

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
            Toast.makeText(this, "Count sensor not available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        getActionBar().hide();
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void onFragmentInteraction(Uri uri){};

    public String getDataFilename(){
        return DATA_FILENAME;
    }
    public float getStepCount(){
        return stepCount;
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        stepCount =  event.values[0];
        HomePageFragment home = (HomePageFragment) getFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG);
        home.updateStepCount();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /*Bluetooth Device Simple*/
/*    private static final String UUID_SERIAL_PORT_PROFILE 
                           = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
    private static final String HEADSET_MAC_ADDRESS = "FC:C5:72:23:A7:D8";
    
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothDevice mDevice;

    private BluetoothSocket mSocket = null;
    private BufferedReader mBufferedReader = null;

    private void openDeviceConnection(BluetoothDevice aDevice)
            throws IOException {
        InputStream aStream = null;
        InputStreamReader aReader = null;
        try {
            mSocket = aDevice
                    .createRfcommSocketToServiceRecord(getSerialPortUUID());
            try{
                Method createMethod = aDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] { int.class });
                mSocket = (BluetoothSocket)createMethod.invoke(aDevice, 1);
            }catch(Exception e){
                Log.e(TAG,"Method Exception thrown: " +e.toString());
            }
            mSocket.connect();
            aStream = mSocket.getInputStream();
            aReader = new InputStreamReader( aStream );
            mBufferedReader = new BufferedReader( aReader );
            Log.e(TAG,aReader.toString());
        } catch ( IOException e ) {
            Log.e( TAG, "Could not connect to device", e );
            close( mBufferedReader );
            close( aReader );
            close( aStream );
            close( mSocket );
            throw e;
        }
    }

    private void close(Closeable aConnectedObject) {
        if ( aConnectedObject == null ) return;
        try {
            aConnectedObject.close();
        } catch ( IOException e ) {
        }
        aConnectedObject = null;
    }

    private UUID getSerialPortUUID() {
        return UUID.fromString( UUID_SERIAL_PORT_PROFILE );
    };*/

}