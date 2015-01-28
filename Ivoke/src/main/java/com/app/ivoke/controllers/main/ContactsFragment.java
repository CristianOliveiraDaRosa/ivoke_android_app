package com.app.ivoke.controllers.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket.ItemStatus;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONException;

import com.app.ivoke.R;
import com.app.ivoke.Router;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;


import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.helpers.DeviceHelper;
import com.app.ivoke.helpers.MessageHelper;
import com.app.ivoke.libraries.NotificationManager;
import com.app.ivoke.models.ChatModel;
import com.app.ivoke.models.UserModel;
import com.app.ivoke.objects.*;
import com.app.ivoke.objects.defaults.DefaultOkListener;
import com.app.ivoke.objects.defaults.DefaultWebCallback;
import com.app.ivoke.objects.interfaces.IChatCallBack;
import com.app.ivoke.objects.services.IXmppConnectionService;
import com.app.ivoke.objects.services.XmppConnectionService;
import com.app.ivoke.objects.services.XmppConnectionServiceBinder;
import com.facebook.widget.ProfilePictureView;

public class ContactsFragment extends Fragment  implements ServiceConnection {
    DebugHelper dbg = new DebugHelper("ContactsFragment");

    private List<Account> listFriends;
    private ContactsAdapter contactsAdapter;
    private Handler m_handler;
    Intent connectionService;

    ListView contactsLV;

    MainActivity mainActivity;
    View     fragmentView;

