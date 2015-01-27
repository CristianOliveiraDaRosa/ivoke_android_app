package com.app.ivoke.controllers.main;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.app.ivoke.R;
import com.app.ivoke.Router;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.app.ivoke.controllers.main.MainActivity.MuralCallback;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.LocationHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.SettingsHelper;
import com.app.ivoke.models.MuralModel;
import com.app.ivoke.objects.*;
import com.app.ivoke.objects.adapters.MuralAdapter;
import com.app.ivoke.objects.defaults.DefaultWebCallback;
import com.app.ivoke.objects.interfaces.IAsyncCallBack;
import com.google.android.gms.maps.model.LatLng;

public class MuralFragment extends Fragment {
      DebugHelper dbg = new DebugHelper("MuralFragment");

      MainActivity          mainAct;
      MuralAdapter          adapter;
      RefreshMuralCallback  refreshCallback = new RefreshMuralCallback();
      Timer                 muralRefreshTimer;
      MuralRefreshTask      muralRefreshTask;

      List<MuralPost> postagens;

      View     fragmentView;
      ListView listView;
      Button   btnPostar;
      boolean  btnPostarBusy;
      EditText txtPost;


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbg.method("onCreateView");
        fragmentView = inflater.inflate(R.layout.main_mural_fragment, container, false);

        mainAct  = (MainActivity) getActivity();
        listView = (ListView) fragmentView.findViewById(R.id.main_mural_posts_list);
        adapter  = new MuralAdapter(fragmentView.getContext(), postagens);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        btnPostar = (Button) fragmentView.findViewById(R.id.main_mural_create_post_button);
        txtPost   = (EditText) fragmentView.findViewById(R.id.main_mural_create_post_text);

        setBtnPostarListener();


        return fragmentView;
      }

      @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
          dbg.method("onCreateContextMenu");
          super.onCreateContextMenu(menu, v, menuInfo);

          MuralPost mp = (MuralPost) listView.getSelectedItem();
          dbg.var("mp",mp);
          
          MenuInflater m = mainAct.getMenuInflater();
          m.inflate(R.menu.main_mural_context_menu, menu);
      }

      @Override
       public boolean onContextItemSelected(MenuItem item) {

            dbg.var("item", item);

            switch(item.getItemId()){
                 case R.id.main_mural_ctxmenu_delete:
                      dbg.method("onContextItemSelected").log("Item selecionado");
                      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

                      MuralPost mp = (MuralPost) listView.getItemAtPosition(info.position);
                      dbg.var("mp",mp);
                      if(mp!=null)
                      {
                          mainAct.deleteMuralPost(mp);
                          adapter.remove(info.position);
                          adapter.notifyDataSetChanged();
                      }
                 return true;
            }
            return super.onContextItemSelected(item);
       }

      @Override
      public void onStart() {
        super.onStart();
        dbg.method("onStart");
        refreshStart();
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

      private class MuralRefreshTask extends TimerTask
      {
         public boolean inErro;

        @Override
        public void run() {
            dbg.method("MuralRefreshTask.Run");
            try {
                mainAct
                .muralModel.asyncGetNearbyPosts( mainAct.locationProvider.getCurrentLatLng()
                                                , SettingsHelper.getMuralPostDistance(mainAct)
                                                , refreshCallback);
            } catch (Exception e) {
                dbg.exception(e);
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
                try {
                    String json = pResult.toString();
                    posts = mainAct.muralModel.getListPostsFromJSon(json);

                } catch (Exception e) {
                    dbg.exception(e);
                    inError = true;
                }
            }

        }

      private void setBtnPostarListener()
      {
          btnPostar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnPostarBusy){
                    MessageHelper.infoAlert(mainAct)
                                 .setMessage(R.string.main_mural_btn_create_is_busy)
                                 .showDialog();
                }
                else
                {
                  mainAct.postMuralPost(txtPost.getText().toString(), new PostMuralCallBack());
                }
            }
        });
      }

      private class PostMuralCallBack extends DefaultWebCallback
      {
            @Override
            public void onPreExecute() {
                btnPostar.setText(mainAct.getString(R.string.main_mural_btn_create_posting));
                btnPostarBusy = true;
            }

            @Override
            public void onCompleteTask(Object pResult) {
                dbg._class(this).method("onCompleteTask").par("pResult", pResult);
                btnPostar.setText(mainAct.getString(R.string.main_mural_btn_create_post));
                txtPost.setText("");
                btnPostarBusy = false;

                muralRefreshTask.run();
            }

      }
}
