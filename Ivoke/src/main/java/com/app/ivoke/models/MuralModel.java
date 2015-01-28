package com.app.ivoke.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.SettingsHelper;
import com.app.ivoke.helpers.WebHelper.NetworkException;
import com.app.ivoke.helpers.WebHelper.ServerException;
import com.app.ivoke.objects.WebParameter;
import com.app.ivoke.objects.MuralPost;
import com.app.ivoke.objects.UserIvoke;
import com.app.ivoke.objects.defaults.DefaultWebCallback;
import com.app.ivoke.objects.interfaces.IAsyncCallBack;
import com.google.android.gms.maps.model.LatLng;

public class MuralModel extends WebServer {

    DebugHelper dbg = new DebugHelper("MuralModel");

    public List<MuralPost> getNearbyPosts(LatLng pLatLng, float pDistance) throws ClientProtocolException, JSONException, NetworkException, IOException, ServerException
    {
        dbg.method("getNearbyPosts")
           .par("pLatLng", pLatLng)
           .par("pDistance", pDistance);

        List<MuralPost> listMuralPosts = new ArrayList<MuralPost>();
        String latLngDistance = String.valueOf(pLatLng.latitude).replace(".",",") + ";"
                                + String.valueOf(pLatLng.longitude).replace(".",",") + ";"
                                + String.valueOf(pDistance).replace(".",",");

        int anonymous = 0;

        if(SettingsHelper.showAnonymousPosts())
            anonymous = 1;

        ArrayList<WebParameter> parameters = new ArrayList<WebParameter>();
        parameters.add(web.parameter("lat_lng_distance", latLngDistance));
        parameters.add(web.parameter("anonymous"       , anonymous));
        String resultAsJson = web.doRequest(site(R.string.ws_url_mural_posts_get_nearby), parameters);
        dbg.var("resultAsJson", resultAsJson);
        JSONArray jsonMuralPosts = new JSONArray(resultAsJson);
        dbg.var("jsonMuralPosts", jsonMuralPosts);
         for (int i = 0; i < jsonMuralPosts.length() ; i++) {
             JSONObject muralPostJson = jsonMuralPosts.getJSONObject(i);

             JSONObject userJson =  muralPostJson.getJSONObject("user");

             listMuralPosts.add(
                     new MuralPost( muralPostJson.getInt("id")
                                  , userJson.getInt("id")
                                  , userJson.getString("name")
                                  , muralPostJson.getString("message")
                                  , muralPostJson.getString("created_at")
                                  , muralPostJson.getDouble("distance")
                                  , userJson.getString("facebook_id")
                                  , muralPostJson.getString("anonymous")));
         }

        return listMuralPosts;
    }

    public List<MuralPost> getPostsFromPlace(String pPlaceId) throws ServerException, NetworkException, JSONException ,Exception
    {
        dbg.method("getNearbyPosts")
           .par("pPlaceId", pPlaceId);

        List<MuralPost> listMuralPosts = new ArrayList<MuralPost>();

        int anonymous = 0;

        if(SettingsHelper.showAnonymousPosts())
            anonymous = 1;

        ArrayList<WebParameter> parameters = new ArrayList<WebParameter>();
        parameters.add(web.parameter("place_id"       , pPlaceId));
        parameters.add(web.parameter("anonymous"       , anonymous));

        JSONArray jsonMuralPosts =
                  new JSONArray(web.doPostRequest(site(R.string.ws_url_mural_posts_from_place)
                               , parameters));

         for (int i = 0; i < jsonMuralPosts.length() ; i++) {
             JSONObject muralPostJson = jsonMuralPosts.getJSONObject(i);

             JSONObject userJson =  muralPostJson.getJSONObject("user");

             listMuralPosts.add(
                     new MuralPost( muralPostJson.getInt("id")
                                  , userJson.getInt("id")
                                  , userJson.getString("name")
                                  , muralPostJson.getString("message")
                                  , muralPostJson.getString("created_at")
                                  , 0//muralPostJson.getDouble("distance")
                                  , userJson.getString("facebook_id")
                                  , muralPostJson.getString("anonymous")));

         }

        return listMuralPosts;
    }

