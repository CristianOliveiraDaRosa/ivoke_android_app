package com.app.ivoke.controllers.main;

import java.util.ArrayList;
import java.util.List;

import com.app.ivoke.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.app.ivoke.objects.*;
import com.app.ivoke.objects.adapters.MuralAdapter;

public class MuralFragment extends Fragment {
	  MuralAdapter adapter;
	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.main_mural_fragment, container, false);
	    
	    List<PostMural> postagens = new ArrayList<PostMural>();
	    postagens.add(new PostMural( "Exterminador"
	    		                  , "I'm back."
	    		                  , "a 2 min."
	    		                  , 0));
	    
	    postagens.add(new PostMural( "James Bond"
                , "My name is Bond, James Bond."
                , "a 1 hora."
                , 0));
	    
	    postagens.add(new PostMural( "Exterminador"
                , "I will be back."
                , "a 30 min."
                , 0));
	    
	    ListView listView = (ListView) view.findViewById(R.id.muralListView);
	    adapter = new MuralAdapter(view.getContext(), postagens);
	    listView.setAdapter(adapter);
	    
	    return view;
	  }
}
