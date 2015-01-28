package com.app.ivoke.objects.interfaces;

public interface IAsyncCallBack {

    void onPreExecute();
    void onPreComplete(Object pResult);
    void onCompleteTask(Object pResult);
    void onProgress(int pPercent, Object pObject);
    void onError(String pMessage, Exception pEx);
}

