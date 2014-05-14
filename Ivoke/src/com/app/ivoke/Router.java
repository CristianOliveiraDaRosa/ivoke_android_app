package com.app.ivoke;

import com.app.ivoke.controllers.checking.CheckActivity;
import com.app.ivoke.controllers.checking.PlacesActivity;
import com.app.ivoke.controllers.login.FacebookLoginActivity;
import com.app.ivoke.controllers.login.LoginActivity;
import com.app.ivoke.controllers.main.MainActivity;
import com.app.ivoke.controllers.setting.SettingsActivity;
import com.app.ivoke.objects.UserIvoke;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.PlacePickerFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

public class Router {
	
	/*
	 * 
	 *   This class has the function to standardize the calls and sending parameters between Activity
	 * 
	 * 	 Using methods start and startForResult you keep refreshed the currentContext!
	 * 
	 * */
	
	public static Activity currentContext;
	
	public static void finishCurrentContext()
	{
		currentContext.finish();
	}
	
	public static void current(Activity pActivity)
	{
		currentContext = pActivity;
	}
	
	public static void gotoFacebookLogin(Activity pActivity)
	{
		start(pActivity, new Intent(pActivity, FacebookLoginActivity.class));
		pActivity.finish();
	}
	
	public static void gotoIvokeLogin(Activity pActivity, Session pSession, GraphUser pFbUser)
	{
		Intent i = new Intent(pActivity, LoginActivity.class);
		i.putExtra(LoginActivity.PE_FACEBOOK_SESSION   , pSession);
		i.putExtra(LoginActivity.PE_FACEBOOK_USER_JSON , pFbUser.getInnerJSONObject().toString());
		start(pActivity, i);
		pActivity.finish();
	}
	
	public static void gotoChecking(Activity pActivity, Session pSession, UserIvoke pUser, GraphUser pFacebookUser)
	{
		Intent i = new Intent(pActivity, CheckActivity.class);
		i.putExtra(CheckActivity.PE_FACEBOOK_SESSION     , pSession);
		i.putExtra(CheckActivity.PE_IVOKE_USER           , pUser);
		i.putExtra(CheckActivity.PE_FACEBOOK_USER_JSON   , pFacebookUser.getInnerJSONObject().toString());
		
		start(pActivity,i);
	}
	
	public static void gotoPlaces(Activity pActivity, Location pLocalUsuario)
	{
		Intent i = new Intent(pActivity, PlacesActivity.class);
		i.putExtra(PlacePickerFragment.LOCATION_BUNDLE_KEY   , pLocalUsuario);
		i.putExtra(PlacePickerFragment.RADIUS_IN_METERS_BUNDLE_KEY, 500);
		
		startForResult(pActivity, i, CheckActivity.RESULT_PLACE_ACT);
	}
	
	public static void gotoMain(Activity pActivity, UserIvoke pUser)
	{
		Intent i = new Intent(pActivity, MainActivity.class);
		i.putExtra(MainActivity.PE_USER_IVOKE, pUser);
		
		start(pActivity, i);
	}
	
	public static void gotoSettings(Activity pActivity)
	{
		Intent i = new Intent(pActivity, SettingsActivity.class);
		start(pActivity, i);
	}
	
	private static void start(Activity pActivity, Intent pIntent)
	{
		currentContext = pActivity;
		pActivity.startActivity(pIntent);
	}
	
	private static void startForResult(Activity pActivity, Intent pIntent, int requestCode)
	{
		currentContext = pActivity;
		pActivity.startActivityForResult(pIntent, requestCode);
		
	}
	
}
