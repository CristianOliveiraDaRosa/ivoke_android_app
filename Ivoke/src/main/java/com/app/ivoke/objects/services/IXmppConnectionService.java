package com.app.ivoke.objects.services;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.app.ivoke.objects.Account;

public interface IXmppConnectionService { 
    
    void connection() throws XMPPException, Exception ;

    void sendMessage(String pMessage, String pToJid);

    Roster getRoster () ;

    XMPPConnection getConnection();

    String getErrorMessage();

    public Account getAccount();
} 