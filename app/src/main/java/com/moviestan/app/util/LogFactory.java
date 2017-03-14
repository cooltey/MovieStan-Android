package com.moviestan.app.util;

import android.content.Context;
import android.support.compat.BuildConfig;
import android.util.Log;
import android.widget.Toast;


public class LogFactory{
	
	public static void set(String logName, Object logVal){
//		if (BuildConfig.DEBUG) {
			Log.d("====Custom Log==== " + logName, logVal + " ====Custom Log====");
//		}
	}

	public static void toast(Context context, String logName, Object logVal){
		if (BuildConfig.DEBUG) {
			Toast.makeText(context, logName + "=>" + logVal , Toast.LENGTH_LONG).show();
		}
	}
}
