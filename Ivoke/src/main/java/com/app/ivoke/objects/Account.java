package com.app.ivoke.objects;

import java.io.Serializable;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;


public class Account implements Serializable {

        private static final long serialVersionUID = 1L;

        private String name;
        private String JID;
        private Mode mode;
        private String status;

        private boolean isSubscribeted;
//        private Presence presence;

        public Account(){}

        public Account(RosterEntry pEntry, Presence pPresence)
        {
            this.name   = pEntry.getName();
            this.JID   = pEntry.getUser();
            setPresence(pPresence);
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getJid() {
            return JID;
        }
        public void setJid(String pJid) {
            this.JID = pJid;
        }
        public String getStatus() {
            return status; //presence.getStatus();
        }
        public void setStatus(String status) {
            this.status = status;
//            presence.setStatus(status);
        }
        public Mode getMode() {
            return mode; //presence.getMode();
        }
        public void setMode(Mode mode) {
            this.mode = mode;//presence.getMode();
//            presence.setMode(mode);
        }

        public void setPresence(Presence pPresence)
        {
//            this.presence = pPresence;

            if(pPresence.isAvailable())
                setMode(Presence.Mode.available);
            else if(pPresence.isAway())
                setMode(Presence.Mode.away);
            else
                setMode(Presence.Mode.dnd);

        }

        public boolean isSubscribeted()
        {
            return isSubscribeted;
        }

        public void setSubscrition(boolean pSubscribeted)
        {
            this.isSubscribeted  = pSubscribeted;
        }

        @Override
        public String toString() {
            return "Account [mode=" + getMode() + ", name=" + name + ", status="
                    + getStatus() + ", user=" + JID + "]";
        }


    }