    ChatModel chatModel;
    UserModel userModel;
    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.main_contacts_fragment, container, false);
        contactsLV = (ListView) fragmentView.findViewById(R.id.contatctListView);
        
        mainActivity = (MainActivity) getActivity();
        
        chatModel = new ChatModel();
        userModel = new UserModel();
        
        connectionService = new Intent(mainActivity, XmppConnectionService.class); 
        mainActivity.bindService(connectionService,this, Context.BIND_AUTO_CREATE);
        
        m_handler = new Handler();
        
        listFriends = new ArrayList<Account>();
        
        contactsAdapter = new ContactsAdapter(mainActivity, R.layout.main_contacts_list_template, listFriends);
        contactsLV.setAdapter(contactsAdapter);
        contactsLV.setTextFilterEnabled(true);
        
        registerForContextMenu(contactsLV);
        // ListActivity has a ListView, which you can get with:
        //ListView lv = getListView();

        contactsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
                Account account = listFriends.get(pos);
                dbg.log("click on: "+ account);

                Router.gotoChat(getActivity(), account, false);
            }

        });
        
        NotificationManager
            .disposeNotification( getActivity()
                                , NotificationManager.CHAT_NOTIFICATION);
       
        return fragmentView;

    }
    
    @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
         super.onCreateContextMenu(menu, v, menuInfo);

         MenuInflater m = getActivity().getMenuInflater();
         m.inflate(R.menu.main_contacts_context_menu, menu);
    }
    
    @Override  
       public boolean onContextItemSelected(MenuItem item) {
         dbg.method("onContextItemSelected");
         AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
         Account account = (Account) contactsLV.getItemAtPosition(info.position);
         
         switch(item.getItemId()){
              case R.id.main_contact_ctxmenu_start_chat:

                 Router.gotoChat(getActivity(), account, false);

                  break;
              case R.id.main_contact_ctxmenu_delete_user:

                  deleteSelectedFriend(account);

            }
            return super.onContextItemSelected(item);
       }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivity.unbindService(this);
        chatModel.close();
    }
    
    
    private void deleteSelectedFriend(Account account)
    {
        dbg.method("deleteSelectedFriend");
        if(xmppService!=null)
        try {
            dbg.var("account", account);
            xmppService.deleteFriend(account.getJid());
            contactsAdapter.remove(account);
            contactsAdapter.notifyDataSetChanged();
        } catch (XMPPException e) {
            dbg.exception(e);
            MessageHelper.errorAlert(mainActivity)
                         .setMessage(R.string.main_contact_error_msg_delete_user)
                         .showDialog();
        }
    }
    
    private Account searchAccount(String user) {

        for ( Account account : listFriends ) {
            if ( account.getJid().equals(user)) {
                return account;
            }
        }

        return null;
    }
    
    class ContactsAdapter extends ArrayAdapter<Account> {

        Context context;

        ContactsAdapter(Context context, int resource,  List<Account> objects ) {
            super(context, resource, objects);
            this.context=context;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            View row = View.inflate(context, R.layout.main_contacts_list_template, null);
            if ( position < listFriends.size() ) {
                Account account = listFriends.get(position);
                com.facebook.widget.ProfilePictureView
                  imgProfile = (ProfilePictureView) row.findViewById(R.id.main_contact_user_image);

                String fbID = account.getJid().replace("@"+getString(R.string.ws_xmpp_host), "");
                TextView lblUserName = (TextView)row.findViewById(R.id.main_contact_user_name);
                if ( account != null ){
                    if ( account.getName() != null ) {
                        try {
                            imgProfile.setProfileId(fbID);
                        } catch (Exception e) { }

                        lblUserName.setText(account.getName());
                    }else
                    {
                        try {
                            userModel.asyncGetIvokeUser(fbID, new RequestUserCallBack(account));
                        } catch (Exception e) {
                            dbg.exception(e);
                            e.printStackTrace();
                        }
                    }

                    if ( account.getStatus() != null ) {
                        TextView message=(TextView)row.findViewById(R.id.main_contact_last_message);
                        message.setText(account.getStatus());
                    }


                    if ( account.getMode() != null ) {
                        TextView icon=(TextView)row.findViewById(R.id.main_contact_icon_presence);
                        switch ( account.getMode() ) {

//                            case chat:
//                                icon.setImageResource(R.drawable.chat);
//                                break;
//
                            case available:
                                icon.setText(R.string.chat_presence_online);
                                icon.setTextColor(getResources().getColor(R.color.chat_presence_online));
                                break;

                            case away:
                                icon.setText(R.string.chat_presence_offline);
                                icon.setTextColor(getResources().getColor(R.color.chat_presence_offline));

                                break;
                            default:
                                icon.setText(R.string.chat_presence_offline);
                                icon.setTextColor(getResources().getColor(R.color.def_ivoke_white));
                                break;

//                            case xa:
//                                icon.setImageResource(R.drawable.extended_away);
//                                break;
//
//                            case dnd:
//                                icon.setImageResource(R.drawable.busy);
//                                break;


                        }
                    }

                }



            }

            return(row);
        }

        public void updateAcount(Account pAccount)
        {
            for (int i = 0; i < this.getCount(); i++) {
                Account acount = getItem(i);
                if(acount.getJid().equals(pAccount.getJid()))
                    acount = pAccount;
            }
        }
    }


    private XmppConnectionService xmppService;


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        dbg.log("Connected!");
        xmppService = (XmppConnectionService) ((XmppConnectionServiceBinder)service).getService(); 
        Roster roster = xmppService.getRoster();
        Account fromAccount = mainActivity.common.getXmppAccount();
        if(roster != null)
        {
            Collection<RosterEntry> entries = roster.getEntries();

            for(RosterEntry entry : entries)
            {
                Presence subscribe = new Presence(Presence.Type.subscribe);
                subscribe.setTo(entry.getUser());
                xmppService.getConnection().sendPacket(subscribe);
            }

            for (RosterEntry entry : entries)
            {
                Account account = new Account();

                account.setSubscrition(entry.getStatus() == ItemStatus.SUBSCRIPTION_PENDING);
                account.setJid(entry.getUser());

                Presence presence = roster.getPresence(entry.getUser());
                dbg.log("presence:" + presence.toString());
                dbg.log("presence.getMode():" + presence.getMode()+" getStatus() "+presence.getStatus());
                account.setName(entry.getName());
                //account.setMode(presence.getMode());

                List<ChatMessage> list = chatModel.getMessages(fromAccount, account);
                int unreadCount = 0;

                if(list.size()>0)
                {
                    for (int i = 0; i < list.size(); i++) {
                        if(!list.get(i).hasBeenRead())
                        unreadCount++;
                    }
                    ChatMessage lastMessage = list.get(list.size() - 1);
                    String status = lastMessage.getBody();

                    if(unreadCount>0)
                        status = "("+unreadCount+") "+status;

                    account.setStatus(status);
                }
                account.setPresence(presence);
                dbg.log("RosterEntry:" + account);
                listFriends.add(account);
            }

            Collections.sort(listFriends, new PresenceComparator());

            RosterListener rosterListener = new RosterListener() {

                @Override
                public void entriesDeleted(Collection<String> addresses) {
                    dbg.log("EntriesDeleted: " + addresses.toString());
                    System.out.println();
                }

                @Override
                public void entriesUpdated(Collection<String> addresses) {
                    dbg.log("EntriesUpdated: " + addresses.toString());
                }

                @Override
                public void presenceChanged(Presence presence) {
                    dbg.log("Presence changed: " + presence.getFrom() + " " + presence);

                    if ( presence.getFrom() != null ) {
                        final Account account = searchAccount(StringUtils.parseBareAddress(presence.getFrom()));
                        if ( account != null ) {

                            account.setPresence(presence);

                            m_handler.post(new Runnable() {
                                public void run() {
                                    contactsAdapter.updateAcount(account);
                                    contactsAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }
                }
                @Override
                public void entriesAdded(Collection<String> addresses) {
                    dbg.log("EntriesAdded: " + addresses.toString());
            }
        };
        
        
        roster.addRosterListener(rosterListener);
                
        contactsLV.setAdapter(contactsAdapter);
        
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        dbg.log("Disconnected!");
    }

    public class PresenceComparator implements Comparator<Account> {
        @Override
        public int compare(Account o1, Account o2) {
            return o1.getMode().compareTo(o2.getMode());
        }
    }

    private class RequestUserCallBack extends DefaultWebCallback
    {
        Account account;

        public RequestUserCallBack(Account pAccount)
        {
            account   = pAccount;
        }

        @Override
        public void onCompleteTask(Object pResult) {
            super.onCompleteTask(pResult);

            try {
                UserIvoke user = userModel.castJson(pResult.toString());
                account.setName(user.getName());

                Roster roster = xmppService.getRoster();
                 roster.getEntry(account.getJid()).setName(user.getName());

                 contactsAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                dbg.exception(e);
                e.printStackTrace();
            }
        }
    }

    private class OnContactChatCallBack implements IChatCallBack
    {
        @Override
        public void onPacketReceived(String pUserName, String pMessage) {
            contactsAdapter.notifyDataSetChanged();
        }
    }
}
