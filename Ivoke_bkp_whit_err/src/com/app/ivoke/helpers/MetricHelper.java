package com.app.ivoke.helpers;

public class MetricHelper {
	
	private static float oneMileToKm = (float) 0.621371192;
	
	public static float converKmToMile(float pKm)
	{
		return pKm*oneMileToKm;
	}
}
