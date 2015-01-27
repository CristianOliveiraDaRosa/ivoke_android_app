package com.app.ivoke.helpers;

import java.util.List;

import android.util.Log;

public class DebugHelper {
	
	String className = "IVOKE_DEBUG";
	String methodName = "";
	boolean isDebugOn = true;
	
	public DebugHelper(){};
	public DebugHelper(String pClassName)
	{
		className = pClassName;
	}
	
	public void log(String pLog)
	{
		if(isDebugOn)
		   Log.d(className+"."+methodName, pLog);
	}
	
	public DebugHelper log(Object pLog)
	{
		if(isDebugOn)
		   Log.d(className+"."+methodName, pLog.toString());
		
		return this;
	}
	
	public DebugHelper par(String pName,Object pLog)
	{
		if(isDebugOn)
		{
			if(pLog == null)
				Log.d(className+"."+methodName, "PARAM: "+pName+"= NULL");
			else
				Log.d(className+"."+methodName, "PARAM: "+pName+"="+pLog.toString());
		}
		
		return this;
	}
	
	public DebugHelper var(String pName,Object pLog)
	{
		if(isDebugOn)
		{
			if(pLog == null)
				Log.d(className+"."+methodName, "VAR: "+pName+"= NULL");
			else
				Log.d(className+"."+methodName, "VAR: "+pName+"="+pLog.toString());
		}
		
		return this;
	}
	
	public DebugHelper var(String pName,List<?> pLog)
	{
		if(isDebugOn)
		{
			if(pLog == null)
				Log.d(className+"."+methodName, "VAR: "+pName+"= NULL");
			else
				Log.d(className+"."+methodName, "VAR: "+pName+"="+pLog.toString()+" size: "+pLog.size());
		}
		
		return this;
	}
	
	public static DebugHelper build(String pTag)
	{
		return new DebugHelper(pTag);
	}
	
	public void setDebugging(boolean pDebugIsOn)
	{
		isDebugOn = pDebugIsOn;
	}
	
	public DebugHelper method(String pPrefix)
	{
		methodName = pPrefix;
		
		this.log(" start ");
		return this;
	}
	
	public DebugHelper setClass(Object pClassName) {
		className = pClassName.getClass().getSimpleName();
		return this;
	}
	
	public DebugHelper _class(Object pClassName) {
		String className = pClassName.getClass().getSimpleName();
		return new DebugHelper(className);
	}
	
	public DebugHelper _class(String pClassName) {
		className = pClassName;
		return this;
	}
	
	public void exception(Exception e) {
		this.log("EXCEPTION: "+e.getMessage());	
	}
}
