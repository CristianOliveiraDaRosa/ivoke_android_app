package com.app.ivoke.objects.defaults;

import com.app.ivoke.objects.interfaces.IAsyncCallBack;

public class DefaultWebCallback implements IAsyncCallBack {

    private String urlError;
    private Exception exception;
    private boolean isRunning;

    @Override
    public void onPreExecute() {
        isRunning = true;
    }

    @Override
    public void onPreComplete(Object pResult) {    }

    @Override
    public void onCompleteTask(Object pResult) {
        isRunning = false;
    }

    @Override
    public void onProgress(int pPercent, Object pObject) {}

    public String getUrlError() {
        return urlError;
    }

    public void setUrlError(String errorMsg) {
        this.urlError = errorMsg;
    }

    @Override
    public void onError(String pMessage, Exception pEx) {
        setUrlError(pMessage);
        setException(pEx);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
