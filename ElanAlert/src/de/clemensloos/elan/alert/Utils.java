package de.clemensloos.elan.alert;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;

public class Utils {

	
	
    public static String md5Hash(String s) {
    	MessageDigest m = null;

    	try {
            m = MessageDigest.getInstance("MD5");
    	} catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
    	}

    	m.update(s.getBytes(),0,s.length());
    	String hash = new BigInteger(1, m.digest()).toString(16);
    	return hash;

    }
    
    
    
    public static void showDialog(String message, FragmentActivity activity, Vibrator myVib) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                
		           }
		       })
		       ;
		AlertDialog errorDialog = builder.create();
		if (myVib != null) {
			myVib.vibrate(new long[] {0, 100, 100, 100}, -1);
		}
		errorDialog.show();
    }
    
    
    
	public static boolean isNetworkAvailable(FragmentActivity activity) {
	    ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
}
