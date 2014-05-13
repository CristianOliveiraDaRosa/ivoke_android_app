package com.app.ivoke.objects.defaults;

import com.app.ivoke.objects.interfaces.IAsyncCallBack;

public class DefaultWebCallback implements IAsyncCallBack {

	private String urlError;
	private Exception exception;
	
	@Override
	public void onPreExecute() { }

	@Override
	public void onPreComplete(Object pResult) {	}

	@Override
	public void onCompleteTask(Object pResult) { }

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

}
