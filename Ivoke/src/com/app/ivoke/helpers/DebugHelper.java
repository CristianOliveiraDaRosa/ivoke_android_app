package com.app.ivoke.helpers;

import android.util.Log;

public class DebugHelper {
	
	String tag = "IVOKE_DEBUG";
	String prefix = "";
	boolean isDebugOn = true;
	
	public DebugHelper(){};
	public DebugHelper(String pTag)
	{
		tag = pTag;
	}
	
	public void log(String pLog)
	{
		if(isDebugOn)
		   Log.d(tag, prefix+": "+pLog);
	}
	
	public static DebugHelper build(String pTag)
	{
		return new DebugHelper(pTag);
	}
	
	public void setDebugging(boolean pDebugIsOn)
	{
		isDebugOn = pDebugIsOn;
	}
	
	public DebugHelper setPrefix(String pPrefix)
	{
		prefix = pPrefix;
		
		this.log(" start ");
		return this;
	}
}