package com.app.ivoke.controllers.main;

import com.app.ivoke.R;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.objects.UserIvoke;

import android.app.Activity;
import android.os.Bundle;

public class ShowPostActivity extends Activity {

    DebugHelper dbg = new DebugHelper("ShowPostActivity");

    public static String PE_USER_IVOKE    = "ShowPostActivity.UserIvoke";
    public static String PE_MURAL_CONTENT = "ShowPostActivity.MuralContent";

    UserIvoke user;
    String    muralContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_show_post);

        Bundle extras = getIntent().getExtras();
        muralContent  = extras.getString(PE_MURAL_CONTENT);
        user          = (UserIvoke) extras.getSerializable(PE_USER_IVOKE);

            dbg.var("muralContent", muralContent)
               .var("user", user);
        
    }

}
