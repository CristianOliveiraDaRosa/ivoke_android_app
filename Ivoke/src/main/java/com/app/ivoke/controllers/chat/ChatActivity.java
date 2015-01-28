package com.app.ivoke.controllers.chat;

import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.ivoke.Common;
import com.app.ivoke.R;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.models.ChatModel;
import com.app.ivoke.objects.Account;
import com.app.ivoke.objects.ChatMessage;
import com.app.ivoke.objects.services.IXmppConnectionService;
import com.app.ivoke.objects.services.XmppConnectionService;
import com.app.ivoke.objects.services.XmppConnectionServiceBinder;
import com.facebook.widget.ProfilePictureView;



public class ChatActivity extends Activity implements ServiceConnection {

    public static final String PE_CHAT_ACCOUNT       = "ChatActivity.Account";
    public static final String PE_IS_CHAT_ANONYMOUS  = "ChatActivity.IsAnonymous";

    DebugHelper dbg = new DebugHelper("ChatActivity");

    private ChatMessageList discussionThread;
    private ArrayAdapter<ChatMessage> discussionThreadAdapter;
    private Handler handler;
    private ListView chatListView;

    private static final String MESSAGES = "messages";

    private Common common;
    private Account accountTo;
    private Account accountFrom;
    private boolean isAnonymous;

    private ChatModel chatModel;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent connectionService = new Intent(this,XmppConnectionService.class); 
        bindService(connectionService,this, Context.BIND_AUTO_CREATE);
        
        setContentView(R.layout.chat_activity);
        
        Intent intent = getIntent();
        
        common = (Common) getApplication();
        accountFrom  = common.getXmppAccount();
        accountTo    =  (Account) intent.getSerializableExtra(PE_CHAT_ACCOUNT);
        
        if(accountTo == null)
            this.finish();
        
        isAnonymous  = intent.getBooleanExtra(PE_IS_CHAT_ANONYMOUS, false);
        
        handler = new Handler();
        
        final TextView recipient = (TextView) this.findViewById(R.id.recipient);

        if(!isAnonymous)
            recipient.setText(accountTo.getName());

        final EditText message = (EditText) this.findViewById(R.id.embedded_text_editor);
        chatListView = (ListView) this.findViewById(R.id.thread);


        discussionThread = (ChatMessageList) (savedInstanceState != null ? savedInstanceState.getParcelable(MESSAGES) : null);
        if ( discussionThread == null ) {
            discussionThread = new ChatMessageList();
        }


        discussionThreadAdapter = new MessageAdapter(this, R.layout.chat_list_view_template, discussionThread);
        chatListView.setAdapter(discussionThreadAdapter);

        chatModel = new ChatModel();

        chatModel.setMessagesRead(accountTo.getJid());

        Button send = (Button) this.findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String text = message.getText().toString();

                if(!text.equals(""))
                {
                    xmppService.sendMessage(text, accountTo.getJid());
                    ChatMessage chatMessage = new ChatMessage("0", accountFrom.getJid() , accountTo.getJid(), text, 1);

//                    m_discussionThread.getMessages().add(chatMessage);

                    if(discussionThread.addMessage(chatMessage))
                       chatModel.addMessage(chatMessage);

                    discussionThreadAdapter.notifyDataSetChanged();
                    message.setText("");
                }
            }
        });

        if(accountTo.getMode() != Mode.available)
        {
            MessageHelper.infoAlert(this)
                         .setMessage(R.string.chat_info_msg_offline)
                         .setButtonOk( new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              finish();
                            }
                        }).showDialog();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MESSAGES, discussionThread);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      discussionThread = savedInstanceState.getParcelable(MESSAGES);
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        dbg.log(intent.toString());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isAnonymous)
            try {
                ((XmppConnectionService)xmppService).deleteFriend(accountTo.getJid());
            } catch (XMPPException e) {
                dbg.exception(e);
                e.printStackTrace();
            }

        unbindService(this);
    }

    private void showHistoryMessages()
    {
        if(xmppService != null){
            List<ChatMessage> history = chatModel.getMessages(accountFrom ,accountTo);

            for (int i = 0; i < history.size(); i++) {
                discussionThread.addMessage(history.get(i));
            }
        }
    }
    
    class MessageAdapter extends ArrayAdapter<ChatMessage> {

        Context context;

        MessageAdapter(Context context, int resource, ChatMessageList objects) {
            super(context, resource, objects.getList());
            this.context=context;
        }




        public View getView(int position, View convertView, ViewGroup parent) {
            View row = View.inflate(context, R.layout.chat_list_view_template, null);
            if ( position < discussionThread.getList().size() ) {

                ChatMessage mess = discussionThread.getList().get(position);
                TextView message=(TextView)row.findViewById(R.id.message);
                message.setText(mess.getBody());

                LinearLayout layPopUpMessage = (LinearLayout) row.findViewById(R.id.chat_lay_pop_up_message);

                ProfilePictureView icon = (ProfilePictureView)row.findViewById(R.id.chat_img_user_from);
                if(!isAnonymous)
                icon.setProfileId(mess.getFrom().substring(0, mess.getFrom().indexOf("@")));

                dbg.var("mess", mess).var("accountFrom", accountFrom);
                dbg.log("mess.getFrom()" +mess.getFrom()+" accountFrom.getJid() "+accountFrom.getJid());
                if ( mess.getFrom().equals(accountFrom.getJid())) {
                    layPopUpMessage.setBackgroundResource(R.drawable.from_popup_message);
                    icon.setVisibility(View.INVISIBLE);
                    //    icon.setImageResource(R.drawable.conversa_usuario);
                } else {
                    layPopUpMessage.setBackgroundResource(R.drawable.to_popup_message);
                    icon.setVisibility(View.VISIBLE);
                        //icon.setImageResource(R.drawable.user);
                }
            }

            return(row);
        }
    }


    private IXmppConnectionService xmppService;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        dbg.log("Connected!");
        xmppService = ((XmppConnectionServiceBinder)service).getService(); 

        //accountFrom  = xmppService.getAccount();
        
        PacketFilter filter = new MessageTypeFilter(Message.Type.chat);

        xmppService.getConnection().addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null)
                    {
                        dbg.log("MESSAGE FROM "+message.getFrom());
                        dbg.log("MESSAGE TO "+message.getTo());
                        dbg.var("accountTo.jid", accountTo.getJid());
                        dbg.var("message", message);
                        ChatMessage chatMessage = new ChatMessage(message.getPacketID(), message.getFrom(), message.getTo(), message.getBody(), 0);
//                        m_discussionThread.getMessages().add(chatMessage);

                        //Only receipt messages from user in chat
                        if(chatMessage.getFrom().equals(accountTo.getJid()))
                        {
                            discussionThread.addMessage(chatMessage);
                            //chatModel.addMessage(chatMessage);

                            handler.post(new Runnable() {
                                public void run() {
                                    discussionThreadAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }, filter);

        showHistoryMessages();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        dbg.log("DisConnected!");
    }

}