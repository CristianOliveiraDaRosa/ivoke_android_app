package com.app.ivoke.controllers.welcome;

import com.app.ivoke.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomeTutorialFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_main_fragment, container, false);

        WelcomeActivity welcomeAct = (WelcomeActivity) getActivity();
        Button btnNext = (Button) view.findViewById(R.id.welcome_main_btn_next);
        btnNext.setOnClickListener(welcomeAct.getButtonNextListener());

        return view;
    }
}
