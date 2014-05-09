package com.app.ivoke.objects.adapters;

import java.util.Date;
import java.util.List;

import com.app.ivoke.R;
import com.app.ivoke.helpers.DateTimeHelper;
import com.app.ivoke.objects.MuralPost;
import com.facebook.widget.ProfilePictureView;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MuralAdapter extends BaseAdapter {

	LayoutInflater lInflater;
	List<MuralPost> itens;
	Context context;
	int itemAtual;
	
    public MuralAdapter(Context pContext, List<MuralPost> pItens) {
    	lInflater = LayoutInflater.from(pContext);
    	itens     = pItens;
    	context   = pContext; 
	}

    @Override
    public long getItemId(int position) {
      return itens.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

	@Override
	public int getCount() {
		return itens!=null? itens.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return itens!=null? itens.get(position) : null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listTemplateView;

		if((listTemplateView = convertView) == null)
			listTemplateView = lInflater.inflate(R.layout.main_mural_list_template, null);
	    
		ProfilePictureView profilePictureView = 
				(ProfilePictureView) listTemplateView.findViewById(R.id.main_mural_image);
	    profilePictureView.setProfileId(itens.get(position).getFacebookId());
	    
		TextView txtNome     = 
				(TextView) listTemplateView.findViewById(R.id.main_mural_title);
		TextView txtMensagem = 
				(TextView) listTemplateView.findViewById(R.id.main_mural_message);
		TextView txtQuando = 
				(TextView) listTemplateView.findViewById(R.id.main_mural_when);
		
		
		txtNome.setText(itens.get(position).getNome());
		txtMensagem.setText(itens.get(position).getMessage());
		Date dt = itens.get(position).getDatePost();
		Date now = new Date();
		
		long minutes = DateTimeHelper.getMinutesBetween(now, dt);
		
		if(DateTimeHelper.getDaysFromMinutes(minutes)>1)
			txtQuando.setText(String.format(context.getString(R.string.def_description_days), DateTimeHelper.getDaysFromMinutes(minutes)));
		else if(DateTimeHelper.getHoursFromMinutes(minutes)>1)
			txtQuando.setText(String.format(context.getString(R.string.def_description_hours), DateTimeHelper.getHoursFromMinutes(minutes)));
		else
			txtQuando.setText(String.format(context.getString(R.string.def_description_minutes), minutes));
		
		return listTemplateView;
		
	}
	
	public void setItens(List<MuralPost> pItens)
	{
		itens = pItens;
	}
	
	public void remove(int pItemPosition)
	{
		itens.remove(pItemPosition);
	}
	
	class auxItemList{
		String nomeUsuario;
		String texto;
	}
}
