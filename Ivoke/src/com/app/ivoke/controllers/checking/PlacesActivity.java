package com.app.ivoke.controllers.checking;

import com.app.ivoke.R;
import com.facebook.model.GraphPlace;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PickerFragment.OnSelectionChangedListener;
import com.facebook.widget.PlacePickerFragment;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

public class PlacesActivity extends ActionBarActivity {
	
	public static String PE_JSON_SELECTED_PLACE = "com.app.ivoke.PlacesActivity.SELECTED_PLACE";
	
	PlacePickerFragment fmFacebookPlaces;
	GraphPlace localSelecionado;
	Intent returnIntent;
	
	
	OnSelectionChangedListener selectListener = new OnSelectionChangedListener() {
		@Override
		public void onSelectionChanged(PickerFragment<?> fragment) {
			localSelecionado = fmFacebookPlaces.getSelection();
			
			returnIntent = new Intent();
			returnIntent.putExtra(PE_JSON_SELECTED_PLACE, localSelecionado.getInnerJSONObject().toString());
			
			setResult( CheckActivity.PE_RESULT_PLACE_ACT, returnIntent);
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places_activity);
		
		FragmentManager fm = getSupportFragmentManager();
		fmFacebookPlaces = 
				(PlacePickerFragment) fm.findFragmentById(R.id.check_pickplaces_lista_locais);
		
		if (savedInstanceState == null) {
			fmFacebookPlaces.setSettingsFromBundle(getIntent().getExtras());
        }
		
		fmFacebookPlaces.setOnSelectionChangedListener(selectListener);
		
	}
	
}
