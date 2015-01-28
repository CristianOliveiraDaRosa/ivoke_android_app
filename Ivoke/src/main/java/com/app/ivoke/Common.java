package com.app.ivoke;

import java.util.ArrayList;
import java.util.HashMap;

import org.acra.*;
import org.acra.annotation.*;

import com.app.ivoke.helpers.LocationHelper;
import com.app.ivoke.objects.Account;
import com.app.ivoke.objects.UserIvoke;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.app.Application;
import android.content.Context;

@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
//        formUri             = "http://localhost:3000/debugs/acra_report",
//        formUri             = "http://gerenciafc.com.br/debugs/acra_report",
        resToastText        = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        httpMethod          = org.acra.sender.HttpSender.Method.POST
    )

public class Common extends Application {

    public static Context appContext;
    public String  deviceRegistrationId;
    private LocationHelper.Listener locationListener;
    private ServiceManager serviceManager;
    private Session facebookSession;
    private GraphUser facebookUser;
    private UserIvoke userIvoke;
    private Account xmppAccount;

    private static Common application;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext       = getApplicationContext();
        locationListener = LocationHelper.getLocationListener(this);

        application      = this;

        ACRAConfiguration config = ACRA.getConfig();
        config.setFormUri(getString(R.string.ws_url) + getString(R.string.ws_url_error_log) );
        config.setForceCloseDialogAfterToast(true);
        ACRA.setConfig(config);

        ACRA.init(this);
    }

    public static Common getInstance()
    {
        return application;
    }

    public void setDeviceRegistrationId(String pRegID)
    {
        deviceRegistrationId = pRegID;
    }

    public String getDeviceRegistrationId()
    {
        return deviceRegistrationId;
    }

    public void setLocationListener(LocationHelper.Listener pLocationListener)
    {
        locationListener = pLocationListener;
    }

    public LocationHelper.Listener getLocationListener()
    {
        return locationListener;
    }

    public ServiceManager getServiceManager()
    {
        if(serviceManager == null)
            serviceManager = new ServiceManager();
        return serviceManager;
    }

    public void setFacebookSession(Session pFbSession)
    {
        this.facebookSession = pFbSession;
    }

    public Session getFacebookSession()
    {
        return facebookSession;
    }

    public UserIvoke getSessionUser() {
        return userIvoke;
    }

    public void setSessionUser(UserIvoke pUserIvoke) {
        this.userIvoke = pUserIvoke;
    }

    public GraphUser getFacebookUser() {
        return facebookUser;
    }

    public void setFacebookUser(GraphUser pFacebookUser) {
        this.facebookUser = pFacebookUser;
    }

    public boolean isUserSession(int pUserId)
    {
        return (pUserId != 0) && (userIvoke != null) ?
                 (userIvoke.getId() == pUserId) : false;
    }

    public Account getXmppAccount() {
        return xmppAccount;
    }

    public void setXmppAccount(Account account) {
        this.xmppAccount = account;
    }

}
