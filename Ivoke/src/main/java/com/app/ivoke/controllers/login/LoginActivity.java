package com.app.ivoke.controllers.login;

import java.io.IOException;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONObject;

import com.app.ivoke.Common;
import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.ServiceManager;
import com.app.ivoke.helpers.DeviceHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.libraries.GCMManager;
import com.app.ivoke.models.UserModel;
import com.app.ivoke.objects.UserIvoke;
import com.app.ivoke.objects.defaults.DefaultBackgroudWorker;
import com.app.ivoke.objects.defaults.DefaultOkListener;
import com.app.ivoke.objects.services.XmppConnectionService;
import com.app.ivoke.objects.services.XmppServiceConnector;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

public class LoginActivity extends android.support.v4.app.FragmentActivity {

    public static final String PE_FACEBOOK_SESSION = "LoginActivity.FacebookSession";
    public static final String PE_FACEBOOK_USER_JSON = "LoginActivity.FacebookUser";
    static DebugHelper debug = new DebugHelper("LoginActivity");

    private UserModel userModel = new UserModel();

    private Session   fbSession;
    private GraphUser fbUser;
    private UserIvoke userIvoke;

    private LoginBackground loginBackground;

    public TextView lblProgress;

    MessageHelper.MessageAlert msgError;
    Common common;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        debug.method("onCreate").par("savedInstanceState", savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        common = (Common) getApplication();

        getExtras();
//        doLoginOnIvoke();

    }

    public void showUserNotError()
    {
        MessageHelper.errorAlert(this).setMessage(R.string.login_msg_error_user_not_found).showDialog();
    }

    public static class PlaceholderFragment extends Fragment {
         @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.login_fragment, container, false);

            return rootView;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        lblProgress = (TextView) findViewById(R.id.login_lbl_processing);
        doLoginOnIvoke();
    }

    private void getExtras()
    {
        debug.method("getExtras");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fbSession = (Session) extras.getSerializable(PE_FACEBOOK_SESSION);

            String jsonFbUser = extras.getString(PE_FACEBOOK_USER_JSON);

             debug.var("jsonFbUser" , jsonFbUser);
            try {
                JSONObject jsonObj = new JSONObject(jsonFbUser);
                fbUser = GraphObject.Factory.create(jsonObj, GraphUser.class);
            } catch (Exception e) {
                debug.exception(e);
                MessageHelper.errorAlert(this)
                             .setMessage(R.string.check_msg_error_get_fb_user)
                             .setButtonOk(new DefaultOkListener(this) {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Router.gotoFacebookLogin(this.getActivity());
                                }
                            }).showDialog();
            }

        }
        debug.var("fbSession",fbSession);

    }

    private void doLoginOnIvoke()
    {
        debug.method("tryToDoLoginOnIvoke");
        loginBackground = new LoginBackground(this);
        loginBackground.execute();

    }

    private void goToChecking() {

        Router.gotoChecking(this, fbSession, userIvoke, fbUser);
        this.finish();

    }

    private class LoginBackground extends DefaultBackgroudWorker
    {
        LoginActivity loginActivity;

        public LoginBackground(Activity pActivity) {
            super(pActivity);
            loginActivity = (LoginActivity) pActivity;
        }

        @Override
        protected Object doInBackground(Object... params) {
            debug.method("doInBackground");

            if(!DeviceHelper.hasInternetConnection())
            {
                setException(new Exception(loginActivity.getString(R.string.def_error_msg_whitout_internet_connection)));
                return false;
            }

            if(fbUser!=null)
            {
                try {
                    userIvoke = userModel.requestIvokeUser(fbUser.getId());

                    if(userIvoke == null)
                    {
                        String gender = fbUser.getProperty("gender").toString();
                        debug.var("gender", gender);
                        userIvoke =
                                userModel.createUser( fbUser.getName()
                                                    , gender
                                                    , fbUser.getId());
                    }

                    String regid = new GCMManager().getRegistrationId(loginActivity);

                    debug.log("regid "+regid);
                    userModel.asyncRegisterDevice(userIvoke, regid);


                } catch (Exception e) {
                    debug.exception(e);
                    setException(new Exception(loginActivity.getString(R.string.def_error_msg_ws_server_not_responding)));
                }
            }
            else
            {
                setException(new Exception(loginActivity.getString(R.string.login_error_msg_facebookuser_not_found)));
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            debug.method("onPostExecute"+result);
            if(inError())
            {
                MessageHelper.errorAlert(loginActivity)
                 .setMessage(getException().getMessage())
                 .setButtonOk(new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            loginActivity.finish();
                                        }
                                    }).showDialog();
            }
            else if(userIvoke!=null)
            {
                openXmppService();
//                new XmmpConnectWaitAsync(loginActivity).execute();
//                loginActivity.goToChecking();
            }
            else
            {
                MessageHelper.errorAlert(loginActivity)
                             .setMessage(R.string.login_msg_error_user_not_found_ask_try_again)
                             .setButtonYesNo(new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if(which == MessageHelper.DIALOG_RESULT_YES)
                                    {
                                        loginBackground.execute();
                                    }
                                    else
                                    {
                                        finish();
                                    }
                                }
                            }).showDialog();
            }
        }

        private void openXmppService() {

            String defaultHostEmail = getString(R.string.ws_xmpp_default_site);

            Intent intentService = new Intent(loginActivity, XmppConnectionService.class);

            intentService.putExtra(XmppConnectionService.PE_SERVER_HOST, loginActivity.getString(R.string.ws_xmpp_host));
            intentService.putExtra(XmppConnectionService.PE_LOGIN      , fbUser.getId()+"@"+defaultHostEmail);
            intentService.putExtra(XmppConnectionService.PE_PASSWORD   , fbUser.getId()     );
            intentService.putExtra(XmppConnectionService.PE_EMAIL      , fbUser.getId()+"@"+defaultHostEmail);
            intentService.putExtra(XmppConnectionService.PE_FULL_NAME  , fbUser.getName() );
            intentService.putExtra(XmppConnectionService.PE_RESOURCE   , "ivoke_"+userIvoke.getId());

            new StartServiceTask().execute(intentService);

        }
        private class StartServiceTask extends AsyncTask<Intent, Void, Boolean> {

//            private ProgressDialog dialog;

            protected void onPreExecute() {
//                this.dialog = new ProgressDialog(loginActivity);
//                this.dialog.setMessage(loginActivity.getString(R.string.connection_in_progress));
//                this.dialog.show();
                lblProgress.setText(R.string.connection_in_progress);

            }
            // automatically done on worker thread (separate from UI thread)
            protected Boolean doInBackground(final Intent... intents) {
                loginActivity.startService(intents[0]);
                XmppServiceConnector conn = new XmppServiceConnector();
                loginActivity.bindService(intents[0], conn, Context.BIND_AUTO_CREATE);

                while (!conn.isConnected) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                XmppConnectionService service = conn.getXmppService();
                debug.log("service coneected");
                if(service!=null)
                   service.userLogin();

                loginActivity.unbindService(conn);
                return true;
            }

            @Override
            protected void onPostExecute(final Boolean result) {
//                if (this.dialog.isShowing())
//                {
//                    this.dialog.dismiss();
//                }

                loginActivity.goToChecking();
            }
        }
    }
}