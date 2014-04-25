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
import android.widget.TextView;
import de.clemensloos.elan.alert.R;


public class SenderFragment extends Fragment {
	
	
	private static final String ERROR_STRING = "Err";
	
	private TextView textViewLast;
	private TextView textViewAct;
	
	private String last = "";
	private String act = "";
	
	private String addressSet;
	private String pass;
	private String user;
	
	private Vibrator myVib;
	
	
	private SharedPreferences sharedPrefs;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		
		refreshParameter();
		
		View myView = inflater.inflate(R.layout.fragment_sender, container, false);
		
		myVib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
	    
		textViewLast = (TextView) myView.findViewById(R.id.textView1);
		textViewAct  = (TextView) myView.findViewById(R.id.textView2);
    
		((Button) myView.findViewById( R.id.button1 )).setOnClickListener( new MyOnClickListener(1) );
		((Button) myView.findViewById( R.id.button2 )).setOnClickListener( new MyOnClickListener(2) );
		((Button) myView.findViewById( R.id.button3 )).setOnClickListener( new MyOnClickListener(3) );
		((Button) myView.findViewById( R.id.button4 )).setOnClickListener( new MyOnClickListener(4) );
		((Button) myView.findViewById( R.id.button5 )).setOnClickListener( new MyOnClickListener(5) );
		((Button) myView.findViewById( R.id.button6 )).setOnClickListener( new MyOnClickListener(6) );
		((Button) myView.findViewById( R.id.button7 )).setOnClickListener( new MyOnClickListener(7) );
		((Button) myView.findViewById( R.id.button8 )).setOnClickListener( new MyOnClickListener(8) );
		((Button) myView.findViewById( R.id.button9 )).setOnClickListener( new MyOnClickListener(9) );
		((Button) myView.findViewById( R.id.button0 )).setOnClickListener( new MyOnClickListener(0) );
		
		((Button) myView.findViewById( R.id.buttonC )).setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				myVib.vibrate(25);
				act = "";
				textViewAct.setText(act);
			}
		});
		((Button) myView.findViewById( R.id.buttonS )).setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				myVib.vibrate(25);
				sendHttp();
			}
		});
		
		return myView;
		
	}
	
	
	public void refreshParameter() {
		
		if(sharedPrefs == null) {
			return;
		}
		
		String defaultUser = getString(R.string.default_user);
		user = sharedPrefs.getString(getString(R.string.setting_user), defaultUser);
		
		String defaultPass = getString(R.string.default_pass);
		String clearPass = sharedPrefs.getString(getString(R.string.setting_pass), defaultPass);
		pass = Utils.md5Hash(clearPass);
		
		String defaultHost = getString(R.string.default_host);
		String host = sharedPrefs.getString(getString(R.string.setting_host), defaultHost);

		String defaultLink = getString(R.string.default_set_link);
		String link = sharedPrefs.getString(getString(R.string.setting_set_link), defaultLink);
		
		addressSet = host + link;
		
		act = "";
		
		if(textViewLast != null) {
			textViewLast.setText(last);
			textViewAct.setText(act);
		}
		
	}
	
	
    private void sendHttp() {
    	
    	if(!Utils.isNetworkAvailable(getActivity())) {
    		Utils.showDialog("Network not avaliable.", getActivity(), myVib);
    		return;
    	}
    	
    	if (!act.equals("")) {
    		
			textViewLast.setText("");
			
			URL url = null;
			try {
				url = new URL(addressSet);
			} catch (Exception e) {
				Utils.showDialog("Bad address.", getActivity(), myVib);
				return;
			}
			
			new Executor().execute(url);
						
		}
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
				nameValuePairs.add(new BasicNameValuePair("song", act));
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
				last = act;
				act = "";
				textViewLast.setText(last);
				textViewAct.setText(act);
			} else {
				last = ERROR_STRING;
				textViewLast.setText(last);
			}
		}
    	
    }
    
    
    
    class MyOnClickListener implements OnClickListener {
    	
    	private int num;
    	
    	public MyOnClickListener(int num) {
			super();
			this.num = num;
		}

		@Override
		public void onClick(View v) {
			myVib.vibrate(25);
			if (act.length() > 2) {
				act = act.substring(1);
			}
			act += num;
			textViewAct.setText(act);
		}
    	
    }
    

}
