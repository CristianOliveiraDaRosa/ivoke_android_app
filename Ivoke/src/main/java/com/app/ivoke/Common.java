package com.app.ivoke;

import android.app.Application;
import android.content.Context;

public class Common extends Application {

    public static Context appContext;
    public static String  deviceRegistrationId;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext  = getApplicationContext();
    }

    public static void setDeviceRegistrationId(String pRegID)
    {
        deviceRegistrationId = pRegID;
    }

    public static String getDeviceRegistrationId()
    {
        return deviceRegistrationId;
    }
}
