package ee364h.wearablehealthtracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
        implements GraphFragment.OnFragmentInteractionListener,
        HomePageFragment.OnGraphSelectedListener,
        HomePageFragment.OnSettingsSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            HomePageFragment home = new HomePageFragment();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, home)
                    .commit();
        }
        Log.d("MainActivity","onCreate Finished");
    }


    /*@Override
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public void onGraphSelected(GraphType graphType){
        // New GraphFragment of the correct type
        GraphFragment graphFragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(GraphFragment.ARG_GRAPHTYPE,graphType.name());
        graphFragment.setArguments(args);

        //Replace HomePageFragment with GraphFragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, graphFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    };

    public void onSettingsSelected(){
        Log.d("MainActivity","SETTINGS SELECTED");
        // New SettingsFragment
        SettingsFragment settingsFragment = new SettingsFragment();

        //Replace HomePageFragment with GraphFragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, settingsFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        Log.d("MainActivity","SETTINGS FRAGMENT INITIATED");
    };

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void onFragmentInteraction(Uri uri){};

    /*Bluetooth Functions and Variables*/

    /*Initialize Bluetooth Adapter*/
    final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    private BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
    private BluetoothGatt mBluetoothGatt;

    /*Temporary Bluetooth Function, find a better place for this....*/
    public void tempBluetoothFunction(){ 

        /*Check for BLE compatibility*/
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            /*If bluetooth is not enabled, enable it*/
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        }


    }
}