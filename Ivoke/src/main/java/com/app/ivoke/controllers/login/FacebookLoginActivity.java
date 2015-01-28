package com.app.ivoke.controllers.login;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.DeviceHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.SettingsHelper;
import com.app.ivoke.models.FacebookModel;
import com.app.ivoke.objects.defaults.DefaultBackgroudWorker;
import com.app.ivoke.objects.defaults.DefaultOkListener;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.StatusCallback;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class FacebookLoginActivity extends ActionBarActivity {

    DebugHelper   debug     = new DebugHelper("FacebookLoginActivity");

    FacebookLoginCallBack callBack = new FacebookLoginCallBack();
    FacebookModel facebookModel    = new FacebookModel();

    private UiLifecycleHelper uiHelper;

    RequestFacebookUserBackground requestFaceUser;

//    ProgressDialog dialog;

    Session activeSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        debug.method("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_login_activity);

        TextView lblAdvice = (TextView) findViewById(R.id.facebook_login_lbl_advice);

        if(DeviceHelper.hasInternetConnection())
        {
            Session activeSession = facebookModel.getActiveSession();
//            if(SettingsHelper.hasLoggedFacebookBefore()){
            if(facebookModel.hasSessionOpened())
            {

                //dialog = new ProgressDialog(this);
                //this.dialog.setMessage(getString(R.string.com_facebook_loading));
                //this.dialog.show();
                getUiHelper().onCreate(savedInstanceState);
                //facebookModel.openSessionAsync(this, callBack);
                gotoIvokeLoginActivity(facebookModel.getActiveSession());
                //requestFaceUser = new RequestFacebookUserBackground(this);
                lblAdvice.setText(R.string.com_facebook_loading);
            }else
            {
                com.facebook.widget.LoginButton
                btnFacebook = (com.facebook.widget.LoginButton) findViewById(R.id.facebook_login_btn_facebook);

                btnFacebook.setVisibility(View.VISIBLE);

            }
        }
        else
        {
            //if (this.dialog.isShowing())
            //    this.dialog.dismiss();

            MessageHelper.errorAlert(this)
                        .setMessage(R.string.def_error_msg_whitout_internet_connection)
                        .setButtonOk(new OnClickListener() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).showDialog();
        }
  }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facebook_login_activity, container, false);


        return view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        debug.method("onActivityResult").par("requestCode",requestCode)
                                        .par("resultCode",resultCode)
                                        .par("data",data);
        
        super.onActivityResult(requestCode, resultCode, data);

        getUiHelper().onActivityResult(requestCode, resultCode, data);
        gotoIvokeLoginActivity(facebookModel.getActiveSession());

    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        debug.method("onSessionStateChange")
                .par("session",session)
                .par("state",state)
                .par("exception",exception);

        if (state.isOpened()) {
            debug.log("Logged!");

             SettingsHelper.setValue(this, getString(R.string.pkey_facebook_has_logged), true);
             gotoIvokeLoginActivity(session);

        } else if (state.isClosed()) {
            debug.log("Logged out...");
            SettingsHelper.setValue(this, getString(R.string.pkey_facebook_has_logged), false);

        }
    }

    private void gotoIvokeLoginActivity(Session pSession)
    {
        requestFaceUser = new RequestFacebookUserBackground(this);
        requestFaceUser.setSession(pSession);
        if(requestFaceUser.getStatus() != AsyncTask.Status.RUNNING)
            requestFaceUser.execute();
        //gotoIvokeLoginActivity(session);
   }

    public class FacebookLoginCallBack implements StatusCallback
    {
        @Override
        public void call(Session session, SessionState state, Exception exception)
        {
            debug._class(this).method("call");
            onSessionStateChange(session, state, exception);
        }
    }

    private UiLifecycleHelper getUiHelper()
    {
        if(uiHelper == null)
           uiHelper = new UiLifecycleHelper(this, callBack);
        return uiHelper;
    }

    private class RequestFacebookUserBackground extends DefaultBackgroudWorker
    {
        Session session;
        GraphUser fbUser;

        public RequestFacebookUserBackground(Activity pActivity) {
            super(pActivity);
        }

        @Override
        protected Object doInBackground(Object... params) {
            debug._class(this).method("doInBackground");
            fbUser = facebookModel.requestFacebookUser();

            return true;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if(fbUser!=null)
            {
                Router.gotoIvokeLogin(getActivityCaller(), session, fbUser);
                finish();

            }else
            {
//                if (dialog!=null)
//                if (dialog.isShowing())
//                    dialog.dismiss();

                MessageHelper.errorAlert(getActivityCaller())
                             .setMessage(R.string.facebook_error_msg_request_error)
                             .setButtonOk(new DefaultOkListener(getActivityCaller()) {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivityCaller().finish();
                                }
                            }).showDialog();
            }
        }

        public void setSession(Session pSession)
        {
            session = pSession;
        }
    }
}
