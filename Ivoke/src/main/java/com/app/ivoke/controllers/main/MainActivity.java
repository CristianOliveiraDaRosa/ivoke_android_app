package com.app.ivoke.controllers.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.app.ivoke.Common;
import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.controllers.chat.ChatActivity;
import com.app.ivoke.controllers.chat.XmppChatListener;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.DeviceHelper;
import com.app.ivoke.helpers.LocationHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.MetricHelper.Metric;
import com.app.ivoke.helpers.SettingsHelper;
import com.app.ivoke.helpers.WebHelper.NetworkException;
import com.app.ivoke.helpers.WebHelper.ServerException;
import com.app.ivoke.libraries.NotificationManager;
import com.app.ivoke.models.FacebookModel;
import com.app.ivoke.models.MuralModel;
import com.app.ivoke.objects.Account;
import com.app.ivoke.objects.MuralPost;
import com.app.ivoke.objects.UserIvoke;
import com.app.ivoke.objects.defaults.DefaultBackgroudWorker;
import com.app.ivoke.objects.interfaces.IChatCallBack;
import com.app.ivoke.objects.services.XmppConnectionService;
import com.app.ivoke.objects.services.XmppConnectionServiceBinder;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.drm.DrmManagerClient.OnErrorListener;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements ServiceConnection {
    
    public static final String PE_USER_IVOKE       = "MainActivity.UserIvoke";
    public static final String PE_FACEBOOK_SESSION = "MainActivity.FacebookSession";

    DebugHelper dbg = new DebugHelper("MainActivity");

    Common common;

    /* Declare Models */
    MuralModel muralModel = new MuralModel();

    /* Declare Fragments */
    Fragment previusFragment;
    MuralFragment muralFragment = new MuralFragment();
    ProcessingFragment processingFragment = new ProcessingFragment();
    ContactsFragment conversationFragment = new ContactsFragment();



    /*  Declare other vars */
    UserIvoke                user;
    LocationHelper.Listener  locationProvider;
    Location                 localAtual;
    //XmppServiceConnector     xmppServiceConnector = new XmppServiceConnector();
    RequestMuralPostsAsync   requestMuralPostsAsync;

    MenuItem                 contactMenuItem;
    MenuItem                 placeMenuItem;

    XmppConnectionService    xmppService;
    ChatCallBack             chatCallback = new ChatCallBack();

    Random gerador = new Random();
    /*** EVENTS ***/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbg.method("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        common = (Common) getApplication();
        user   = common.getSessionUser(); //(UserIvoke) extras.getSerializable(PE_USER_IVOKE);
//        common.getServiceManager().bindXmppService(this);
//        xmppServiceConnector = common.getServiceManager().getServiceConnector();
        
        Intent connectionService = new Intent(this, XmppConnectionService.class); 
        bindService(connectionService, this, Context.BIND_AUTO_CREATE);
        
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if(intent.hasExtra(NotificationManager.PE_FROM_NOTIFICATION))
                showContactFragment();
            else
                showMuralFragment();
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        contactMenuItem = menu.findItem(R.id.main_act_menu_contacts);
        placeMenuItem   = menu.findItem(R.id.main_act_menu_checking);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
      switch (id) {
        case R.id.main_act_menu_checking:

            Router.gotoChecking( this
                               , common.getFacebookSession()
                               , common.getSessionUser()
                               , common.getFacebookUser());

            //this.finish();

            break;
        case R.id.main_act_menu_mural:

            showMuralFragment();

            break;
        case R.id.main_act_menu_contacts:
            contactMenuItem.setIcon(R.drawable.ic_action_chat);
            showContactFragment();
//            Router.gotoContacts(this);

            break;
        case R.id.main_act_menu_settings_dropdown_setting:
            Router.gotoSettings(this);

            break;
        case R.id.main_act_menu_settings_dropdown_logout:

            FacebookModel facebookModel = new FacebookModel();

            facebookModel.logout();

            SettingsHelper.setValue(this
                                   , getString(R.string.pkey_facebook_has_logged)
                                   , false);

            this.finish();

            break;
        case R.id.main_act_menu_help:

             MessageHelper.showHelp(this, R.string.help_main_lbl_how_works_desc);

            break;
        case R.id.main_act_menu_feedback:

            Router.gotoFeedback(this);

            break;
        default:
            break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        dbg.method("onRestart");
    }
    
    @Override
    public void onBackPressed() {
        //
        if(muralFragment.isVisible())
            super.onBackPressed();
        else
        if(muralFragment!=null)
            showFragment(muralFragment);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            requestMuralPostsAsync.cancel(true);
            unbindService(this);

        } catch (Exception e) {}

    }
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        dbg.method("onServiceConnected").par("name", name);
        xmppService = (XmppConnectionService) ((XmppConnectionServiceBinder)service).getService();
        XmppChatListener chatListener = xmppService.getChatListener();
        dbg.var("chatListener", chatListener);

        if(chatListener==null)
        {
            chatListener = new XmppChatListener(xmppService.getConnection());
            xmppService.setChatListener(chatListener);
        }

        chatListener.setCallback(chatCallback);
        dbg.log("fim");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {}
    
    /*** METHODS ***/
    public void deleteMuralPost(MuralPost pMuralPost)
    {
        dbg.method("deleteMuralPost").par("pMuralPost", pMuralPost);
        muralModel.deleteMuralPost(pMuralPost);
    }

    private void showMuralFragment() {

        try {

            if(user.isOnAPlace())
                setTitle(user.getLocalCheckingName());
            else
                setTitle(R.string.main_title);

            findUserLocation();
//             muralModel.asyncGetNearbyPosts( user.getLocalization()
//                                           , SettingsHelper.getMuralPostDistance()
//                                           , new MuralCallback());
            
            if(requestMuralPostsAsync == null || !requestMuralPostsAsync.isRunning())
            {
                requestMuralPostsAsync = new RequestMuralPostsAsync(this);
                requestMuralPostsAsync.execute();
            }

        }catch (Exception e) {
            dbg.method("showMuralFragment").exception(e);
            MessageHelper.errorAlert(this)
                         .setMessage(e.getMessage())
                         .showDialog();
        }
   }
    
    public void findUserLocation() throws Exception
    {
        try {

            if(common == null)
                common = (Common) getApplication();

                locationProvider = common.getLocationListener();
                locationProvider.listenerForUser(user);
                LocationHelper.getRequestLocation(this, locationProvider);

        } catch (Exception e) {
            throw new Exception(getString(R.string.def_error_msg_location_not_found));
        }
    }
  
    public void showProcessingFragment()
    {
        if(user.isOnAPlace())
            processingFragment
            .setMessageProgress( String.format(getString(R.string.main_mural_finding_mural_on_place)
                               , user.getLocalCheckingName()));
        else
            processingFragment
            .setMessageProgress( String.format(getString(R.string.main_mural_finding_mural_by_distance)
                               , SettingsHelper.getMuralPostDistance(Metric.METER)));

        showFragment(processingFragment);
    }
    
    public void showContactFragment()
    {
        setTitle(R.string.title_activity_contacts);
        
        try {
            showFragment(conversationFragment);
        } catch (Exception e) {
            MessageHelper.errorAlert(this)
             .setMessage(R.string.def_error_msg_ws_server_not_responding)
             .showDialog();
        }
    }

    private void showFragment(Fragment pFragment)
    {
        try {

            getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.container, pFragment).commit();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public Common getCommon()
    {
        return common;
    }

    /*** CLASSES ***/
    public class RequestMuralPostsAsync extends DefaultBackgroudWorker
    {
        String messageError;
        List<MuralPost> posts;

        public RequestMuralPostsAsync(Activity pActivity) {
            super(pActivity);
            messageError = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProcessingFragment();
        }

        @Override
        protected Object doInBackground(Object... params) {
             posts = new ArrayList<MuralPost>();

                try {
                    if(user.isOnAPlace())
                        posts = muralModel.getPostsFromPlace(user.getPlaceId());
                    else
                        posts = muralModel.getNearbyPosts(user.getLocalization(), SettingsHelper.getMuralPostDistance(Metric.MILLES));

                } catch (Exception e) {
                    if(e.getClass().equals(NetworkException.class)
                     ||e.getClass().equals(ServerException.class))
                        messageError = e.getMessage();
                    else
                        e.printStackTrace();
                }

            return posts;
        }

        @Override
        public void onComplete(Object result) {
            if(muralFragment!=null){
                muralFragment.setPosts(posts);
                showFragment(muralFragment);

            }
        }


    }    
    
    private class ChatCallBack implements IChatCallBack
    {
        @Override
        public void onPacketReceived(String pUserJid, String pMessage) {
            dbg._class(this).method("onPacketReceived")
                            .par("pUserJid", pUserJid)
                            .par("pMessage", pMessage);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    contactMenuItem.setIcon(R.drawable.ic_action_chat_blue);
                }
            });
        }

    }

    public XmppConnectionService getXmppService() {
        return xmppService;
    }

}
