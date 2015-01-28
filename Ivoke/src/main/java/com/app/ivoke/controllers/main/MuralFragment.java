package com.app.ivoke.controllers.main;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.XMPPException;

import com.app.ivoke.R;
import com.app.ivoke.Router;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;

import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.MetricHelper.Metric;
import com.app.ivoke.helpers.SettingsHelper;
import com.app.ivoke.helpers.WebHelper.NetworkException;
import com.app.ivoke.helpers.WebHelper.ServerException;
import com.app.ivoke.models.TranslateModel;
import com.app.ivoke.models.TranslateModel.Languages;
import com.app.ivoke.objects.*;
import com.app.ivoke.objects.defaults.DefaultBackgroudWorker;
import com.app.ivoke.objects.defaults.DefaultWebCallback;
import com.app.ivoke.objects.extended_android.PullDownListView;
import com.app.ivoke.objects.extended_android.PullDownListView.ListViewTouchEventListener;
import com.app.ivoke.objects.services.XmppConnectionService;

public class MuralFragment extends Fragment {
      DebugHelper dbg = new DebugHelper("MuralFragment");

      final int RESCODE_MURAL_POSTED = 1;

      MainActivity          mainAct;
      MuralAdapter          adapter;
      RefreshMuralCallback  refreshCallback = new RefreshMuralCallback();
      Timer                 muralRefreshTimer;
      MuralRefreshTask      muralRefreshTask;

      List<MuralPost> postagens;

      View     fragmentView;
      PullDownListView listView;

      Button      btnPostar;
      boolean     btnPostarBusy;

      MuralPost currentMuralPost;
      int       currentPosition;

      XmppConnectionService xmppService;

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbg.method("onCreateView");
        fragmentView = inflater.inflate(R.layout.main_mural_fragment, container, false);

        mainAct  = (MainActivity) getActivity();
        listView = (PullDownListView) fragmentView.findViewById(R.id.main_mural_posts_list);

        registerForContextMenu(listView);

        adapter  = new MuralAdapter(fragmentView.getContext(), postagens);
        adapter.setBtnStartChatListener(new ButtonStartChatOnClick());
        adapter.setBtnOpenFacebookListener(new ButtonOpenFacebookOnClick());
        adapter.setBtnTranslateListener(new ButtonTranslateListener());

        listView.setAdapter(adapter);

        //listView.setListViewTouchListener(new PullToRefreshListener());
        //listView.setOnTouchListener(new DownRefreshingListenter());
        listView.setFocusable(true);

        btnPostar = (Button) fragmentView.findViewById(R.id.main_mural_create_post_button);

