package com.app.ivoke;

import com.app.ivoke.controllers.about.FeedBackActivity;
import com.app.ivoke.controllers.chat.ChatActivity;
import com.app.ivoke.controllers.checking.CheckActivity;
import com.app.ivoke.controllers.checking.PlacesActivity;
import com.app.ivoke.controllers.login.FacebookLoginActivity;
import com.app.ivoke.controllers.login.LoginActivity;
import com.app.ivoke.controllers.main.MainActivity;
import com.app.ivoke.controllers.setting.SettingsActivity;
import com.app.ivoke.helpers.MetricHelper.Metric;
import com.app.ivoke.helpers.SettingsHelper;
import com.app.ivoke.objects.Account;
import com.app.ivoke.objects.UserIvoke;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.PlacePickerFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.audiofx.BassBoost.Settings;

public class Router {

    /*
     *
     *   This class has the function to standardize the calls and sending parameters between Activity
     *
     *      Using methods start and startForResult you keep refreshed the currentContext!
     *
     * */

    public  static Activity previousContext;
    private static Class<?> currentActivityClass;

    public static void finishPreviousContext()
    {
        previousContext.finish();
    }

    public static void previous(Activity pActivity)
    {
        previousContext = pActivity;
    }

    public static Class<?> getCurrentActivityClass()
    {
        return currentActivityClass;
    }

    public static void gotoFacebookLogin(Activity pActivity)
    {
        currentActivityClass = FacebookLoginActivity.class;
        start(pActivity, new Intent(pActivity, currentActivityClass));
        pActivity.finish();
    }

    public static void gotoIvokeLogin(Activity pActivity, Session pSession, GraphUser pFbUser)
    {
        currentActivityClass = LoginActivity.class;

        Intent i = new Intent(pActivity, currentActivityClass);
        i.putExtra(LoginActivity.PE_FACEBOOK_SESSION   , pSession);
        i.putExtra(LoginActivity.PE_FACEBOOK_USER_JSON , pFbUser.getInnerJSONObject().toString());
        start(pActivity, i);
        pActivity.finish();
    }

    public static void gotoChecking(Activity pActivity, Session pSession, UserIvoke pUser, GraphUser pFacebookUser)
    {
        currentActivityClass = CheckActivity.class;

        Intent i = new Intent(pActivity, currentActivityClass);
        i.putExtra(CheckActivity.PE_FACEBOOK_SESSION     , pSession);
        i.putExtra(CheckActivity.PE_IVOKE_USER           , pUser);
        i.putExtra(CheckActivity.PE_FACEBOOK_USER_JSON   , pFacebookUser.getInnerJSONObject().toString());

        start(pActivity,i);
    }

    public static void gotoPlaces(Activity pActivity, Location pLocalUsuario)
    {
        currentActivityClass = PlacesActivity.class;

        Intent i = new Intent(pActivity, currentActivityClass);i.putExtra( PlacePickerFragment.RESULTS_LIMIT_BUNDLE_KEY   , 5);
        i.putExtra( PlacePickerFragment.LOCATION_BUNDLE_KEY        , pLocalUsuario);
        i.putExtra( PlacePickerFragment.RADIUS_IN_METERS_BUNDLE_KEY
                  , Math.round(SettingsHelper.getMuralPostDistance(Metric.METER)));

        startForResult(pActivity, i, CheckActivity.RESULT_PLACE_ACT);
    }

    public static void gotoMain(Activity pActivity, UserIvoke pUser)
    {
        currentActivityClass = MainActivity.class;
        Intent i = new Intent(pActivity, currentActivityClass);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(MainActivity.PE_USER_IVOKE, pUser);

        start(pActivity, i);
    }

    public static void gotoSettings(Activity pActivity)
    {
        currentActivityClass = SettingsActivity.class;
        Intent i = new Intent(pActivity, currentActivityClass);
        start(pActivity, i);
    }

    public static void gotoChat(Activity pActivity, Account pAccount, boolean pIsAnonymous)
    {
        currentActivityClass = ChatActivity.class;
        Intent i = new Intent(pActivity, currentActivityClass);
        i.putExtra(ChatActivity.PE_CHAT_ACCOUNT, pAccount);
        i.putExtra(ChatActivity.PE_IS_CHAT_ANONYMOUS, pIsAnonymous);
        start(pActivity, i);
    }


    public static void gotoFeedback(Activity pActivity) {
        currentActivityClass = FeedBackActivity.class;
        Intent i = new Intent(pActivity, currentActivityClass);
        start(pActivity, i);
    }

    private static void start(Activity pActivity, Intent pIntent)
    {
        previousContext = pActivity;
        pActivity.startActivity(pIntent);
    }

    private static void startForResult(Activity pActivity, Intent pIntent, int requestCode)
    {
        previousContext = pActivity;
        pActivity.startActivityForResult(pIntent, requestCode);

    }



}
