package com.app.ivoke.objects;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import com.app.ivoke.helpers.DebugHelper;

public class MuralPost{
	
	int muralPostId;
	int userId;

    String userName;
	String message;
	String createdAt;
	float  distance;
	
	String facebookId;
    
    public MuralPost( int    pMuralPostId
    		        , int    pUserId
    		        , String pUserName
    		        , String pMessage
    		        , String pCreatedAt
    		        , double pDistance
    		        , String pFacebookId)
    {
    	muralPostId    = pMuralPostId;
        userName       = pUserName;
    	message        = pMessage;
    	createdAt      = pCreatedAt;
    	distance       = (float) pDistance;
    	facebookId     = pFacebookId;
    }
    
    public int getId()
    {
    	return muralPostId;
    }
    
    public String getNome()
    {
    	return userName;
    }
    
    public String getMessage()
    {
    	return message;
    }
    
    @SuppressLint("SimpleDateFormat")
	public Date getDatePost()
    {	 
        try {
        	SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        	return ft.parse(createdAt);
		} catch (Exception e) {
			new DebugHelper("MuralPost").method("getQuando").var("createdAt", createdAt).exception(e);
			return null;
		}
    	 
    }
    
    public String getFacebookId()
    {
    	return facebookId;
    }
    
}

