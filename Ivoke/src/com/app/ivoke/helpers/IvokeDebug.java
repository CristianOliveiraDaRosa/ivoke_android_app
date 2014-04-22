package com.app.ivoke.helpers;

import android.util.Log;

public class IvokeDebug {
	
	String tag = "IVOKE_DEBUG";
	
	public IvokeDebug(){};
	public IvokeDebug(String pTag)
	{
		tag = pTag;
	}
	
	public void log(String pLog)
	{
		Log.d(tag, pLog);
	}
}
