package com.app.ivoke;

import com.app.ivoke.controllers.checking.CheckActivity;
import com.app.ivoke.controllers.checking.PlacesActivity;
import com.app.ivoke.controllers.login.FacebookLoginActivity;
import com.app.ivoke.controllers.login.LoginActivity;
import com.app.ivoke.controllers.main.MainActivity;
import com.app.ivoke.objects.UserIvoke;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.PlacePickerFragment;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;

public class Router {
	
	/*
	 * 
	 *   This class has the function to standardize the calls and sending parameters between Activity
	 * 
	 * */
	
	public static void gotoFacebookLogin(Activity pActivity)
	{
		pActivity.startActivity(new Intent(pActivity, FacebookLoginActivity.class));
		pActivity.finish();
	}
	
	public static void gotoIvokeLogin(Activity pActivity, Session pSession)
	{
		Intent i = new Intent(pActivity, LoginActivity.class);
		i.putExtra(LoginActivity.PE_FACEBOOK_SESSION, pSession);
		pActivity.startActivity(i);
		pActivity.finish();
	}
	
	public static void gotoChecking(Activity pActivity, Session pSession, UserIvoke pUser, GraphUser pFacebookUser)
	{
		Intent i = new Intent(pActivity, CheckActivity.class);
		i.putExtra(CheckActivity.PE_FACEBOOK_SESSION, pSession);
		i.putExtra(CheckActivity.PE_IVOKE_USER      , pUser);
		i.putExtra(CheckActivity.PE_FACEBOOK_USER_JSON   , pFacebookUser.getInnerJSONObject().toString());
		pActivity.startActivity(i);
	}
	
	public static void gotoPlaces(Activity pActivity, Location pLocalUsuario)
	{
		Intent i = new Intent(pActivity, PlacesActivity.class);
		i.putExtra(PlacePickerFragment.LOCATION_BUNDLE_KEY   , pLocalUsuario);
		i.putExtra(PlacePickerFragment.RADIUS_IN_METERS_BUNDLE_KEY, 500);
		pActivity.startActivityForResult(i, CheckActivity.RESULT_PLACE_ACT);
	}
	
	public static void gotoMain(Activity pActivity, UserIvoke pUser)
	{
		Intent i = new Intent(pActivity, MainActivity.class);
		i.putExtra(MainActivity.PE_USER_IVOKE, pUser);
		pActivity.startActivity(i);
	}
	
}
