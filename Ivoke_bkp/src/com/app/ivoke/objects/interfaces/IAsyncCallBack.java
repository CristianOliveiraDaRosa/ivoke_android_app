package com.app.ivoke.objects.interfaces;

public interface IAsyncCallBack {
	
	public String error        = "";
	
	void onPreExecute();
	void onPreComplete(Object pResult);
	void onCompleteTask(Object pResult);
	void onProgress(int pPercent, Object pObject);
}
