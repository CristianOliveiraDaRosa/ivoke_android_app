package com.app.ivoke.objects.services;

import android.os.Binder;

public class XmppConnectionServiceBinder extends Binder { 
  
    private IXmppConnectionService service = null; 
  
    public XmppConnectionServiceBinder(IXmppConnectionService service) { 
        super(); 
        this.service = service; 
    } 
 
    public IXmppConnectionService getService(){ 
        return service; 
    } 
};