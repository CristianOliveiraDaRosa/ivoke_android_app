package com.app.ivoke.controllers.about;

import java.util.ArrayList;

import com.app.ivoke.R;
import com.app.ivoke.R.id;
import com.app.ivoke.R.layout;
import com.app.ivoke.R.menu;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.WebHelper;
import com.app.ivoke.objects.WebParameter;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FeedBackActivity extends ActionBarActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        setTitle(R.string.feedback_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements OnClickListener {

        public FeedBackActivity act;
        TextView txtMessage;
        TextView txtSuggest;

        public PlaceholderFragment() {
            act = (FeedBackActivity) getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_feed_back,
                    container, false);

            Button btnSend = (Button) rootView.findViewById(R.id.feedback_button_send);
            btnSend.setOnClickListener(this);
            txtMessage = (TextView) rootView.findViewById(R.id.feedback_txt_message);
            txtSuggest = (TextView) rootView.findViewById(R.id.feedback_txt_suggest);

            return rootView;
        }

        @Override
        public void onClick(View v) {

            if(txtMessage.getText().equals(null) && txtSuggest.getText().equals(null))
                getActivity().finish();

            WebHelper web = new WebHelper();

             ArrayList<com.app.ivoke.objects.WebParameter> par = new ArrayList<com.app.ivoke.objects.WebParameter>();

             par.add(new WebParameter("message", txtMessage.getText()));
             par.add(new WebParameter("suggestion", txtSuggest.getText()));

             web.doAsyncPostRequest(getString(R.string.ws_url) + getString(R.string.ws_url_feedback), par, null);

             MessageHelper.infoAlert(getActivity())
                          .setMessage(R.string.feedback_thank_you)
                          .setButtonOk(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        }).showDialog();
        }
    }

}
