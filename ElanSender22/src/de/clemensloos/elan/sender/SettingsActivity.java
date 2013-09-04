package de.clemensloos.elan.sender;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;



public class SettingsActivity extends PreferenceActivity {


	Preference portPreference;
	SharedPreferences sharedPrefs;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
        
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String port = sharedPrefs.getString(
        		getResources().getString(R.string.pref_port_key),
        		getResources().getString(R.string.pref_port_default));
        
        portPreference = findPreference(getResources().getString(R.string.pref_port_key));
        portPreference.setSummary(getString(R.string.pref_port_desc) + " " + port);
        
        portPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
			    if( !newValue.toString().equals("")  &&  newValue.toString().matches("\\d*") ) {
			        int port = Integer.parseInt(newValue.toString());
			        if( port < 49152 || port > 65535) {
			        	Toast.makeText(SettingsActivity.this, R.string.pref_toast_invalid_port, Toast.LENGTH_LONG).show();
			        	return false;
			        }
			        portPreference.setSummary(getString(R.string.pref_port_desc) + " " + newValue.toString());
			    	return true;
			    }
			    else {
			        Toast.makeText(SettingsActivity.this, R.string.pref_toast_invalid_port, Toast.LENGTH_LONG).show();
			        return false;
			    }
			}
		});
        
    }


//	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	private void setupActionBar() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			// Show the Up button in the action bar.
//			getActionBar().setDisplayHomeAsUpEnabled(true);
//		}
//	}

	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		
////		switch (item.getItemId()) {
////		case android.R.id.home:
////			NavUtils.navigateUpFromSameTask(this);
////			return true;
////		}
//		return super.onOptionsItemSelected(item);
//	}

}
