package com.app.ivoke;

import org.jivesoftware.smack.Roster;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.objects.services.XmppConnectionService;
import com.app.ivoke.objects.services.XmppServiceConnector;

public class ServiceManager {
    DebugHelper dbg = new DebugHelper("ServiceManager");

    private XmppServiceConnector xmppServiceConnector;

    public boolean startXmppService( Activity pActivity
                                   , String pUserName
                                   , String pPass
                                   , String pEmail
                                   , String pFullName
                                   , String pService)
    {
        Intent intentService = new Intent(pActivity, XmppConnectionService.class);

        intentService.putExtra(XmppConnectionService.PE_SERVER_HOST, pActivity.getString(R.string.ws_xmpp_host));
        intentService.putExtra(XmppConnectionService.PE_LOGIN      , pUserName );
        intentService.putExtra(XmppConnectionService.PE_PASSWORD   , pPass     );
        intentService.putExtra(XmppConnectionService.PE_EMAIL      , pEmail    );
        intentService.putExtra(XmppConnectionService.PE_FULL_NAME  , pFullName );
        intentService.putExtra(XmppConnectionService.PE_RESOURCE   , pService  );

        new StartServiceTask(pActivity).execute(intentService);

        return true;
    }

//    public boolean bindXmppService(Activity pActivity)
//    {
//        if(pActivity!=null && !pActivity.isFinishing())
//        {
//            dbg.method("bindXmppService").log("starting...");
//            Intent connectionService = new Intent(pActivity,XmppConnectionService.class);
//            if(xmppServiceConnector == null)
//               xmppServiceConnector  = new XmppServiceConnector();
//            pActivity.bindService(connectionService, xmppServiceConnector, Context.BIND_AUTO_CREATE);
//            return true;
//        }
//        else
//        return false;
//    }

    private class StartServiceTask extends AsyncTask<Intent, Void, Boolean> {

//        private ProgressDialog dialog;
        Context activityCaller;

        public StartServiceTask(Context context)
        {
            this.activityCaller = context;
        }


//        protected void onPreExecute() {
//            this.dialog = new ProgressDialog(Common.appContext);
//            this.dialog.setMessage(activityCaller.getString(R.string.connection_in_progress));
//            this.dialog.show();
//        }
        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final Intent... intents) {
            activityCaller.startService(intents[0]);
            return true;
        }

//        @Override
//        protected void onPostExecute(final Boolean result) {
//            if (this.dialog.isShowing())
//            {
//                this.dialog.dismiss();
//            }
//        }
    }

    public void unbindXmppService(Context pContext) {
        pContext.unbindService(xmppServiceConnector);
    }

    public XmppServiceConnector getServiceConnector() {
        return xmppServiceConnector;
    }

    public void setServiceConnection(XmppServiceConnector serviceConnection) {
        this.xmppServiceConnector = serviceConnection;
    }

}
