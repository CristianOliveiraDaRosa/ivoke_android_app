package com.app.ivoke.models;

import com.app.ivoke.helpers.WebHelper;

public class WebServer {
	   
	   public final static String SITE_URL = "http://10.0.2.2:3000";
	   //Usuario
	   public final static String URL_USUARIO_ADD     = "/usuarios.json";
	   public final static String URL_USUARIO_GET     = "/usuarios/existe.json";
	   //Mural
	   public final static String URL_MURAL_POSTS_GET = "/mural_posts.json";
	   
	   WebHelper web = new WebHelper();
	   
	   public String getUrl(String pUrlAccess)
	   {
		   return SITE_URL+pUrlAccess;
	   }
}
