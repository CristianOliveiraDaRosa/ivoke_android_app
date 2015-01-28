package com.app.ivoke.helpers;

import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.helpers.MetricHelper.Metric;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SettingsHelper {

    public static float getMuralPostDistance(MetricHelper.Metric pMetric)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Router.previousContext);
        String val = pref.getString(Router.previousContext.getString(R.string.pkey_mural_post_distance), "500");

          switch (pMetric) {
            case KM:
                 return MetricHelper.convertMilesTo(Metric.KM, Integer.parseInt(val));

            case METER:
                return Integer.parseInt(val);

            case MILLES:
                return MetricHelper.converMeterTo(Metric.MILLES,Integer.parseInt(val));

            default:
                return 0;
          }

    }

    public static boolean askForChecking(Activity pAct)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(pAct);
        return pref.getBoolean(pAct.getString(R.string.pkey_ask_for_checking), true);
    }

    public static int frequencyRefreshMural(Activity pAct)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(pAct);
        return pref.getInt(pAct.getString(R.string.pkey_frequency_refresh_mural), 5)*1000*60;
    }

    public static boolean hasLoggedFacebookBefore() {
        return getValue(R.string.pkey_facebook_has_logged, false);
    }

    public static boolean appHasBeenConfigured()
    {
        return getValue(R.string.pkey_app_has_been_configured, false);
    }

    public static boolean showAnonymousPosts()
    {
        return getValue(R.string.pkey_show_anonymous_posts, true);
    }

    /*  SET AND GET */
    public static void setValue(Activity pAct, String pPrefKey, String pString)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(pAct);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(pPrefKey, pString);
        editor.commit();
    }

    public static void setValue(Activity pAct, String pPrefKey, int pInt)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(pAct);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(pPrefKey, pInt);
        editor.commit();
    }

    public static Editor getEditor(Context pContext)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(pContext);
        return prefs.edit();
    }

    public static SharedPreferences getSharedPreference(Context pContext)
    {
        return PreferenceManager.getDefaultSharedPreferences(pContext);
    }

    public static boolean getValue(int pResId, boolean pDefault)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Router.previousContext);
        return prefs.getBoolean(Router.previousContext.getString(pResId), pDefault);
    }

    public static String getValue(int pResId, String pDefault)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Router.previousContext);
        return prefs.getString(Router.previousContext.getString(pResId), pDefault);
    }

    public static int getValue(int pResId, int pDefault)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Router.previousContext);
        return prefs.getInt(Router.previousContext.getString(pResId), pDefault);
    }

    public static void setValue(Activity pAct, String pPrefKey,boolean pValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Router.previousContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(pPrefKey, pValue);
        editor.commit();

    }


}
