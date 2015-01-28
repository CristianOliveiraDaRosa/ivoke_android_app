package com.app.ivoke.objects.services;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.app.ivoke.Common;
import com.app.ivoke.Router;
import com.app.ivoke.controllers.chat.XmppChatListener;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.objects.Account;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class XmppConnectionService extends Service implements IXmppConnectionService { 

    private DebugHelper dbg = new DebugHelper("XmppConnectionService");

    public static final String PE_SERVER_HOST = "XmppConnectionService.ServerHost";
    public static final String PE_LOGIN       = "XmppConnectionService.Login";
    public static final String PE_PASSWORD    = "XmppConnectionService.Password";
    public static final String PE_EMAIL       = "XmppConnectionService.Email";
    public static final String PE_FULL_NAME   = "XmppConnectionService.FullName";
    public static final String PE_RESOURCE    = "XmppConnectionService.Resource";

    private XmppConnectionServiceBinder binder ;
    private XmppChatListener chatListener;
    private XMPPConnection m_connection;

    private Account account;

    public String errorMessage;
    boolean isRunning;

    String serverHost ;
    String login      ;
    String password   ;
    String email      ;
    String fullName   ;
    String resource   ;

    @Override
    public void onCreate() {
        dbg.method("onCreate");
        super.onCreate();
        binder = new XmppConnectionServiceBinder(this);
        Log.d(this.getClass().getName(), "onCreate");
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dbg.method("onStartCommand").par("intent", intent);

        errorMessage = null;
        if(intent != null)
        {
            serverHost = intent.getStringExtra(PE_SERVER_HOST);
            login      = intent.getStringExtra(PE_LOGIN);
            password   = intent.getStringExtra(PE_PASSWORD);
            email      = intent.getStringExtra(PE_EMAIL);
            fullName   = intent.getStringExtra(PE_FULL_NAME);
            resource   = intent.getStringExtra(PE_RESOURCE);

            new AsyncConnect().execute();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(this.getClass().getName(), "onDestroy");
        disconnect();
        isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean serverConnect(String serverHost) throws XMPPException
    {
        if(m_connection==null || !m_connection.isConnected()){
            ConnectionConfiguration config = new ConnectionConfiguration(serverHost, 5222);
            dbg.log("Instance XMPPTCPConnection");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                config.setTruststoreType("AndroidCAStore");
                config.setTruststorePassword(null);
                config.setTruststorePath(null);
            } else {
                config.setTruststoreType("BKS");
                String path = System.getProperty("javax.net.ssl.trustStore");
                if (path == null)
                    path = System.getProperty("java.home") + File.separator + "etc"
                        + File.separator + "security" + File.separator
                        + "cacerts.bks";
                config.setTruststorePath(path);
            }


            m_connection = new XMPPConnection(config);
            dbg.log("conecting");
            m_connection.connect();

            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

        }

        return m_connection.isConnected();
    }

    public void connection() throws IOException, XMPPException
    {
        dbg.method("connection")
        .par("serverHost", serverHost)
        .par("login", login)
        .par("password", password)
        .par("resource", resource);

        serverConnect(serverHost);
    }

    public boolean userLogin()
    {
        dbg.log("login");
        dbg.var("login.substring(0, login.indexOf(\"@\"))", login.substring(0, login.indexOf("@"))).var("password", password).var("resource", resource) ;
        
        if(isServerConnected())
        try {

            try {
                registerUser(login.substring(0, login.indexOf("@")), password, email, fullName);
            } catch (XMPPException e) {}

             m_connection.login(login.substring(0, login.indexOf("@")), password, resource);

             Presence presence = new Presence(Presence.Type.available);
             m_connection.sendPacket(presence);
             dbg.log("Setting listener");

             setChatListener(new XmppChatListener(m_connection));
             account  = new Account();
             account.setJid(login);
             account.setName(fullName);

            Common common = (Common) getApplication();
            dbg.log("## Setting account "+account);
            common.setXmppAccount(account);

        } catch (Exception e) {
             dbg.exception(e);
             return false;
        }
         return true;
    }

    public void setPresence()
    {

    }

    public void disconnect() {
        if ( m_connection != null && !m_connection.isConnected() ){
                m_connection.disconnect();
        }
    }
    
    /** METHODS **/
    /** APP TO SERVER **/
    public boolean userExists(String serverHost, String login) throws IOException, XMPPException
    {
        dbg.method("userExists")
        .par("serverHost", serverHost)
        .par("login", login);

        serverConnect(serverHost);

        UserSearchManager search = new UserSearchManager(m_connection);

        Form searchForm = search.getSearchForm("search."+m_connection.getServiceName());
        Form answerForm = searchForm.createAnswerForm();  
        
        answerForm.setAnswer("Username", true);  
        answerForm.setAnswer("search", login);  

        org.jivesoftware.smackx.ReportedData data = search.getSearchResults(answerForm,"search."+m_connection.getServiceName());  

            if(data.getRows() != null)
            {
                Iterator<Row> it = data.getRows();
                return it.hasNext();
            }
        return false;
    }

    public boolean registerUser(String pLogin, String pPassword, String pEmail, String pFullName) throws XMPPException
    {
        org.jivesoftware.smack.AccountManager am = getConnection().getAccountManager();
        if(am.supportsAccountCreation() && isServerConnected())
        {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("username", pLogin);
            attributes.put("password", pPassword);
            attributes.put("email"   , pEmail);
            attributes.put("name"    , pFullName);
            am.createAccount(pLogin, pPassword, attributes);

            return true;
        }else
            return false;
    }

    public XMPPConnection getConnection () {
        return m_connection;
    }

    public boolean isServerConnected()
    {
        return m_connection!= null? m_connection.isConnected() : false;
    }
    
    public Roster getRoster () {
        Roster roster = null;
        if ( m_connection != null && m_connection.isConnected() ) {
            roster = m_connection.getRoster();
        }

        return roster;
    }

    public Account getAccount(String pJid) {

        if ( m_connection != null && m_connection.isConnected() )
        {
            Roster roster = m_connection.getRoster();
            RosterEntry entry = roster.getEntry(pJid);
            return new Account(entry, roster.getPresence(pJid));
        }
        else
            return null;
    }
    
    public Account getMyAccount() {

        if ( m_connection != null && m_connection.isConnected() )
        {
            Roster roster = m_connection.getRoster();
            RosterEntry entry = roster.getEntry(login);
            return new Account(entry, roster.getPresence(login));
        }
        else
            return null;
    }
    
    /** USER TO USER **/
    public void sendMessage(String text, String to) {
        Message msg = new Message(to, Message.Type.chat);
        msg.setBody(text);
        if ( m_connection != null && m_connection.isConnected() ) {
                m_connection.sendPacket(msg);
        }
    }
    
    public void addFriend(String pJid, String pUserNick, String[] pGroup) throws XMPPException
    {

        if(isServerConnected())
        {
            Roster roster = getRoster();
            if(roster !=null && roster.getEntry(pJid) == null)
               roster.createEntry(pJid, pUserNick, pGroup);

        }else
        {
            new AsyncConnect().execute();
        }
    }

    public void deleteFriend(String pJid) throws XMPPException
    {
        Roster roster = getRoster();
        if(roster !=null && roster.getEntry(pJid) != null)
        {
            RosterEntry user = roster.getEntry(pJid);
            roster.removeEntry(user);
        }
    }
    
    public void acceptFriend(String pJid) throws XMPPException
    {
        Roster roster = getRoster();
        if(roster !=null)
        {
            Presence subscribe = new Presence(Presence.Type.subscribe);
            subscribe.setTo(pJid);
            getConnection().sendPacket(subscribe);
        }
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setChatListener(XmppChatListener pXmppChatListener)
    {
        if(chatListener==null)
        {
            chatListener = pXmppChatListener;
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            if(getConnection()!=null)
               getConnection().addPacketListener(chatListener, filter);
        }
    }

    public XmppChatListener getChatListener()
    {
        return chatListener;
    }

    class AsyncConnect extends AsyncTask<Object, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Object... params) {

            try {
                connection();
                userLogin();
            }catch (IOException e) {
                dbg.exception(e);
                errorMessage = e.getMessage();

                return false;
            } catch (XMPPException e) {
                dbg.exception(e);
                errorMessage = e.getMessage();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dbg._class(this).method("onPostExecute").par("result", result);
            if(result)
            {
                try {
                     registerUser(login.substring(0, login.indexOf("@")), password, email, fullName);
                } catch (XMPPException e) {
                    if(!e.getMessage().equals("conflict(409)"))
                    {
                        dbg.exception(e);
                        e.printStackTrace();
                    }
                }

                try {
                    m_connection.login(login, password, resource);
                    m_connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);
                } catch (Exception e) {
                    dbg.exception(e);
                }
            }
            else
            {
                dbg.log("não conectou");
                errorMessage = "Não foi possível connectar.";
            }
        }

    }

    public Account getAccount()
    {
        return account;
    }

}