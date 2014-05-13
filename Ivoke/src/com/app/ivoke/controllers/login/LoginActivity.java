package com.app.ivoke.controllers.login;

import com.app.ivoke.Common;
import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.models.FacebookModel;
import com.app.ivoke.models.UserModel;
import com.app.ivoke.objects.UserIvoke;
import com.app.ivoke.objects.defaults.DefaultBackgroudWorker;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
	
	private LoginBackground loginBackground;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		debug.method("onCreate").par("savedInstanceState", savedInstanceState);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
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
			
			try {
				loginBackground = new LoginBackground(this);
				loginBackground.execute();
			} catch (Throwable t){
				showError();
			}
			
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
	
	private void goToChecking() {
		
		Router.gotoChecking(this, fbSession, userIvoke, fbUser);
		this.finish();
		
	}
	
	
	private class LoginBackground extends DefaultBackgroudWorker
	{
		LoginActivity loginActivity;
		Exception  exception;
		
		public LoginBackground(Activity pActivity) {
			super(pActivity);
			loginActivity = (LoginActivity) pActivity;
		}

		@Override
		protected Object doInBackground(Object... params) {
			debug.method("doInBackground");
			try {
				
				faceModel.requestFacebookUser();
				fbUser = faceModel.getFacebookUser();
				debug.var("fbUser", fbUser);
				
				if(fbUser!=null)
				{
					debug.log("BUSCA USER");
					try {
						userIvoke = userModel.requestIvokeUser(fbUser.getId());
						
						if(userIvoke == null)
						{
							userIvoke = userModel.createOnServer(fbUser.getName(), fbUser.getId());
						}
						
						userModel.asyncRegisterDevice(userIvoke, Common.getDeviceRegistrationId());
						
					} catch (Exception e) {
						debug.exception(e);
						exception = e;
					}
				}
				
		    } catch (Exception e) {
		    	exception = e;
		    	return false;
		    }
			return true;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			
			if(inError())
			{
				MessageHelper.errorAlert(loginActivity)
	             .setMessage(R.string.def_error_msg_ws_server_not_responding)
	             .setButtonOk(new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											loginActivity.finish();
										}
									});
			}
			else if(userIvoke!=null)
			{
				loginActivity.goToChecking();
			}
			else
			{
				MessageHelper.errorAlert(loginActivity).setMessage(R.string.def_msg_ask_try_agai).showDialog();
			}
		}
	}
}