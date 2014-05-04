package com.app.ivoke.objects.interfaces;

public interface IAsyncCallBack {
	void onPreExecure();
	void onCompleteTask(Object pResult);
	void onProgress(int pPercent, Object pObject);
}

