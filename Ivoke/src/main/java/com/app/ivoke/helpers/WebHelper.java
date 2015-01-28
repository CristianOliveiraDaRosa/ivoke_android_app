package com.app.ivoke.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.ivoke.Common;
import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.objects.WebParameter;
import com.app.ivoke.objects.defaults.DefaultWebCallback;
import com.app.ivoke.objects.interfaces.IAsyncCallBack;
import com.google.android.gms.internal.dg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class WebHelper {

    String requestCache;

    DebugHelper debug = new DebugHelper("WebHelper");

    private HttpClient getClient()
    {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, getEncoding());

        return new DefaultHttpClient(params);
    }

    public String getEncoding()
    {
        return "UTF-8";
    }

    public String doRequest(String url, ArrayList<WebParameter> pParametros) throws NetworkException, ClientProtocolException, IOException, ServerException
    {
        if(DeviceHelper.hasInternetConnection())
        {
            String urlConcat = "";
            for (WebParameter parameter : pParametros) {
                urlConcat += "/"+parameter.getValor();
            }

            HttpClient httpclient = getClient();
            HttpGet httpget = new HttpGet(url+urlConcat);
            HttpResponse response;

            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                requestCache = convertStreamToString(instream);

                try {
                    instream.close();
                } catch (Exception e) {
                    debug.method("doRequest").exception(e);
                }
            }

            if(requestCache.contains("{\"exception\":"))
            {
                debug.log("ERRO SERVIDOR: "+requestCache);
                throw new ServerException(Router.previousContext.getString(R.string.def_error_msg_ws_server_not_responding), null);
            }
        }else
        {
            throw new NetworkException(Common.appContext.getString(R.string.def_error_msg_whitout_internet_connection));
        }

        return requestCache;

    }
    
    public String doPostRequest(String url, ArrayList<WebParameter> pParametros) throws ServerException, NetworkException, Exception
    {
        if(DeviceHelper.hasInternetConnection())
        {
            HttpClient httpclient = getClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = null;

            debug.method("doPostRequest");
            if(pParametros!=null)
            {
                ArrayList<NameValuePair> postParamtros = new ArrayList<NameValuePair>(2);
                for (WebParameter pParametro : pParametros) {
                    debug.log("PARAMETROS: Key= "+pParametro.getKey()+" Valor="+pParametro.getValor());
                    postParamtros.add(new BasicNameValuePair(pParametro.getKey(), pParametro.getValor()));
                }
                httppost.setEntity(new UrlEncodedFormEntity(postParamtros, getEncoding()));
            }

            response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                requestCache = convertStreamToString(instream);
                instream.close();
            }

            if(requestCache.contains("{\"exception\":"))
            {
                debug.log("ERRO SERVIDOR: "+requestCache);
                throw new ServerException(Router.previousContext.getString(R.string.def_error_msg_ws_server_not_responding), null);
            }
        }
        else
        {
            throw new NetworkException(Common.appContext.getString(R.string.def_error_msg_whitout_internet_connection));
        }
        debug.var("requestCache",requestCache);
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

    public ArrayList<WebParameter> makeListParameter(String pKey, Object pValor)
    {
        ArrayList<WebParameter> list = new ArrayList<WebParameter>();
        list.add(new WebParameter(pKey, pValor.toString()));
        return list;
    }

//    public WebParameter parameter(String pKey, String pValor)
//    {
//        return new WebParameter(pKey, pValor);
//    }
//
    public WebParameter parameter(String pKey, Object pValor)
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

        public NetworkException(String string) {
            // TODO Auto-generated constructor stub
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
                } catch (Exception e) {
                    callBack.onError("URL: "+pUrl[i], e);
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
            if(callBack!=null)
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
                    if(callBack!=null)
                    callBack.onError("URL: "+pUrl[i], e);
                }
            }
            if(callBack!=null)
            callBack.onPreComplete(requestResult);
            return requestResult;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(callBack!=null)
            callBack.onCompleteTask(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(callBack!=null)
            callBack.onProgress(values[0], null);
        }

    }

}
