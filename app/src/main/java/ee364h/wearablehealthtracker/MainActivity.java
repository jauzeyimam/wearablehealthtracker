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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends Activity
        implements GraphFragment.OnFragmentInteractionListener,
        HomePageFragment.OnGraphSelectedListener,
        HomePageFragment.OnSettingsSelectedListener, HomePageFragment.OnBluetoothSelectedListener, 
        SensorEventListener {

    private final static String HOME_FRAGMENT_TAG = "HomePageFragment";
    private final static String GRAPH_FRAGMENT_TAG = "GraphFragment";
    private final static String BLUETOOTH_FRAGMENT_TAG = "DeviceScanFragment";
    private final static String BLUETOOTH_DEVICE_FRAGMENT_TAG = "DeviceControlFragment";
    private final static String SETTINGS_FRAGMENT_TAG = "SettingsFragment";
    private static final int REQUEST_ENABLE_BT = 1;

    private final static String TAG = MainActivity.class.getSimpleName();
    
    private Date date = new Date();
    private Bundle currentData;
    private float stepCount;
    private boolean connectionStatus;
    private SensorManager sensorManager;
    static PackageManager packageManager;
    private int pedometerToastCount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Started");
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            String filePath = getFilesDir() + "/" + getDataFilename();
            File mFile = new File( filePath );
            currentData = new Bundle();
            if(mFile.exists())
            {
                String lastLine = tail(mFile);
                String[] values = lastLine.split(" ");
                currentData.putLong("time", Long.valueOf(values[0]));
                currentData.putInt("pulse", (int) GraphType.PULSE.getValueFromStringArray(values));
                currentData.putInt("bloodox",(int) GraphType.BLOODOX.getValueFromStringArray(values));
                currentData.putDouble("temperature",(double) GraphType.TEMPERATURE.getValueFromStringArray(values));
                currentData.putInt("battery", Integer.valueOf(values[4]));
                currentData.putString("values",lastLine);
            } else{
                currentData.putLong("time", 0);
                currentData.putInt("pulse", 0);
                currentData.putInt("bloodox",0);
                currentData.putDouble("temperature",0);
                currentData.putInt("battery",0);
                currentData.putString("values","");
            }

            stepCount = 0;
            pedometerToastCount = 0;
            connectionStatus = false;

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


        Log.d(TAG,"onCreate Finished");

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        packageManager = getPackageManager();

        if(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)){
            Toast.makeText(this, "Pedometer Feature is available.",Toast.LENGTH_LONG).show();
        }

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else if(pedometerToastCount == 0){
            Toast.makeText(this, "Pedometer not available.", Toast.LENGTH_LONG).show();
            pedometerToastCount++;
        }
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
        Log.d(TAG, "BLUETOOTH SELECTED");

        BluetoothAdapter mBluetoothAdapter;
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }else{
            // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
            // BluetoothAdapter through BluetoothManager.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();

            // Checks if Bluetooth is supported on the device.
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                return;
            }

            // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
            // fire an intent to display a dialog asking the user to grant permission to enable it.
            if (!mBluetoothAdapter.isEnabled()) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
            
            toggleBluetoothFragment();
        }


    };

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    public void toggleBluetoothFragment(){

        boolean bluetoothFragmentExists = getFragmentManager().findFragmentByTag(BLUETOOTH_FRAGMENT_TAG) != null;

        if(!bluetoothFragmentExists){

            DeviceControlFragment bluetoothFragment = new DeviceControlFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(bluetoothFragment, BLUETOOTH_FRAGMENT_TAG);


            // Commit the transaction
            transaction.commit();
            Toast.makeText(this, "Attempting to connect to UART", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"BLUETOOTH FRAGMENT INITIATED");
        } else{
            Toast.makeText(this, "Disconnected from UART", Toast.LENGTH_SHORT).show();

            DeviceControlFragment bluetoothFragment = (DeviceControlFragment) getFragmentManager().findFragmentByTag(BLUETOOTH_FRAGMENT_TAG);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(bluetoothFragment);

            // Commit the transaction
            transaction.commit();
            Log.d(TAG,"BLUETOOTH FRAGMENT REMOVED");
        }
    }
    
/*    public void onBLEDeviceSelected(String name, String address){
        Log.d(TAG, "DEVICE SELECTED");
        // New SettingsFragment
        DeviceControlFragment deviceControlFragment = new DeviceControlFragment();
        Bundle args = new Bundle();
        args.putString(DeviceControlFragment.EXTRAS_DEVICE_NAME, name);
        args.putString(DeviceControlFragment.EXTRAS_DEVICE_ADDRESS, address);
        deviceControlFragment.setArguments(args);

        //Replace HomePageFragment with GraphFragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, deviceControlFragment, BLUETOOTH_DEVICE_FRAGMENT_TAG);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        Log.d(TAG,"CONTROL FRAGMENT INITIATED");
    };*/


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
        return GraphType.PULSE.getFilename();
    }
    public String getPedometerFilename(){
        return GraphType.PEDOMETER.getFilename();
    }
    public float getStepCount(){
        return stepCount;
    }
    public Bundle getCurrentData(){
        return currentData;
    }
    public Boolean getConnectionStatus(){
        return connectionStatus;
    }

    public void updateCurrentData(Bundle bundle){
        this.currentData = bundle;
        HomePageFragment home = (HomePageFragment) getFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG);
        home.updateMeasurements();
    }
    public void updateBluetoothConnectionStatus(boolean connected){
        this.connectionStatus = connected;
        HomePageFragment home = (HomePageFragment) getFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG);
        home.updateBluetoothButton();
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        stepCount =  event.values[0];
        long time = date.getTime();
        String data = String.format("" + time + " %.0f", stepCount);

        String filename = getPedometerFilename();
        FileOutputStream outputStream;
        try {
          outputStream = openFileOutput(filename, Context.MODE_APPEND);
          outputStream.write(data.getBytes());
          outputStream.write("\n".getBytes());
          outputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }

        HomePageFragment home = (HomePageFragment) getFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG);
        home.updateStepCount();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public String tail( File file ) {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new RandomAccessFile( file, "r" );
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();

            for(long filePointer = fileLength; filePointer != -1; filePointer--){
                fileHandler.seek( filePointer );
                int readByte = fileHandler.readByte();

                if( readByte == 0xA ) {
                    if( filePointer == fileLength ) {
                        continue;
                    }
                    break;

                } else if( readByte == 0xD ) {
                    if( filePointer == fileLength - 1 ) {
                        continue;
                    }
                    break;
                }

                sb.append( ( char ) readByte );
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch( java.io.FileNotFoundException e ) {
            e.printStackTrace();
            return null;
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileHandler != null )
                try {
                    fileHandler.close();
                } catch (IOException e) {
                    /* ignore */
                }
        }
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