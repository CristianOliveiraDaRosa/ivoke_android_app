package com.app.ivoke.controllers.login;

import com.app.ivoke.Common;
import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.SettingsHelper;
import com.app.ivoke.libraries.GCMManager;
import com.app.ivoke.models.FacebookModel;
import com.app.ivoke.models.UserModel;
import com.app.ivoke.objects.DefaultWebCallback;
import com.app.ivoke.objects.UserIvoke;
import com.app.ivoke.objects.interfaces.IAsyncCallBack;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.model.GraphUser;

public class LoginActivity extends android.support.v4.app.FragmentActivity {
	
	public static String PE_FACEBOOK_SESSION = "LoginActivity.FacebookSession";
	
	static DebugHelper debug = new DebugHelper("LoginActivity");
	
	private FacebookModel faceModel = new FacebookModel();
	private UserModel userModel = new UserModel();
	
	private Session   fbSession;
	private GraphUser fbUser;
	private UserIvoke userIvoke;
	
	private IvokeServerCallback callback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		debug.method("onCreate").par("savedInstanceState", savedInstanceState);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		debug.log("before new IvokeServerCallback");
		callback = new IvokeServerCallback(this);
		
		getExtras();
		
		tryToDoLoginOnIvoke();
		
	}
	
	public void showError()
	{
		MessageHelper.errorAlert(this).setMessage(R.string.login_msg_error_user_not_found).showDialog();
	}
	
	public static class PlaceholderFragment extends Fragment {
     	@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.login_fragment, container, false);
			return rootView;
		}
	}
	
	
	private void getExtras()
	{
		debug.method("getExtras");
		Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	fbSession = (Session) extras.getSerializable(PE_FACEBOOK_SESSION);
        }
        
        debug.var("fbSession",fbSession);
		
	}
	
	private void tryToDoLoginOnIvoke()
	{
		debug.method("tryToDoLoginOnIvoke");
		if(fbSession.isOpened())
		{			
			faceModel.setSessaoAtiva(this, fbSession);
			this.getString(R.string.ws_url_facebook_profile_img);
			this.getString(R.string.ws_url);
			/*  Evita o NetworkOnMainThreadException fix it!!! */
			new Thread(new Runnable(){
				public void run() {
					faceModel.requestFacebookUser();
					fbUser = faceModel.getFacebookUser();
					debug.var("fbUser", fbUser);
					
					if(fbUser!=null)
					{
						try {
							userModel.asyncGetIvokeUser(fbUser.getId(), callback);
						} catch (Exception e) {
							debug.exception(e);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			 }).start();
			
		}
		else
		{
			MessageHelper
			.errorAlert(this)
			.setMessage(R.string.login_msg_session_error)
			.setButtonOk(new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					returnToFacebookLogin();
				}
			});
			
		}
	}
	
	
	private void returnToFacebookLogin()
	{
		Router.gotoFacebookLogin(this);
		finish();
		
	}
	
	private class IvokeServerCallback extends DefaultWebCallback
	{
		    LoginActivity activityCaller;
			Boolean inError;
			
			public IvokeServerCallback(LoginActivity pActivity)
			{
				activityCaller = pActivity;
			}
			
			@Override
			public void onCompleteTask(Object pResult) {
				debug._class(this).method("onCompleteTask").par("pResult", pResult);
				
				TextView lblProgress = (TextView) findViewById(R.id.login_lbl_processing);
				lblProgress.setText(R.string.login_lbl_process_done);
				
				if(inError)
				{
					MessageHelper.errorAlert(activityCaller)
					             .setMessage(R.string.def_error_msg_ws_server_not_responding)
					             .setButtonOk(new OnClickListener() {
														@Override
														public void onClick(DialogInterface dialog, int which) {
															activityCaller.finish();
														}
													});
				    
				}else if(userIvoke!= null)
				{
					
					Router.gotoChecking(activityCaller, fbSession, userIvoke, fbUser);
					activityCaller.finish();
				}
			}

			@Override
			public void onPreComplete(Object pResult) {
				debug.method("onPreComplete").par("pResult", pResult);
				try {
					
					if(pResult.toString() == "null")
					{
						userIvoke = userModel.create(fbUser.getName(), fbUser.getId());
					}
					else
					{
						userIvoke = UserIvoke.castJson(pResult.toString());
						userIvoke.setFacebookID(fbUser.getId());
						userIvoke.setName(fbUser.getName());
					}
					
					debug.log("TEST COMMON REG ID "+Common.getDeviceRegistrationId());
					
					SharedPreferences pref = SettingsHelper.getSharedPreference(activityCaller);
					userModel.asyncRegisterDevice(userIvoke, pref.getString(GCMManager.PREF_REGISTRATION_ID, null));
					
					
					debug.var("userIvoke", userIvoke);
					inError = false;
				} catch (Exception e) {
					inError = true;
					debug.exception(e);
				}
			}
	}
}