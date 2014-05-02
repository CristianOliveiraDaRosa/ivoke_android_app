package com.app.ivoke;

import com.app.ivoke.controllers.checking.CheckActivity;
import com.app.ivoke.controllers.login.FacebookLoginActivity;
import com.app.ivoke.controllers.login.LoginActivity;
import com.app.ivoke.objects.UserIvoke;
import com.facebook.Session;

import android.app.Activity;
import android.content.Intent;

public class Routes {
	/*
	 *   This class is to standardize the calls and sending parameters between activities
	 * 
	 * */
	
	public static String PE_FACEBOOK_SESSION = "FacebookLoginSession";
	
	public static void gotoFacebookLogin(Activity pActivity)
	{
		pActivity.startActivity(new Intent(pActivity, FacebookLoginActivity.class));
	}
	
	public static void gotoIvokeLogin(Activity pActivity, Session pSession)
	{
		Intent i = new Intent(pActivity, LoginActivity.class);
		i.putExtra(LoginActivity.PE_FACEBOOK_SESSION, pSession);
		pActivity.startActivity(i);
	}
	
	public static void gotoChecking(Activity pActivity, Session pSession, UserIvoke pUser)
	{
		Intent i = new Intent(pActivity, CheckActivity.class);
		i.putExtra(CheckActivity.PE_FACEBOOK_SESSION, pSession);
		i.putExtra(CheckActivity.PE_USER_IVOKE      , pUser);
		pActivity.startActivity(i);
	}
	
}
