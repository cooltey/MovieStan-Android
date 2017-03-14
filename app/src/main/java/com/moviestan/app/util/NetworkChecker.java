package com.moviestan.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChecker{
	
	private Context mContext;
	private Boolean mStatus = false;
	
	public NetworkChecker(Context context){
		
		mContext = context;
		
		boolean result;
    	ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
    	NetworkInfo info = connManager.getActiveNetworkInfo();
    	if (info == null || !info.isConnected())
    	{
    		result = false;
    	}else{
    		if (!info.isAvailable())
    		{
    			result = false;
    		}else{
    			result = true;
    		}
    	}
    	
    	mStatus = result;

	}
		
	
	public boolean getStatus(){
		return mStatus;
	}
}
