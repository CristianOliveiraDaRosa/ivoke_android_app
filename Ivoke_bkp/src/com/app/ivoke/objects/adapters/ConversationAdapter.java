package com.app.ivoke.objects.adapters;

import java.util.List;
import com.app.ivoke.R;
import com.app.ivoke.objects.Contato;
import com.app.ivoke.objects.Conversation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConversationAdapter extends BaseAdapter {

	LayoutInflater lInflater;
	List<Conversation> conversations;
	int itemAtual;
	
    public ConversationAdapter(Context pContext, List<Conversation> pConversations) {
    	lInflater = LayoutInflater.from(pContext);
    	conversations     = pConversations;
	}

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return conversations.size();
	}

	@Override
	public Object getItem(int position) {
		return conversations.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listTemplateView;

		if((listTemplateView = convertView) == null)
			listTemplateView = lInflater.inflate(R.layout.main_contato_list_template
					                            , null);
	    
		TextView txtNome     = 
				(TextView) listTemplateView.findViewById(R.id.main_contato_titulo);
		TextView txtMensagem = 
				(TextView) listTemplateView.findViewById(R.id.main_contato_ultima_mensagem);
		TextView txtQuando = 
				(TextView) listTemplateView.findViewById(R.id.main_contato_quando);
		
		txtNome.setText(conversations.get(position).getUserOneNome());
		txtMensagem.setText(conversations.get(position).getLastMessage());
		txtQuando.setText(conversations.get(position).getTime());
		
		return listTemplateView;
		
	}
	
	class auxItemList{
		String nomeUsuario;
		String texto;
	}
}