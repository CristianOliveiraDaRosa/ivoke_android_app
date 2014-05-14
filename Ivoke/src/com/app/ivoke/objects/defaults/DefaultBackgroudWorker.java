package com.app.ivoke.objects.defaults;

import android.app.Activity;
import android.os.AsyncTask;

public abstract class DefaultBackgroudWorker extends AsyncTask<Object, Object, Object> {
	
	Activity  context;
	Exception exception;
	
	public DefaultBackgroudWorker(Activity pActivity)
	{
		context = pActivity;
	}
	
	public Activity getActivityCaller()
	{
		return context;
	}
	
	public void setException(Exception pEx)
	{
		exception = pEx;
	}
	
	public Exception getException()
	{
		return exception;
	}
	
	public boolean inError()
	{
		return exception!=null;
	}

}