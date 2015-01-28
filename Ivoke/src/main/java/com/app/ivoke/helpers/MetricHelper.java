package com.app.ivoke.helpers;

public class MetricHelper {

    private static float oneMileToKm = (float) 0.621371192;

    public static float converKmToMile(float pKm)
    {
        return pKm*oneMileToKm;
    }

    public static float converMeterTo(Metric pMetric, float pMeter)
    {
        switch (pMetric) {
        case KM:
            return pMeter/1000;
        case MILLES:
            return pMeter*(oneMileToKm/1000);
        default:
            return pMeter;
        }
    }

    public static float convertMilesTo(Metric pMetric, float pValue)
    {
        switch (pMetric) {
        case KM:
            return oneMileToKm * pValue;

        case METER:
            return (oneMileToKm * pValue) * 1000;

        default:
            return pValue;
        }
    }

    public enum Metric
    {
        KM, METER, MILLES
    }
}