package com.app.ivoke.models;

import java.util.ArrayList;
import org.json.JSONObject;

import android.util.Log;

import com.app.ivoke.helpers.WebHelper;
import com.app.ivoke.helpers.WebHelper.WebParametro;
import com.app.ivoke.objects.UsuarioIvoke;

public class UsuarioModel extends WebServer {
	
	private UsuarioIvoke usuario;

	public UsuarioIvoke getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioIvoke usuario) {
		this.usuario = usuario;
	}
	
	public boolean existe(String pFacebookId) throws Exception
	{
		ArrayList<WebHelper.WebParametro> parametros = new ArrayList<WebHelper.WebParametro>();
		parametros.add(web.newParametro("facebook_id", pFacebookId));
		
		String retorno = null;
		
		retorno = web.doPostRequest(getUrl(URL_USUARIO_GET), parametros);
		
		 try {
			 JSONObject json = new JSONObject(retorno);
			 usuario = new UsuarioIvoke();
			 usuario.setIvokeID(json.getInt("id"));
			 usuario.setNome(json.getString("nome"));
			 usuario.setFacebookID(json.getString("facebook_id"));
		 } catch (Exception e) {
			return false;
		 }
		 
		 return true;
		
	}
	
	public UsuarioIvoke create(String pNome, String pFacebookId) throws Exception
	{     
		UsuarioIvoke usuario = new UsuarioIvoke();
		try 
			{
				ArrayList<WebParametro>  parametros = new ArrayList<WebParametro>();
				parametros.add(web.newParametro("nome"       , pNome));
				parametros.add(web.newParametro("facebook_id", pFacebookId));
				
				String jsonString = web.doPostRequest(getUrl(URL_USUARIO_ADD), parametros);
				
				JSONObject json = new JSONObject(jsonString);
				
				Log.d("DEBUG", "RETORNO WEB "+jsonString);
				
				usuario.setIvokeID(json.getInt("id"));
				usuario.setNome(pNome);
				usuario.setFacebookID(pFacebookId);
				
				return usuario;
			} 
			catch (Exception e) 
			{
				Log.d("ERROR", "ERRO:"+e.getMessage());
				throw e;
			}	
	}
}
