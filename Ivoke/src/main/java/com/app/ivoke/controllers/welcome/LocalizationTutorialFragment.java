package com.app.ivoke.controllers.welcome;

import com.app.ivoke.R;
import com.app.ivoke.helpers.DeviceHelper;
import com.app.ivoke.helpers.SettingsHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class LocalizationTutorialFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_localization_fragment, container, false);

        Button btnGPS = (Button) view.findViewById(R.id.welcome_localization_btn_gps);
        btnGPS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceHelper.showGpsConfiguration(getActivity());
            }
        });

        Button btnNetwork = (Button) view.findViewById(R.id.welcome_localization_btn_network);
        btnNetwork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceHelper.showNetworkConfiguration(getActivity());
            }
        });

        WelcomeActivity welcomeAct = (WelcomeActivity) getActivity();
        Button btnNext = (Button) view.findViewById(R.id.welcome_localization_btn_next);
        btnNext.setOnClickListener(welcomeAct.getButtonNextListener());

        return view;
    }
}