        btnPostar.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainAct, MuralPostActivity.class);
                startActivityForResult(i, RESCODE_MURAL_POSTED);
            }
        });


        xmppService = (XmppConnectionService) mainAct.getXmppService();

        return fragmentView;
      }

      @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dbg.method("onActivityResult").par("requestCode", requestCode).par("resultCode", resultCode);
        new RefreshMuralAsync(mainAct).execute();
    }

    @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
          dbg.method("onCreateContextMenu");
          super.onCreateContextMenu(menu, v, menuInfo);

          AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
          MuralPost mp = (MuralPost) listView.getItemAtPosition(info.position);
          dbg.var("mp",mp);
          
          if(isPostFromUser(mp))
          {
             MenuInflater m = mainAct.getMenuInflater();  
             m.inflate(R.menu.main_mural_context_menu, menu);
          }
          else
          {
             MenuInflater m = mainAct.getMenuInflater();
               m.inflate(R.menu.main_mural_context_menu_not_user, menu);
          }
      }

      @Override
       public boolean onContextItemSelected(MenuItem item) {

            dbg.var("item", item);
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

            switch(item.getItemId()){
                 case R.id.main_mural_ctxmenu_delete:
                 {
                     dbg.method("onContextItemSelected").log("Item selecionado");

                      MuralPost mp = (MuralPost) listView.getItemAtPosition(info.position);
                      dbg.var("mp",mp);
                      if(isPostFromUser(mp))
                      {
                          mainAct.deleteMuralPost(mp);
                          adapter.remove(info.position);
                          adapter.notifyDataSetChanged();
                      }

                   return true;
                 }
                 case R.id.main_mural_ctxmenu_not_user_translate:
                 {
                     currentPosition = info.position;

                    final MuralPost muralpost = (MuralPost) listView.getItemAtPosition(info.position);

                     showTranslateOptions(muralpost);

                    return true;
                 }
            }
            return super.onContextItemSelected(item);
       }

      private void showTranslateOptions(MuralPost pMuralpost) {
         final MuralPost muralpost = pMuralpost;

          String[] langs = getResources().getStringArray(R.array.def_choices_translate_languages);
          MessageHelper.getDialogWhitChoices(getActivity()
                                            , langs
                                            , new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                    case 0:
                                                        new AsyncMuralPostTranslate( muralpost
                                                                                   , Languages.PT
                                                                                   , Languages.EN).execute();
                                                        break;

                                                    case 1:
                                                        new AsyncMuralPostTranslate( muralpost
                                                                                   , Languages.EN
                                                                                   , Languages.PT).execute();

                                                        break;
                                                    }

                                                    dialog.dismiss();

                                                }
                                            }).setTitle(R.string.def_msg_choice_translate_title)
                                            .show();
      }

    @Override
      public void onStart() {
        super.onStart();
        dbg.method("onStart");
        refreshStart();

        Random gerador  =  new Random();
        if(gerador.nextInt(10) == 3)
            Router.gotoFeedback(getActivity());
      }

      @Override
      public void onStop() {
        dbg.method("onStop");
        super.onStop();
        try {
            refreshStop();
        } catch (InterruptedException e) {
            dbg.method("onStop").exception(e);
            e.printStackTrace();
        }
      }

      /*** METHODS ***/

      public void setPosts(List<MuralPost> pPostagens)
      {
          postagens = pPostagens;
          refreshListView();
      }

      public void refreshStop() throws InterruptedException
      {
          if(muralRefreshTimer!=null)
          {
              muralRefreshTask.cancel();
              muralRefreshTimer.cancel();
              muralRefreshTimer.purge();
          }
      }

      public void refreshStart()
      {
          int frequency = SettingsHelper.frequencyRefreshMural(mainAct);

          muralRefreshTimer = new Timer();
          muralRefreshTask  = new MuralRefreshTask();
          muralRefreshTimer.schedule( muralRefreshTask
                                    , frequency
                                    , frequency);
      }

      public void refreshListView()
      {
         if(listView!=null)
             if(adapter!=null)
              {
                  adapter.setItens(postagens);
                  adapter.notifyDataSetChanged();
              }
      }

      private boolean isPostFromUser(MuralPost pMuralPost)
      {
          return pMuralPost!=null && (pMuralPost.getUserId() == mainAct.user.getId());
      }

      private class MuralRefreshTask extends TimerTask
      {
        @Override
        public void run() {
            dbg.method("MuralRefreshTask.Run");
            try {
//                mainAct
//                .muralModel.asyncGetNearbyPosts( mainAct.locationProvider.getCurrentLatLng()
//                                                , SettingsHelper.getMuralPostDistance()
//                                                , refreshCallback);

                new RefreshMuralAsync(mainAct).execute();

            } catch (Exception e) {
                dbg.exception(e);
            }
        }

      }

      private class RefreshMuralAsync extends DefaultBackgroudWorker
      {
          Exception error;

        private RefreshMuralAsync(Activity pActivity) {
            super(pActivity);
        }

        @Override
        protected Object doInBackground(Object... params) {
            error = null;
            try {
                if(mainAct.user!=null)
                if(mainAct.user.isOnAPlace())
                    postagens = mainAct.muralModel.getPostsFromPlace(mainAct.user.getPlaceId());
                else
                    postagens =
                         mainAct.muralModel.getNearbyPosts( mainAct.user.getLocalization()
                                                          , SettingsHelper.getMuralPostDistance(Metric.MILLES));

            } catch (Exception e) {
                error = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if(error == null)
               refreshListView();
            else
            {
                if(error.getClass() == NetworkException.class||
                   error.getClass() == ServerException.class)
                {
                    MessageHelper.errorAlert(getActivity())
                                 .setMessage(error.getMessage())
                                 .showDialog();
                }else
                {
                    error.printStackTrace();
                    dbg.exception(error);
                }
            }
        }
      }

      public class RefreshMuralCallback extends DefaultWebCallback
      {
            List<MuralPost> posts;

            boolean inError;

            @Override
            public void onCompleteTask(Object pResult) {
                dbg._class(this).method("onCompleteTask").par("pResult", pResult);
                if(!inError)
                {
                  setPosts(posts);
                }
                else
                {
                    MessageHelper.errorAlert(getActivity())
                                 .setMessage(R.string.def_error_msg_ws_server_not_responding)
                                 .showDialog();
                }
            }
            
            @Override
            public void onPreComplete(Object pResult) {
                super.onCompleteTask(pResult);
                try {
                    String json = pResult.toString();
                    posts = mainAct.muralModel.getListPostsFromJSon(json);

                } catch (Exception e) {
                    dbg.exception(e);
                    inError = true;
                }
            }

        }

      private class PullToRefreshListener implements ListViewTouchEventListener
      {

          @Override
          public void onListViewPulledDown() {
              dbg.method("onListViewPulledDown");
          }

        }

      private class DownRefreshingListenter implements OnTouchListener
      {
              int itensLidos = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ListView lv = (ListView) v;

                if(postagens!=null)
                if(
                  (lv.isItemChecked(postagens.size()-1)
                || lv.isItemChecked(postagens.size()-2)
                || lv.isItemChecked(postagens.size()-3)
                )
                && event.getAction() == MotionEvent.ACTION_DOWN
                )
                {
                    itensLidos += 5;
                    dbg.var("itensLidos", itensLidos);
                }

                return false;
             }

      }

    private class ButtonStartChatOnClick implements OnClickListener
    {
        MuralPost muralPost;

        @Override
        public void onClick(View v) {
            int pos = (Integer) v.getTag();

            muralPost = postagens.get(pos);

            UserIvoke sessionUser = mainAct.getCommon().getSessionUser();

            if(sessionUser.getId() != postagens.get(pos).getUserId())
            {
                try {
                    if(xmppService.getConnection()!=null
                    && xmppService.getConnection().isConnected())
                       gotoChat();
                    else
                        xmppService.connection();

                } catch (Exception e) {
                    dbg.exception(e);
                    MessageHelper.infoAlert(mainAct)
                    .setMessage(R.string.main_mural_msg_error_add_buddy)
                    .showDialog();
                }
            }
        }

        private void gotoChat() throws XMPPException, IOException
        {
            String facebookId = muralPost.getFacebookId();
            String userName   = muralPost.getName();

            String JID = facebookId+"@"+getString(R.string.ws_xmpp_default_site);
            xmppService.addFriend(JID, userName, null);
            Account account = xmppService.getAccount(JID);
            Router.gotoChat(mainAct, account, muralPost.isAnonymous());
        }
    }

    private class ButtonOpenFacebookOnClick implements OnClickListener
    {
        MuralPost muralPost;

        @Override
        public void onClick(View v) {
            int pos = (Integer) v.getTag();

            muralPost = postagens.get(pos);

            UserIvoke sessionUser = mainAct.getCommon().getSessionUser();

            if(sessionUser.getId() != postagens.get(pos).getUserId()
            || !muralPost.isAnonymous())
            {
                try {

                    try {
                        final String url = "fb://profile/" + muralPost.getFacebookId();
                        Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivity(facebookAppIntent);
                    } catch (Exception e) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/"+muralPost.getFacebookId()));
                        startActivity(browserIntent);
                    }

                } catch (Exception e) {
                    dbg.exception(e);
                    MessageHelper.infoAlert(mainAct)
                    .setMessage(R.string.main_mural_msg_error_add_buddy)
                    .showDialog();
                }
            }
        }
    }

    private class ButtonTranslateListener implements OnClickListener
    {
        @Override
        public void onClick(View v) {

//            currentPosition = (Integer) v.getTag();
            currentPosition = listView.getPositionForView(v);
            MuralPost muralPost = postagens.get(currentPosition);

            showTranslateOptions(muralPost);

        }
    }


    private class AsyncMuralPostTranslate extends AsyncTask<String, Void, String>
    {
        MuralPost  muralPost;
        TranslateModel.Languages toLang;
        TranslateModel.Languages fromLang;

        public AsyncMuralPostTranslate(MuralPost pMuralpost, TranslateModel.Languages pFromLang, TranslateModel.Languages pToLang)
        {
            this.muralPost = pMuralpost;
            this.fromLang  = pFromLang;
            this.toLang    = pToLang;
        }

        @Override
        protected String doInBackground(String... params) {
            TranslateModel translateModel = new TranslateModel();

            String translated = "";

            try {
                translated = translateModel.translate(muralPost.getMessage(), fromLang, toLang);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return translated;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            muralPost.setTranslated(result);

            adapter.notifyDataSetChanged();

        }
    }

}
