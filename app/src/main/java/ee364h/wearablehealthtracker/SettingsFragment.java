package ee364h.wearablehealthtracker;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by Nadeem on 3/29/2015.
 */
public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    	Preference clear_data = (Preference)findPreference("pref_key_clear_data");
    	clear_data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
    		@Override
    		public boolean onPreferenceClick(Preference preference){
    			getActivity().deleteFile(((MainActivity)getActivity()).getDataFilename());
    			return true;
    		}
    	});
	}
}
