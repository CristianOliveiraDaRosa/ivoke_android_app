package com.app.ivoke.objects.services;

import com.app.ivoke.helpers.DebugHelper;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class XmppServiceConnector implements ServiceConnection {
    DebugHelper dbg = new DebugHelper("XmppServiceConnector");
    private IXmppConnectionService xmppService;
    public boolean isConnected;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        dbg.method("onServiceConnected");
        xmppService = ((XmppConnectionServiceBinder)service).getService();
        isConnected = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        dbg.method("onServiceDisconnected");
        isConnected = false;
    }

    public XmppConnectionService getXmppService()
    {
        return (XmppConnectionService) xmppService;
    }
}
