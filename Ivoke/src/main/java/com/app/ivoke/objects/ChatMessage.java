package com.app.ivoke.objects;

import java.io.Serializable;

public class ChatMessage implements Serializable {

        private static final long serialVersionUID = -3359911823393072186L;

        private String packageId;
        private String fromJid;
        private String toJid;
        private String message;
        private int    read = 0;

        public ChatMessage() {
        }

        public ChatMessage(String pPackageId,String pFromJid, String pToJid, String pMessage, int pRead) {

            String _to   = pToJid;
            String _from = pFromJid;

            if(pFromJid.indexOf("/")>0)
                _from = pFromJid.substring(0, pFromJid.indexOf("/"));

            if(pToJid.indexOf("/")>0)
                _to = pToJid.substring(0, pToJid.indexOf("/"));

            this.packageId = pPackageId;
            this.fromJid  = _from;
            this.toJid    = _to;
            this.message  = pMessage;
            this.read     = pRead;
        }

        public String getFrom() {
            return fromJid;
        }
        public void setFrom(String from) {
            this.fromJid = from;
        }
        public String getBody() {
            return message;
        }
        public void setBody(String body) {
            this.message = body;
        }

        @Override
        public String toString()
        {
            return "FROM "+fromJid+" TO "+toJid+" MESSAGE "+message;
        }

        public String getTo() {
            return toJid;
        }

        public void setTo(String toJid) {
            this.toJid = toJid;
        }

        public void setRead(int pRead)
        {
            this.read = pRead;
        }

        public boolean hasBeenRead()
        {
            return read == 1;
        }

        public String getPackageId() {
            return packageId;
        }

        public void setPackageId(String packageId) {
            this.packageId = packageId;
        }
    }