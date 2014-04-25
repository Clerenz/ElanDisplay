package de.clemensloos.elan.alert;


import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import de.clemensloos.elan.alert.R;


public class SettingsFragment extends Fragment {
	
	
	private EditText textViewPauseTime;
	private EditText textViewHost;
	private EditText textViewLinkSet;
	private EditText textViewLinkGet;
	private EditText textViewLinkSettings;
	private EditText textViewUser;
	private EditText textViewPass;
	
	
	private Vibrator myVib;
	
	private SharedPreferences sharedPrefs;
	
	private String addressSettings;
	private String user;
	private String pass;
	private long time;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View myView = inflater.inflate(R.layout.fragment_settings, container, false);
		myVib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		
		sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		
		textViewPauseTime = (EditText) myView.findViewById(R.id.edit_pause_time);
		textViewHost = (EditText) myView.findViewById(R.id.edit_host);
		textViewLinkSet = (EditText) myView.findViewById(R.id.edit_link_set);
		textViewLinkGet = (EditText) myView.findViewById(R.id.edit_link_get);
		textViewLinkSettings = (EditText) myView.findViewById(R.id.edit_link_settings);
		textViewUser = (EditText) myView.findViewById(R.id.edit_user);
		textViewPass = (EditText) myView.findViewById(R.id.edit_pass);

		refreshParameters();
    
		((Button) myView.findViewById( R.id.button_save )).setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				applyChanges();
			}
		});

		
		return myView;
		
	}


	protected void refreshParameters() {
		
		long defaultTime = Long.parseLong(getString(R.string.default_pause_time));
		time = sharedPrefs.getLong(getString(R.string.setting_pause_time), defaultTime);

		String defaultHost = getString(R.string.default_host);
		String host = sharedPrefs.getString(getString(R.string.setting_host), defaultHost);
		
		String defaultLinkSet = getString(R.string.default_set_link);
		String linkSet = sharedPrefs.getString(getString(R.string.setting_set_link), defaultLinkSet);
		
		String defaultLinkGet = getString(R.string.default_get_link);
		String linkGet = sharedPrefs.getString(getString(R.string.setting_get_link), defaultLinkGet);
		
		String defaulLinkSettings = getString(R.string.default_link_settings);
		String linkSettings = sharedPrefs.getString(getString(R.string.setting_link_settings), defaulLinkSettings);
		
		String defaultUser = getString(R.string.default_user);
		user = sharedPrefs.getString(getString(R.string.setting_user), defaultUser);
		
		String defaultPass = getString(R.string.default_pass);
		String clearPass = sharedPrefs.getString(getString(R.string.setting_pass), defaultPass);
		pass = Utils.md5Hash(clearPass);
		
	    
		textViewPauseTime.setText(time + "");
		textViewHost.setText(host);
		textViewLinkGet.setText(linkGet);
		textViewLinkSet.setText(linkSet);
		textViewLinkSettings.setText(linkSettings);
		textViewUser.setText(user);
		textViewPass.setText(clearPass);
		
	}
	
	
    private void applyChanges() {
		
		long defaultTime = Long.parseLong(getString(R.string.default_pause_time));
		long oldTime = sharedPrefs.getLong(getString(R.string.setting_pause_time), defaultTime);
		
		SharedPreferences.Editor editor = sharedPrefs.edit();
		
		time = Long.parseLong(textViewPauseTime.getText().toString());
		editor.putLong(getString(R.string.setting_pause_time), time);
		
		editor.putString(getString(R.string.setting_host), textViewHost.getText().toString());
		editor.putString(getString(R.string.setting_get_link), textViewLinkGet.getText().toString());
		editor.putString(getString(R.string.setting_set_link), textViewLinkSet.getText().toString());
		editor.putString(getString(R.string.setting_link_settings), textViewLinkSettings.getText().toString());
		editor.putString(getString(R.string.setting_user), textViewUser.getText().toString());
		editor.putString(getString(R.string.setting_pass), textViewPass.getText().toString());
		
		editor.commit();
	
		if ( oldTime != time ) {
			
			String defaultHost = getString(R.string.default_host);
			String host = sharedPrefs.getString(getString(R.string.setting_host), defaultHost);
	
			String defaultLink = getString(R.string.default_link_settings);
			String link = sharedPrefs.getString(getString(R.string.setting_link_settings), defaultLink);
			
			String defaultUser = getString(R.string.default_user);
			user = sharedPrefs.getString(getString(R.string.setting_user), defaultUser);
			
			String defaultPass = getString(R.string.default_pass);
			String clearPass = sharedPrefs.getString(getString(R.string.setting_pass), defaultPass);
			pass = Utils.md5Hash(clearPass);
			
			addressSettings = host + link;
			
			sendHttp();
		}
	}


	private void sendHttp() {
    	
    	if(!Utils.isNetworkAvailable(getActivity())) {
    		Utils.showDialog("Network not avaliable.", getActivity(), myVib);
    		return;
    	}
			
    	URL url = null;
    	try {
    		url = new URL(addressSettings);
    	} catch (Exception e) {
    		Utils.showDialog("Bad address.", getActivity(), myVib);
    		return;
    	}
    	
    	new Executor().execute(url);				
		
    }
    
    
    private class Executor extends AsyncTask<URL, Integer, boolean[]> {

		@Override
		protected boolean[] doInBackground(URL... urls) {
			
			
			try {
				URI uri = urls[0].toURI();
				
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(uri);
				
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user", user));
				nameValuePairs.add(new BasicNameValuePair("pass", pass));
				nameValuePairs.add(new BasicNameValuePair("time", ""+time));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				// Execute HTTP Post Request
				HttpResponse response = httpClient.execute(httpPost);
				int status = response.getStatusLine().getStatusCode();
				if( status < 200 || status >= 300 ) {
					return new boolean[] {false};
				}
				
			} catch (Exception e) {
				return new boolean[] {false};
			}
			return new boolean[] {true};
		}
		
		@Override
		protected void onPostExecute(boolean[] b) {
			if(b[0]) {
				
			} else {
				Utils.showDialog("Err", getActivity(), myVib);
			}
		}
    	
    } 
    
    

}
