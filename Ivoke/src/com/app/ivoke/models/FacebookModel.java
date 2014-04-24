package com.app.ivoke.models;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphUser;


public class FacebookModel {
 
	private GraphUser	 usuarioFacebook;
	private Session 	 activeSession;
	private SessionState statusSession;
 	
	public FacebookModel(){}
	public FacebookModel(Activity pActivity, Session pActiveSession)
	{
		setSessaoAtiva(pActivity,pActiveSession);
	}
	
	public boolean openSessionAsync(Activity pActivity, com.facebook.Session.StatusCallback pCallBack)
	{
		try {
			
			Session session = Session.openActiveSession(pActivity, true, new StatusCallback() {
				public void call(Session session, SessionState state, Exception exception) {
					if (session.isOpened())
					{
						activeSession = session;
						statusSession = state;
					};
				}
			});
			
			if(pCallBack!=null)
			{
				session.addCallback(pCallBack);
			}
			
			return true;
		} catch (Exception e) {
			Toast.makeText(pActivity.getApplicationContext(), 
						   "Não foi possível logar ao Facebook.", 
					       2000).show();
			Log.d("FacebookModel0001","Erro ao abrir sessao:"+e.getMessage());
			return false;
		}
	}
	
	public boolean requestUsuarioFacebook()
	{
		Request.newMeRequest(activeSession, new GraphUserCallback() {
			public void onCompleted(GraphUser user, Response response) {
				usuarioFacebook = user;
			}
		}).executeAndWait();
		
		return usuarioFacebook!=null;
	}
	
	public GraphUser getFacebookUser()
	{	
		if(usuarioFacebook == null)
		{
			if(requestUsuarioFacebook())
			   return usuarioFacebook;
			else
			   return null;
		}
		else
		{
			return usuarioFacebook;
		}
	}
	
	public Session getActiveSession()
	{
		return activeSession;
	}
	
	public void setSessaoAtiva(Activity pActivity,Session pSession)
	{
		   Session.setActiveSession(pSession);
		   activeSession = pSession;
		   Request.newMeRequest(pSession, new GraphUserCallback() {
				public void onCompleted(GraphUser user, Response response) {
					usuarioFacebook = user;
				}
			}).executeAsync();
	}
	
	public SessionState getStatusSessao()
	{
		return statusSession;
	}

	public boolean hasSessionActive()
	{
		return activeSession!=null;
	}
}
