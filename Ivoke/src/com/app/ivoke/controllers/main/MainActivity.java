package com.app.ivoke.controllers.main;

import com.app.ivoke.R;
import com.app.ivoke.models.FacebookModel;
import com.app.ivoke.objects.UserIvoke;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.Session;

public class MainActivity extends ActionBarActivity {
    
	public static final String PE_USER_IVOKE       = "MainActivity.UserIvoke";
	public static final String PE_FACEBOOK_SESSION = "MainActivity.FacebookSession";
	
	FacebookModel faceModel;
	UserIvoke user;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	Session session = 
        			(Session) extras.getSerializable(PE_FACEBOOK_SESSION);
        	
        	faceModel = new FacebookModel(this, session);
        	user   = (UserIvoke) extras.getSerializable(PE_USER_IVOKE);
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.main_act_menu_configuracoes) {
            return true;
        }
        
        switch (id) {
		case R.id.main_act_menu_mural:
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.container, new MuralFragment()).commit();
            break;
		case R.id.main_act_menu_contatos:
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.container, new ContatoFragment()).commit();
			break;
		default:
			break;
		}
        
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_mural_fragment, container, false);
            return rootView;
        }
    }

}
