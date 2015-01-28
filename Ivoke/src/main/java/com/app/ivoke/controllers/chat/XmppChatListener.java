package com.app.ivoke.controllers.chat;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.content.Context;

import com.app.ivoke.R;
import com.app.ivoke.Router;
import com.app.ivoke.controllers.main.MainActivity;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.DeviceHelper;
import com.app.ivoke.libraries.NotificationManager;
import com.app.ivoke.models.ChatModel;
import com.app.ivoke.objects.Account;
import com.app.ivoke.objects.ChatMessage;
import com.app.ivoke.objects.interfaces.IChatCallBack;

public class XmppChatListener implements org.jivesoftware.smack.PacketListener {
    DebugHelper dbg = new DebugHelper("XmppChatListener");

    ChatModel chatModel;

    private IChatCallBack callback;
    private XMPPConnection xmppConnection;

    public XmppChatListener(XMPPConnection pXmppConnection)
    {
        chatModel = new ChatModel();
        xmppConnection = pXmppConnection;
    }

    public XmppChatListener(IChatCallBack pChatCallback)
    {
        chatModel   = new ChatModel();
    }

    @Override
    public void processPacket(Packet packet) {
        dbg.method("processPacket").par("packet", packet);
        Message message = (Message) packet;

        dbg.var("packet.getTo()"  , packet.getTo());
        dbg.var("packet.getFrom()", packet.getFrom());
        dbg.var("message", message);

        ChatMessage chatMessage = new ChatMessage(message.getPacketID(), message.getFrom(), message.getTo(), message.getBody(), 0);

        dbg.var("chatMessage", chatMessage);

        chatModel.addMessage(chatMessage);

        Account acc = getAccount(message.getFrom().substring(0,message.getFrom().indexOf("/")));

        if( Router.getCurrentActivityClass() !=null
        && !Router.getCurrentActivityClass().equals(ChatActivity.class))
        NotificationManager.showNotification( Router.previousContext
                                            , NotificationManager.CHAT_NOTIFICATION
                                            , R.drawable.conversa_usuario
                                            , "Ivokee - "+acc.getName()
                                            , message.getBody()
                                            , MainActivity.class);

        if(callback!=null)
           callback.onPacketReceived(message.getFrom(), message.getBody());

    }



    public IChatCallBack getCallback() {
        return callback;
    }

    public void setCallback(IChatCallBack callback) {
        this.callback = callback;
    }

    private Account getAccount(String pJid)
    {
        if ( xmppConnection != null && xmppConnection.isConnected() )
        {
            Roster roster = xmppConnection.getRoster();
            RosterEntry entry = roster.getEntry(pJid);
            return new Account(entry, roster.getPresence(pJid));
        }
        else
            return null;
    }

}
