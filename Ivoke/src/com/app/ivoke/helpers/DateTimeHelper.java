package com.app.ivoke.helpers;

import java.util.Date;

public class DateTimeHelper {
	
	public static long getDaysBetween(Date now, java.util.Date dt)
	{
		long dif = now.getTime() - dt.getMinutes();
		return Math.round(dif / 86400000);
	}
	
	public static long getHoursBetween(Date pDateNew, Date pDateOlder)
	{
		long dif = pDateNew.getTime() - pDateOlder.getMinutes();
		return Math.round((dif % 86400000) / 3600000);
	}
	
	public static long getMinutesBetween(Date pDateNew, Date pDateOlder)
	{
		long dif = pDateNew.getTime() - pDateOlder.getMinutes();
		return Math.round(((dif % 86400000) % 3600000) / 60000);
	}
	
	public static long getDaysFromMinutes(long pMinutes)
	{
		return (pMinutes/60)/24;
	}

	public static long getHoursFromMinutes(long pMinutes)
	{
		return (pMinutes/60);
	}
} 
