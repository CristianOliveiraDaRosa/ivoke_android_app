package com.app.ivoke.controllers.login;

import org.json.JSONException;

import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.controllers.checking.CheckActivity;
import com.app.ivoke.controllers.main.MainActivity;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.ViewHelper;
import com.app.ivoke.helpers.WebHelper.NetworkException;
import com.app.ivoke.helpers.WebHelper.ServerException;
import com.app.ivoke.models.FacebookModel;
import com.app.ivoke.models.UserModel;
import com.app.ivoke.objects.UserIvoke;
import com.app.ivoke.objects.interfaces.IAsyncCallBack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.app.DownloadManager.Request;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.facebook.SessionState;

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
			MessageHelper.infoAlert(this).setMessage(R.string.login_msg_session_error, "facebook").showDialog();
			returnToFacebookLogin();
		}
	}
	
	private void returnToFacebookLogin()
	{
		Router.gotoFacebookLogin(this);
		finish();
	}
	
	private class IvokeServerCallback implements IAsyncCallBack
	{
			Activity activityCaller;
			
			public IvokeServerCallback(Activity pActivity)
			{
				activityCaller = pActivity;
			}
		
			@Override
			public void onProgress(int pPercent, Object pObject) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCompleteTask(Object pResult) {
				debug._class(this).method("onCompleteTask").par("pResult", pResult);
				
				TextView lblProgress = (TextView) findViewById(R.id.login_lbl_processing);
				lblProgress.setText(R.string.process_done);
				
				try {
					
					userIvoke = UserIvoke.castJson(pResult.toString());
					userIvoke.setFacebookID(fbUser.getId());
					userIvoke.setName(fbUser.getName());
					
					debug.var("userIvoke", userIvoke);
				} catch (Exception e) {
					debug.exception(e);
					MessageHelper.errorAlert(activityCaller)
					             .setMessage(R.string.login_msg_error_user_not_found).showDialog();
				}
				
				if(userIvoke!= null)
				{
					Router.gotoChecking(activityCaller, fbSession, userIvoke, fbUser);
					activityCaller.finish();
				}
			}
	}
}