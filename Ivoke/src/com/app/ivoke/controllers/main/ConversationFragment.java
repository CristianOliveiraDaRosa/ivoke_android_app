package com.app.ivoke.controllers.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.app.ivoke.objects.adapters.ConversationAdapter;
import com.app.ivoke.objects.adapters.MuralAdapter;

public class ConversationFragment extends Fragment {
	  ConversationAdapter adapter;
	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.main_contato_fragment, container, false);
	    
	    List<Contato> list = new ArrayList<Contato>();
		
	    try {
		 list.add(new Contato("Exterminador"
	    		            ,"Eae cara"
	    		            ,0
	    		            ,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-07-14 09:00:02")));
	    
	    list.add(new Contato("Gostosa da Praça"
	            ,"Me liga!"
	            ,0
	            ,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-07-14 09:00:02")));
	    
	    list.add(new Contato("Silvestrer Stalone"
	            ,"Bla bla bla Bla bla bla Bla bla bla Bla bla blaBla bla bla Bla bla blaBla bla bla Bla bla blaBla bla bla Bla bla blaBla bla bla Bla bla blaBla bla bla Bla bla blaBla bla bla Bla bla bla"
	            ,0
	            ,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2010-07-14 09:00:02")));
	    
	    	list.add(new Contato("Loira"
			        ,"Sai fora cara!"
			        ,0
			        ,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2010-07-14 09:00:02")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    ListView listView = (ListView) view.findViewById(R.id.muralListView);
	    adapter = new ConversationAdapter(view.getContext(), list);
	    listView.setAdapter(adapter);
	    
	    return view;
	  }
}
