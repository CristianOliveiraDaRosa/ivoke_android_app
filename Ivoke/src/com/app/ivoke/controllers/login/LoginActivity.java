package com.app.ivoke.controllers.login;

import com.app.ivoke.R;
import com.app.ivoke.controllers.checking.CheckActivity;
import com.app.ivoke.controllers.main.MainActivity;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.helpers.IvokeDebug;
import com.app.ivoke.helpers.ViewHelper;
import com.app.ivoke.helpers.WebHelper.NetworkException;
import com.app.ivoke.helpers.WebHelper.ServerException;
import com.app.ivoke.models.FacebookModel;
import com.app.ivoke.models.UsuarioModel;
import com.app.ivoke.objects.UsuarioIvoke;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;

public class LoginActivity extends android.support.v4.app.FragmentActivity {
	/* Declaração dos parametros (putExtra) desta Activity */
	public static String PE_FACEBOOK_SESSION = "FacebookSession";
	public static String PE_USUARIO_IVOKE   = "UsuarioIvoke";
	
	
	private Activity loginActivity    = this;
	private FacebookModel faceModel   = new FacebookModel();
	private UsuarioModel usuarioModel =  new UsuarioModel();
	private UsuarioIvoke usuario;
	private Intent checkingIntent;
	
	private Button btnAcessIvoke;
	private TextView lblFacebook;
	
	private com.facebook.widget.LoginButton btnFacebook;
	
	IvokeDebug debug = new IvokeDebug("LOGIN_ACTIVITY");
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		debug.log("INICIO");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		faceModel.openSessionAsync(this, new FacebookLoginCallBack());
		
	}
   
	public static class PlaceholderFragment extends Fragment {
     	@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.login_fragment, container, false);
			return rootView;
		}
	}
	
	public void OnClickListener(View pview)
	{
		callCheckingIntent();
	}
	
	private void callCheckingIntent()
	{
		if(usuario == null)
		{
			;
			ivokeLogin();
			return;
		}
		
		if (faceModel.hasSessionActive())
		{
			debug.log("Existe sessão");
			checkingIntent.putExtra(PE_FACEBOOK_SESSION, faceModel.getActiveSession());
			checkingIntent.putExtra(PE_USUARIO_IVOKE  , usuario);
			Log.d("LOGIN_ACTIVITY", "ANTES chamada Main activitity");
			startActivity(checkingIntent);
		}
		else
		{
			MessageHelper.getToastMessage(this, R.string.login_msg_session_error, "facebook").show();
			debug.log("Não existe sessão");
		}
		
	}
	
	public void ivokeLogin()
	{
	    String facebookID = faceModel.getFacebookUser().getId();
		
		try {
    		if(!usuarioModel.existe(facebookID))
				usuario = usuarioModel.create(faceModel.getFacebookUser().getName(),facebookID);
    		else
		    	usuario = usuarioModel.getUsuario();
        	
    		btnAcessIvoke.setText(getString(R.string.login_btn_login_ivoke_continue));
			
    	}
    	catch (Exception e) 
    	{
    		MessageHelper.getErrorAlert(this).setMessage(e.getMessage()).showDialog();
			
    		btnAcessIvoke.setText("Login Ivoke.");
			
			e.printStackTrace();
		}
	}
	
	public class FacebookLoginCallBack implements StatusCallback
	{
		@Override
		public void call(Session session, SessionState state, Exception exception) 
		{
			btnAcessIvoke = (Button) findViewById(R.id.login_button_login_ivoke);
			
			
			if(faceModel.requestUsuarioFacebook())
			{	
			    String nome    = faceModel.getFacebookUser().getName();
			
			    lblFacebook = (TextView) findViewById(R.id.login_mensagem_facebook_login);
			    lblFacebook.setText("Logado como: "+nome);
			    
			    ivokeLogin();
			}
			
			if(checkingIntent==null)
			{
				checkingIntent = new Intent(loginActivity, CheckActivity.class);
				callCheckingIntent();
			}
		}
		
		
	}
	
}
