package com.app.ivoke.controllers.welcome;

import com.app.ivoke.R;
import com.app.ivoke.Router;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("ValidFragment")
public class SettingsTutorialFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_settings_fragment, container, false);

        Button btnSettings = (Button) view.findViewById(R.id.welcome_settings_btn_settings);
        btnSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.gotoSettings(getActivity());
            }
        });

        WelcomeActivity welcomeAct = (WelcomeActivity) getActivity();
        Button btnNext = (Button) view.findViewById(R.id.welcome_settings_btn_next);
        btnNext.setOnClickListener(welcomeAct.getButtonNextListener());

        return view;
    }
}
