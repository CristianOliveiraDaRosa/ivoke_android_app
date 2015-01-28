package com.app.ivoke.objects;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

import com.app.ivoke.helpers.DateTimeHelper;
import com.app.ivoke.helpers.DebugHelper;

public class MuralPost{

    int muralPostId;
    int userId;

    String userName;
    String message;
    String createdAt;
    private float  distance;

    String facebookId;
    int anonymous;

    String messageTranlated;

    public MuralPost( int    pMuralPostId
                    , int    pUserId
                    , String pUserName
                    , String pMessage
                    , String pCreatedAt
                    , double pDistance
                    , String pFacebookId
                    , String pAnonymous)
    {
        userId         = pUserId;
        muralPostId    = pMuralPostId;
        userName       = pUserName;
        message        = pMessage;
        createdAt      = pCreatedAt;
        setDistance((float) pDistance);
        facebookId     = pFacebookId;

        if(pAnonymous == "null")
            anonymous      = 0;
        else
            anonymous      = Integer.parseInt(pAnonymous);

    }
    
    public int getId()
    {
        return muralPostId;
    }
    
    public int getUserId()
    {
        return userId;
    }
    
    public String getName()
    {
        return userName;
    }
    
    public String getMessage()
    {
        if(messageTranlated!=null)
            return messageTranlated;
        else
            return message;
    }
    
    public Date getDatePost()
    {
        return DateTimeHelper.parseToDate(createdAt);
    }
    
    public String getFacebookId()
    {
        return facebookId;
    }
    
    public boolean isAnonymous()
    {
        return anonymous == 1;
    }
    
    public void setMessate(String pMessage)
    {
        this.message = pMessage;
    }
    
    public void setTranslated(String pTranslated)
    {
        this.messageTranlated = pTranslated;
    }
    
    public boolean isTranslated()
    {
        return messageTranlated != null;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
    
}

