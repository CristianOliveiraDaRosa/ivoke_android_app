package com.app.ivoke.controllers.main;

import java.util.Date;
import java.util.List;

import com.app.ivoke.Common;
import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.helpers.DateTimeHelper;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.MetricHelper;
import com.app.ivoke.helpers.MetricHelper.Metric;
import com.app.ivoke.objects.MuralPost;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.internal.bn;

import android.content.Context;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MuralAdapter extends BaseAdapter {

    DebugHelper dbg  = new DebugHelper(this);

    LayoutInflater lInflater;
    List<MuralPost> itens;
    Context context;
    int itemAtual;

    OnClickListener btnStartChatListener;
    OnClickListener btnOpenFacebookListener;
    private OnClickListener btnTranslateListener;

    Common common;

    public MuralAdapter(Context pContext, List<MuralPost> pItens) {
        lInflater = LayoutInflater.from(pContext);
        itens     = pItens;
        context   = pContext;

        common = (Common) context.getApplicationContext();
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
        View listTemplateView = convertView;
        ViewTemplateHolder holder;
        boolean isNewView = listTemplateView == null;
        MuralPost muralPost = itens.get(position);

        if(isNewView)
        {
            listTemplateView = lInflater.inflate(R.layout.main_mural_list_template, null);
            holder = new ViewTemplateHolder();
            dbg.method("getView").par("position", position);
            //if((listTemplateView = convertView) == null)

            holder.txtName     =
                    (TextView) listTemplateView.findViewById(R.id.main_mural_title);
            holder.txtMessage =
                    (TextView) listTemplateView.findViewById(R.id.main_mural_message);
            holder.txtWhen  =
                    (TextView) listTemplateView.findViewById(R.id.main_mural_when);

            holder.btnStartChat    = (ImageButton) listTemplateView.findViewById(R.id.main_mural_btn_menu_options_chat);
            holder.btnOpenFacebook = (ImageButton) listTemplateView.findViewById(R.id.main_mural_btn_menu_options_open_facebook);
            holder.btnTranslate    = (Button) listTemplateView.findViewById(R.id.main_mural_btn_menu_options_translate);
            holder.muralId = muralPost.getId();

            listTemplateView.setTag(holder);
        }else
            holder = (ViewTemplateHolder) listTemplateView.getTag();

        ProfilePictureView profilePictureView =
                (ProfilePictureView) listTemplateView.findViewById(R.id.main_mural_image);

        holder.btnStartChat.setTag(position);
        holder.btnOpenFacebook.setTag(position);
        holder.btnTranslate.setTag(position);

        //Treatments for anonymous and session user
        if(muralPost.isAnonymous())
        {
             String nome =  context.getString(R.string.def_lbl_usuario_anonymous);
             holder.txtName.setText(nome);
             profilePictureView.setProfileId(null);

                setButtonVisible(holder.btnStartChat,false);
                setButtonVisible(holder.btnOpenFacebook,false);
        }
        else
        {
            profilePictureView.setProfileId(muralPost.getFacebookId());
            holder.txtName.setText(muralPost.getName());

            holder.btnOpenFacebook.setOnClickListener(btnOpenFacebookListener);
            holder.btnStartChat.setOnClickListener(btnStartChatListener);

            boolean isDifUser = muralPost.getUserId() != common.getSessionUser().getId();

            setButtonVisible(holder.btnStartChat   , isDifUser);
            setButtonVisible(holder.btnOpenFacebook, isDifUser);



//            if(common.getSessionUser().getId() == itens.get(position).getUserId())
//            {
//                setButtonInvisible(btnStartChat);
//                setButtonInvisible(btnOpenFacebook);
//            }
        }

//        if(muralPost.isTranslated())
//            holder.btnTranslate.setText(R.string.def_lbl_tranlated);

        holder.btnTranslate.setOnClickListener(btnTranslateListener);

        holder.btnTranslate.setFocusable(false);
        holder.btnOpenFacebook.setFocusable(false);
        holder.btnStartChat.setFocusable(false);

        holder.txtMessage.setText(muralPost.getMessage());

        Date dtPost = muralPost.getDatePost();
        Date now = new Date();

        long minutes = DateTimeHelper.getMinutesBetween(now, dtPost);

        if(DateTimeHelper.getDaysFromMinutes(minutes)>1)
            holder.txtWhen.setText(String.format( context.getString(R.string.def_description_days)
                                           , DateTimeHelper.getDaysFromMinutes(minutes)));
        else if(DateTimeHelper.getHoursFromMinutes(minutes)>1)
            holder.txtWhen.setText(String.format( context.getString(R.string.def_description_hours)
                                           , DateTimeHelper.getHoursFromMinutes(minutes)));
        else if (minutes>5)
            holder.txtWhen.setText(String.format( context.getString(R.string.def_description_minutes)
                                           , minutes));
        else
            holder.txtWhen.setText(context.getString(R.string.def_description_now));


        dbg.var("getCount()", getCount());

        return listTemplateView;

    }

    private void setButtonVisible(ImageButton pButton, boolean pVisible)
    {
        if(pVisible)
            pButton.setVisibility(View.VISIBLE);
        else
        {
            pButton.setOnClickListener(null);
            pButton.setVisibility(View.INVISIBLE);
        }
    }

    public void setItens(List<MuralPost> pItens)
    {
        itens = pItens;
    }

    public void remove(int pItemPosition)
    {
        itens.remove(pItemPosition);
    }

    public void setBtnStartChatListener(OnClickListener pListener)
    {
        btnStartChatListener = pListener;
    }

    public void setBtnOpenFacebookListener(OnClickListener pListener)
    {
        btnOpenFacebookListener = pListener;
    }

    public void setBtnTranslateListener(OnClickListener btnTranslateListener) {
        this.btnTranslateListener = btnTranslateListener;
    }


    static class ViewTemplateHolder
    {
        public int muralId;
        public ProfilePictureView profilePictureView;
        public ImageButton btnOpenFacebook;
        public ImageButton btnStartChat;
        public Button      btnTranslate;
        public TextView    txtName;
        public TextView    txtWhen;
        public TextView    txtMessage;
        public TextView    txtDistance;

    }
}
