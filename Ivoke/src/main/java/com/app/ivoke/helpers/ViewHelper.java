package com.app.ivoke.helpers;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ViewHelper {
	
	Activity activity;
	
	public ViewHelper(Activity pActivity)
	{
		activity = pActivity;
	}
	
	public void mensagem(String pMensagem)
	{
		Toast.makeText(activity, pMensagem, 5000).show();
	}
	
}