    public void asyncGetNearbyPosts(LatLng pLatLng, float pDistance, IAsyncCallBack pCallback) throws Exception
    {
        try {
            dbg.method("getNearbyPosts")
               .par("pLatLng", pLatLng)
               .par("pDistance", pDistance);

              String latLngDistance = String.valueOf(pLatLng.latitude).replace(".",",") + ";"
                                    + String.valueOf(pLatLng.longitude).replace(".",",") + ";"
                                    + String.valueOf(pDistance).replace(".",",");

              int anonymous = 0;

                if(SettingsHelper.showAnonymousPosts())
                    anonymous = 1;

              ArrayList<WebParameter> parameters = new ArrayList<WebParameter>();
                parameters.add(web.parameter("lat_lng_distance", latLngDistance));
                parameters.add(web.parameter("anonymous"       , anonymous));

              web.doAsyncRequest(site(R.string.ws_url_mural_posts_get_nearby),
                                 parameters,
                                 pCallback);
        } catch (Exception e) {
            throw new Exception(Router.previousContext.getString(R.string.def_error_msg_ws_server_not_responding));
        }
    }

    public List<MuralPost> getListPostsFromJSon(String pJsonString) throws Exception
    {
        dbg.method("getListPostsFromJSon").par("pJsonString", pJsonString);

        List<MuralPost> listMuralPosts = new ArrayList<MuralPost>();

        if(pJsonString !=null)
        {
            JSONArray jsonMuralPosts = new JSONArray(pJsonString);

             for (int i = 0; i < jsonMuralPosts.length() ; i++) {
                 JSONObject muralPostJson = jsonMuralPosts.getJSONObject(i);

                 JSONObject userJson =  muralPostJson.getJSONObject("user");

                 listMuralPosts.add(
                         new MuralPost( muralPostJson.getInt("id")
                                      , userJson.getInt("id")
                                      , userJson.getString("name")
                                      , muralPostJson.getString("message")
                                      , muralPostJson.getString("created_at")
                                      , muralPostJson.getDouble("distance")
                                      , userJson.getString("facebook_id")
                                      , muralPostJson.getString("anonymous")));

             }

                 dbg.log("Cast sucess.");
         }
        return listMuralPosts;
    }

    public boolean createMuralPost(UserIvoke pUser, String pMessage, boolean isAnonymous, DefaultWebCallback pCallback)
    {
        int anonymous = 0;
         if (isAnonymous)
             anonymous = 1;
        //:usuario_id, :from, :latitude, :longitude, :message, :posted_at
        if(pMessage.length() > 0)
        try {
            ArrayList<WebParameter> parameters = new ArrayList<WebParameter>();

            parameters.add(new WebParameter("user_id"    , pUser.getId()));
            parameters.add(new WebParameter("latitude"   , pUser.getLocalization().latitude));
            parameters.add(new WebParameter("longitude"  , pUser.getLocalization().longitude));
            parameters.add(new WebParameter("message"    , pMessage));
            parameters.add(new WebParameter("anonymous"  , anonymous));

            if(pUser.isOnAPlace())
            {
                parameters.add(new WebParameter("place_id"  , pUser.getPlaceId()));
                web.doAsyncPostRequest(site(R.string.ws_url_mural_posts_create_on_place), parameters, pCallback);
            }else
                web.doAsyncPostRequest(site(R.string.ws_url_mural_posts_create), parameters, pCallback);

        } catch (Exception e) {
            dbg.exception(e);
            return false;
        }

        return true;
    }

    public boolean deleteMuralPost(MuralPost pMuralPost) {

        try {
            ArrayList<WebParameter> parameters = new ArrayList<WebParameter>();
            parameters.add(new WebParameter("id"    , pMuralPost.getId()));

            web.doAsyncPostRequest(site(R.string.ws_url_mural_posts_delete), parameters, new DefaultWebCallback());

            return true;

        } catch (Exception e) {
            dbg.exception(e);
            return false;
        }

    }
}
