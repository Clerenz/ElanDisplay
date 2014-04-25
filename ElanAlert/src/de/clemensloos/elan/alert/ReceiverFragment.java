package de.clemensloos.elan.alert;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.clemensloos.elan.alert.R;


public class ReceiverFragment extends Fragment {
	
	
	private TextView textViewNo;
	private TextView textViewTitle;
	private TextView textViewArtist;
	private Vibrator myVib;
	private ProgressBar progressBar;
	
	private long pause;
	private int steps = 100;
	private String addressGet;
	
	private boolean active = false;
	
	private Executor executor;
	
	private SharedPreferences sharedPrefs;
	
	
	
	public void restart() {
		
		long defaultPause = Long.parseLong(getString(R.string.default_pause_time));
		pause = sharedPrefs.getLong(getString(R.string.setting_pause_time), defaultPause);
		
		if(!active) {
			active = true;
			if (textViewNo.getText().equals("")) {
				textViewNo.setText("...");
			}
			getHttp();
		}
	}

	public void refreshParameter() {
		
		if(sharedPrefs == null) {
			return;
		}
		
		String defaultHost = getString(R.string.default_host);
		String defaultLink = getString(R.string.default_get_link);
		String host = sharedPrefs.getString(getString(R.string.setting_host), defaultHost);
		String link = sharedPrefs.getString(getString(R.string.setting_get_link), defaultLink);
		addressGet = host + link;
	}
	
	public void pause() {
		this.active = false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.active = false;
		if(executor != null) {
			executor.cancel(false);
		}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		
		refreshParameter();
		
		View myView = inflater.inflate(R.layout.fragment_receiver, container, false);
		
		myVib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		textViewNo = (TextView) myView.findViewById(R.id.textViewNo);
		textViewTitle = (TextView) myView.findViewById(R.id.textViewTitle);
		textViewArtist = (TextView) myView.findViewById(R.id.textViewArtist);
		progressBar = (ProgressBar) myView.findViewById(R.id.progressBar1);
		progressBar.setMax(steps);
		
		return myView;
		
	}
	
	
	
	

	
	
    private void getHttp() {
    	
    	if(!Utils.isNetworkAvailable(getActivity())) {
    		Utils.showDialog("Network not avaliable.", getActivity(), myVib);
    		return;
    	}
		
    	executor = new Executor();
    	
		executor.execute();
    	
    }
    
    
    private class Executor extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void...msg) {
			
			String s = "";
			
	    	try {
	    		
	    		while (active) {
	    			
	    			progressBar.setProgress(0);
	    			
	    			HttpClient httpClient = new DefaultHttpClient();
	    			HttpGet httpGet;
	    			synchronized (addressGet) {
	    				httpGet = new HttpGet(addressGet);					
	    			}
	    			
	    			HttpResponse response;
	    			HttpEntity entity;
	    			InputStream is;
	    			BufferedReader rd;

					response = httpClient.execute(httpGet);
					entity = response.getEntity();
					is = entity.getContent();
					rd = new BufferedReader(new InputStreamReader(is));
					s = rd.readLine();
					rd.close();
					httpGet.abort();
					publishProgress(s);
					
					for (int i = 1; i < steps + 1; i++) {
						try {
							Thread.sleep((pause*1000) / steps);
							if(!active)
								return "";
						} catch (InterruptedException e) {
							publishProgress("Err");
						}
						progressBar.setProgress(i);
					}
					
				}
	    		
	    	} catch (Exception e) {
	    		publishProgress("Err");
	    	}
			return "";
		}
		
		@Override
		protected void onProgressUpdate(String... msg) {
			super.onProgressUpdate(msg);
			
			String no = msg[0];
			String title = "";
			String artist = "";
			if (no.contains("%")) {
				title = no.substring(no.indexOf("%")+1);
				no = no.substring(0, no.indexOf("%"));
				if (title.contains("%")) {
					artist = title.substring(title.indexOf("%")+1);
					title = title.substring(0, title.indexOf("%"));
				}
			}
			
			if(!no.equals(textViewNo.getText())) {
				textViewNo.setTextColor(0xFFDD0000);
				textViewNo.setText(no);
				textViewTitle.setText(title);
				textViewArtist.setText(artist);
				myVib.vibrate(new long[] {0, 100, 100, 100}, -1);
			} else {
				textViewNo.setTextColor(0xFF000000);
			}
		}
    	
    }
    

}
