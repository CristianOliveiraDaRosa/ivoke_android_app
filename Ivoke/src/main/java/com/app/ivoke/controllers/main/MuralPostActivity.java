package com.app.ivoke.controllers.main;

import com.app.ivoke.Common;
import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.models.MuralModel;
import com.app.ivoke.objects.defaults.DefaultBackgroudWorker;
import com.app.ivoke.objects.defaults.DefaultWebCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MuralPostActivity extends ActionBarActivity {

    Button btnPostar;
    boolean btnPostarBusy;
    CheckBox chxIsAnonymous;
    EditText txtPost;

    MuralModel mural = new MuralModel();
    Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mural_post_activity);

        btnPostar      = (Button)   findViewById(R.id.main_mural_create_post_button);
        chxIsAnonymous = (CheckBox) findViewById(R.id.main_mural_is_anonymous);
        txtPost        = (EditText) findViewById(R.id.main_mural_create_post_text);
        common = (Common) getApplication();

        setButtonPostar_OnClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_help) {

            MessageHelper.showHelp(this, R.string.help_mural_post_lbl_how_works_desc);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setButtonPostar_OnClick()
      {
          btnPostar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!btnPostarBusy)
                {
                    try {
                        new MuralPostAsync(Router.previousContext).execute();
                    } catch (Exception e) {
                        new DebugHelper(this).exception(e);
                        MessageHelper.errorAlert(getBaseContext())
                                     .setMessage(R.string.def_error_msg_ws_server_not_responding)
                                     .showDialog();
                    }

                }
            }
        });
      }

    private class MuralPostAsync extends DefaultBackgroudWorker
    {
        public MuralPostAsync(Activity pActivity) {
            super(pActivity);
        }

        @Override
        protected void onPreExecute() {
            btnPostar.setText(R.string.main_mural_btn_create_posting);
            btnPostarBusy = true;
            chxIsAnonymous.setEnabled(!btnPostarBusy);
        }

        @Override
        protected Object doInBackground(Object... params) {

            mural.createMuralPost( common.getSessionUser()
                                 , txtPost.getText().toString()
                                 , chxIsAnonymous.isChecked()
                                 , new DefaultWebCallback());

            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            btnPostar.setText(R.string.main_mural_btn_create_post);
            btnPostarBusy = false;
            chxIsAnonymous.setEnabled(!btnPostarBusy);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",1);
            setResult(RESULT_OK,returnIntent);
            finish();
        }
    }
}
