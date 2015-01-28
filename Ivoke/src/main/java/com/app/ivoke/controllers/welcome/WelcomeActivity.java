package com.app.ivoke.controllers.welcome;

import java.util.TimeZone;

import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.DeviceHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.SettingsHelper;
import com.app.ivoke.helpers.WebHelper.NetworkException;
import com.app.ivoke.helpers.WebHelper.ServerException;
import com.app.ivoke.libraries.GCMManager;
import com.app.ivoke.models.TranslateModel;
import com.app.ivoke.models.TranslateModel.Languages;
import com.app.ivoke.objects.defaults.DefaultOkListener;
import com.app.ivoke.objects.defaults.DefaultWebCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class WelcomeActivity extends FragmentActivity {
     DebugHelper dbg = new DebugHelper("WelcomeActivity");
     GCMManager gcmManager = new GCMManager();

     RegisterDeviceCallback registerCallback;

     MessageHelper.MessageAlert poMsgAlert;

     BtnNextOnClick btnNextOnClickListener = new BtnNextOnClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbg.method("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_activity);

        Router.previous(this);

        if (verifyApplicationRequisites()) {
            String regid = gcmManager.getRegistrationId(this);
            dbg.var("regid", regid);
            if (regid!=null && DeviceHelper.checkGooglePlayServices(this)) 
            {
                registerCallback = new RegisterDeviceCallback(this);
                gcmManager.registerInBackground(registerCallback);
            }
            else
            {
                gotoFacebooklogin(this);
//                Router.gotoFacebookLogin(this);
//                this.finish();
            }
        }
        else
        {
            poMsgAlert.showDialog();
        }
    }

    private void gotoFacebooklogin(Activity pAct)
    {
        if(!SettingsHelper.appHasBeenConfigured())
            showFragment(new WelcomeTutorialFragment());
        else
        {
            Router.gotoFacebookLogin(pAct);
            pAct.finish();
        }
    }

    private void showFragment(Fragment pFragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, pFragment).commit();
    }

    private boolean verifyApplicationRequisites()
    {
//        if(!DeviceHelper.checkGooglePlayServices(this))
//        {
//            poMsgAlert = MessageHelper.errorAlert(this)
//                                        .setMessage(R.string.def_error_msg_google_play_not_available)
//                                        .setButtonOk(new DefaultOkListener(this) {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                finish();
//                                            }
//                                        });
//
//        }
//        else
        if (!DeviceHelper.hasInternetConnection())
        {
                poMsgAlert = MessageHelper.errorAlert(this)
                            .setMessage(R.string.def_error_msg_whitout_internet_connection)
                            .setButtonOk(new DefaultOkListener(this) {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
        }
        else if(!DeviceHelper.hasGpsOrInternetLocalization(this))
        {

            poMsgAlert =   MessageHelper.errorAlert(this)
                                        .setMessage(R.string.check_msg_info_on_get_user_local)
                                        .setButtonYesNo(new OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if(which == MessageHelper.DIALOG_RESULT_YES)
                                                {
                                                    //Intent i = LocationHelper.getIntentGpsSettings();
                                                    //startActivity(i);
                                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                                    finish();
                                                }
                                                else
                                                {
                                                    finish();
                                                }

                                            }
                                        });

        }

        return poMsgAlert == null;
    }

    public BtnNextOnClick getButtonNextListener()
    {
        return btnNextOnClickListener;
    }

    class RegisterDeviceCallback extends DefaultWebCallback
    {
        Activity actCaller;

        public RegisterDeviceCallback(Activity pActivity)
        {
            actCaller = pActivity;
        }

        @Override
        public void onCompleteTask(Object pResult) {
            super.onCompleteTask(pResult);

            if(pResult!=null)
            {
                gotoFacebooklogin(actCaller);
//                Router.gotoFacebookLogin(actCaller);
//                actCaller.finish();
            }
        }
    }

    public class BtnNextOnClick implements android.view.View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.welcome_main_btn_next:

                showFragment(new LocalizationTutorialFragment());

                break;
            case R.id.welcome_localization_btn_next:

                showFragment(new SettingsTutorialFragment());

                break;
            case R.id.welcome_settings_btn_next:

                SettingsHelper.setValue( Router.previousContext
                                       , getString(R.string.pkey_app_has_been_configured)
                                       , true);

                gotoFacebooklogin(Router.previousContext);
            }
        }
    }
}
