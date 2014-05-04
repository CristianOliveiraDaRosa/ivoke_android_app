package com.app.ivoke.models;

import com.app.ivoke.Router;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfiguracaoModel {
	
	private static SharedPreferences prefs;
	
	private static String APP_PACKAGE         = "com.app.ivoke";
	private static String MURAL_POST_DISTANCE = APP_PACKAGE+".mural_post_distance";
	private static String BLOCK_ADVERTMENT    = APP_PACKAGE+".block_advertment";
	
	public ConfiguracaoModel()
	{
		prefs = 
		  Router.currentContext.getSharedPreferences( APP_PACKAGE
				  									, Context.MODE_PRIVATE);
	}
	
	public ConfiguracaoModel(Context pContext)
	{
		prefs = pContext.getSharedPreferences( APP_PACKAGE
				  						     , Context.MODE_PRIVATE);
	}
	
	public void setMuralPostDistance(float pDistance)
	{
		prefs.edit().putFloat(MURAL_POST_DISTANCE, pDistance).commit();
	}
	
	public float getMuralPostDistance()
	{
		return prefs.getFloat(MURAL_POST_DISTANCE, 100);
	}
		
	public void setBlockAdvertments(boolean pBolean)
	{
		prefs.edit().putBoolean(BLOCK_ADVERTMENT, pBolean).commit();
	}
	
	public boolean getBlockAdvertments()
	{
		return prefs.getBoolean(BLOCK_ADVERTMENT, false);
	}
}
