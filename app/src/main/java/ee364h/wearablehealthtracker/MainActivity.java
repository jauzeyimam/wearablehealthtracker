package ee364h.wearablehealthtracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

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
}