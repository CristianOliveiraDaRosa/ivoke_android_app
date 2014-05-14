package com.app.ivoke.objects.defaults;

import android.app.Activity;
import android.content.DialogInterface;


public abstract class DefaultOkListener implements android.content.DialogInterface.OnClickListener {
	
	Activity activity;
	
	public DefaultOkListener(Activity pActivity)
	{
		activity = pActivity;
	}
	
	public void finishActivity()
	{
		activity.finish();
	}
	
	public Activity getActivity()
	{
		return activity;
	}
}
