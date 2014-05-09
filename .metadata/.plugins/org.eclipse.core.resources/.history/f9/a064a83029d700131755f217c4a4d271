package com.app.ivoke.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.ivoke.objects.WebParameter;
import com.app.ivoke.objects.interfaces.IAsyncCallBack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class WebHelper {

	String requestCache;
	
	DebugHelper debug = new DebugHelper("WebHelper");
	
	public String doRequest(String url, ArrayList<WebParameter> pParametros) throws NetworkException, ClientProtocolException, IOException
	{
		String urlConcat = "";
		for (WebParameter parameter : pParametros) {
			urlConcat += "/"+parameter.getValor();
		}		
		
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpGet httpget = new HttpGet(url+urlConcat); 
	    HttpResponse response;
	    
    	response = httpclient.execute(httpget);
	    HttpEntity entity = response.getEntity();
	    
	    if (entity != null) {
	        InputStream instream = entity.getContent();
	        requestCache = convertStreamToString(instream);
	        instream.close();
	    }
		
	    

	     return requestCache;
		   
	}
    
	public String doPostRequest(String url, ArrayList<WebParameter> pParametros) throws ServerException, NetworkException, Exception
	{
		debug.method("doPostRequest");
		ArrayList<NameValuePair> postParamtros = new ArrayList<NameValuePair>(2);
		for (WebParameter pParametro : pParametros) {
			debug.log("PARAMETROS: Key= "+pParametro.getKey()+" Valor="+pParametro.getValor());
			postParamtros.add(new BasicNameValuePair(pParametro.getKey(), pParametro.getValor()));
		}
		
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);
	    HttpResponse response = null;
    	httppost.setEntity(new UrlEncodedFormEntity(postParamtros)); 
    	response = httpclient.execute(httppost);
        
    	HttpEntity entity = response.getEntity();
        
    	if (entity != null) {
            InputStream instream = entity.getContent();
            requestCache = convertStreamToString(instream);
            instream.close();
        }
    	
    	if(requestCache.contains("[ERROR]"))
    	{
    		debug.log("ERRO SERVIDOR: "+requestCache);
    		throw new ServerException("Erro no servidor. Motivo:"+requestCache, null);
    	}
    	
	    return requestCache;
		   
	}
	
	public void doAsyncRequest(String url, ArrayList<WebParameter> pParametros, IAsyncCallBack pCallBack) throws NetworkException
	{
		debug.method("doAsyncPostRequest").par("url", url);
		 new AsyncRequest(this, pParametros, pCallBack).execute(url);
	}
	
	public void doAsyncPostRequest(String url, ArrayList<WebParameter> pParameters, IAsyncCallBack pCallBack)
	{
		 debug.method("doAsyncPostRequest").par("url", url);
		 new AsyncPostRequest(this, pParameters, pCallBack).execute(url);
	}
	
	public Bitmap getImageFromUrl(String pUrlPath) throws Exception
	{
		debug.method("getImageFromUrl").par("pUrlPath", pUrlPath);
		
		URL img_value = new URL(pUrlPath);
	    Bitmap image = null;
	    InputStream stream = img_value.openConnection().getInputStream();
	    image = BitmapFactory.decodeStream(stream);
	   
	    return image;
	}
	
	public JSONObject getJsonObjectFromUrl(String pUrl, ArrayList<WebParameter> pParametros) throws JSONException, NetworkException, ClientProtocolException, IOException
	{
		return new JSONObject(doRequest(pUrl, pParametros));
	}
	
	public JSONArray getJsonArrayFromUrl(String pUrl, ArrayList<WebParameter> pParametros) throws JSONException, NetworkException, ClientProtocolException, IOException
	{
		return new JSONArray(doRequest(pUrl, pParametros));
	}
	
    public String getRequestCache()
 	{
		return requestCache;
	}
	
	private static String convertStreamToString(InputStream is) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	public ArrayList<WebParameter> makeListParameter(String pKey, String pValor)
	{
		ArrayList<WebParameter> list = new ArrayList<WebParameter>();
		list.add(new WebParameter(pKey, pValor));
		return list;
	}
	
	public WebParameter parameter(String pKey, String pValor)
	{
		return new WebParameter(pKey, pValor);
	}
	
	public class NetworkException extends Exception
	{
		private Exception InnerException;
		private static final long serialVersionUID = 1L;
		
		public NetworkException(String pMensagem, Exception pEx)
		{
			super(pMensagem);
			this.InnerException = pEx;
		}
		
		public Exception getInnerException()
		{
			return InnerException;
		}
	}
	
	public class ServerException extends Exception
	{
		private Exception InnerException;
		private static final long serialVersionUID = 1L;
		
		public ServerException(String pMensagem, Exception pEx)
		{
			super(pMensagem);
			this.InnerException = pEx;
		}
		
		public Exception getInnerException()
		{
			return InnerException;
		}
	}
	
	public class AsyncRequest extends AsyncTask<String, Integer, String>
	{
		WebHelper    web;
		ArrayList<WebParameter> parameters;
		IAsyncCallBack callBack;
		
		public AsyncRequest(WebHelper pWeb, ArrayList<WebParameter> pParameters, IAsyncCallBack pCallBack)
		{
			web = pWeb;
			parameters = pParameters;
			callBack = pCallBack;
		}
		
		@Override
		protected String doInBackground(String... pUrl) {
			String requestResult = null;
					
			for (int i = 0; i < pUrl.length; i++) {
				try {
					
					requestResult = web.doRequest(pUrl[i], parameters);
					debug._class(this).method("doInBackground").var("requestResult", requestResult);
				} catch (NetworkException e) {
					requestResult = null;
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			callBack.onPreComplete(requestResult);
			
			return requestResult;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			callBack.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			callBack.onCompleteTask(result);
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			callBack.onProgress(values[0], null);
		}
		
	}
	
	public class AsyncPostRequest extends AsyncTask<String, Integer, String>
	{
		WebHelper    web;
		ArrayList<WebParameter> parameters;
		IAsyncCallBack callBack;
		
		public AsyncPostRequest(WebHelper pWeb, ArrayList<WebParameter> pParameters, IAsyncCallBack pCallBack)
		{
			web = pWeb;
			parameters = pParameters;
			callBack = pCallBack;
		}
		
		@Override
		protected void onPreExecute() {
			callBack.onPreExecute();
		};
		
		@Override
		protected String doInBackground(String... pUrl) {
			String requestResult = null;
					
			for (int i = 0; i < pUrl.length; i++) {
				try {
					requestResult = web.doPostRequest(pUrl[i], parameters);
					
				} catch (Exception e) {
					requestResult = null;
		        }
			}
			callBack.onPreComplete(requestResult);
			return requestResult;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			callBack.onCompleteTask(result);
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			callBack.onProgress(values[0], null);
		}
		
	}
}
