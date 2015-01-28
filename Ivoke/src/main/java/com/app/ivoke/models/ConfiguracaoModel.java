package com.app.ivoke.models;

import com.app.ivoke.Router;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ConfiguracaoModel {

    private static SharedPreferences prefs;

    private static String APP_PACKAGE         = "com.app.ivoke";
    private static String MURAL_POST_DISTANCE = APP_PACKAGE+".mural_post_distance";
    private static String BLOCK_ADVERTMENT    = APP_PACKAGE+".block_advertment";

    public ConfiguracaoModel()
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(Router.previousContext);
    }

    public ConfiguracaoModel(Context pContext)
    {
        prefs = pContext.getSharedPreferences( APP_PACKAGE
                                               , Context.MODE_PRIVATE);
    }

    public int getMuralPostDistance()
    {
        return prefs.getInt(MURAL_POST_DISTANCE, 100);
    }

}
