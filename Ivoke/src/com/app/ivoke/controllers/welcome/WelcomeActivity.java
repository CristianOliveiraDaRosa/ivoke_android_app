package com.app.ivoke.controllers.welcome;

import com.app.ivoke.Common;
import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.DeviceHelper;
import com.app.ivoke.helpers.LocationHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.libraries.GCMManager;
import com.app.ivoke.objects.defaults.DefaultOkListener;
import com.app.ivoke.objects.defaults.DefaultWebCallback;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class WelcomeActivity extends Activity {
	 DebugHelper dbg = new DebugHelper("WelcomeActivity");
	 GCMManager gcmManager = new GCMManager();
	
	 RegisterDeviceCallback registerCallback;
	 
	 MessageHelper.MessageAlert poMsgAlert;
	 
	@SuppressWarnings("null")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		dbg.method("onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_activity);
		
		Router.current(this);
		
	    if (verifyApplicationRequisites()) {
	    	String regid = gcmManager.getRegistrationId(this);
            dbg.var("regid", regid);
            if (regid.isEmpty()) {
            	registerCallback = new RegisterDeviceCallback(this);
            	gcmManager.registerInBackground(registerCallback);
            }else
            {
            	Router.gotoFacebookLogin(this);
            	this.finish();
            }
	    }
	    else
	    {
	    	poMsgAlert.showDialog();
	    }
	}
	
	private boolean verifyApplicationRequisites()
	{
		if(!DeviceHelper.checkGooglePlayServices(this))
		{
			poMsgAlert = MessageHelper.errorAlert(this)
					   	             .setMessage(R.string.def_error_msg_google_play_not_available)
					   	             .setButtonOk(new DefaultOkListener(this) {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												finish();
											}
										});
			
		}
		else if (!DeviceHelper.hasInternetConnection())
		{
				poMsgAlert = MessageHelper.errorAlert(this)
		   	             .setMessage(R.string.def_error_msg_whitout_internet_connection)
		   	             .setButtonOk(new DefaultOkListener(this) {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							});	
		}
		else if(!DeviceHelper.hasGpsOrInternetLocalization(this))
		{
			
			poMsgAlert =   MessageHelper.errorAlert(this)
										.setMessage(R.string.check_msg_info_on_get_user_local)
										.setButtonYesNo(new OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												
												if(which == MessageHelper.DIALOG_RESULT_YES)
												{
													//Intent i = LocationHelper.getIntentGpsSettings();
													//startActivity(i);
													startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
													finish();
												}
												else
												{
													finish();
												}
												
											}
										});

		}
		
		return poMsgAlert == null;
	}
	
	class RegisterDeviceCallback extends DefaultWebCallback
	{
		Activity actCaller;
		
		public RegisterDeviceCallback(Activity pActivity)
		{
			actCaller = pActivity;
		}
		
		@Override
		public void onCompleteTask(Object pResult) {
			super.onCompleteTask(pResult);
			
			if(pResult!=null)
			{
				Router.gotoFacebookLogin(actCaller);
				actCaller.finish();
			}
		}
	}

}