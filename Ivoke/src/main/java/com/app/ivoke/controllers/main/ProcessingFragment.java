package com.app.ivoke.controllers.main;

import com.app.ivoke.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProcessingFragment extends Fragment {

    String messageProcessing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_processing_fragment, container, false);

        TextView lblProcess = (TextView) view.findViewById(R.id.main_processing_progress_lbl);

        if(lblProcess!=null && messageProcessing != null)
            lblProcess.setText(messageProcessing);

        return view;
    }

    public void setMessageProgress(String pMessage)
    {
        messageProcessing = pMessage;
    }
}
