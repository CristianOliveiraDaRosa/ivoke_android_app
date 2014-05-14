package com.app.ivoke.helpers;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GoogleServicesHelper {
	
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	/*
	 *  Return boolean
	 *  Verify if GooglePlay is supported.
	 *  
	 *  Created: Cristian Oliveira
	 */
	public static boolean checkGooglePlayServices(Activity pActivityCaller) {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(pActivityCaller);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, pActivityCaller,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	        	pActivityCaller.finish();
	        }
	        return false;
	    }
	    return true;
	}
}