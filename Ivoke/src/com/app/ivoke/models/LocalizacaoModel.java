package com.app.ivoke.models;

import com.app.ivoke.helpers.GPSService;

public class LocalizacaoModel extends WebServer {
	   
	   GPSService gpsService;
	   
	   public void setGpsService(GPSService pGpsService)
	   {
		   gpsService = pGpsService;
	   }
	
	   public boolean setLocalizacaoAtual()
	   {
		   
		   return true;
	   }
}
