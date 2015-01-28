package com.app.ivoke.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.util.Log;

public class DateTimeHelper {

    public static final String DEFAULT_WEBSERVER_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @SuppressLint("SimpleDateFormat")
    public static Date parseToDate(String pDataString)
    {
        DateFormat formatter = new SimpleDateFormat(DEFAULT_WEBSERVER_DATETIME_FORMAT);
        formatter.setTimeZone(TimeZone.getDefault());
        try {
            return formatter.parse(pDataString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long getDaysBetween(Date now, java.util.Date dt)
    {
        long dif = now.getTime() - dt.getTime();
        return Math.round(dif / (1000*60*60*24));
    }

    public static long getHoursBetween(Date pDateNew, Date pDateOlder)
    {
        long dif = pDateNew.getTime() - pDateOlder.getTime();
        return Math.round(dif/(1000*60*60));
    }

    public static long getMinutesBetween(Date pDateNew, Date pDateOlder)
    {
        long dif = pDateNew.getTime() - pDateOlder.getTime();
        return Math.round(dif/(1000*60));
    }

    public static long getDaysFromMinutes(long pMinutes)
    {
        return (pMinutes/60)/24;
    }

    public static long getHoursFromMinutes(long pMinutes)
    {
        return (pMinutes/60);
    }

    public static long getMilisecondsFromMinutes(int pMinutes)
    {
        return 1000 * 60 * pMinutes;
    }
} 
