package com.app.ivoke.models;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Roster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.app.ivoke.Router;
import com.app.ivoke.helpers.DebugHelper;
import com.app.ivoke.objects.Account;
import com.app.ivoke.objects.ChatMessage;
import com.app.ivoke.objects.defaults.DefaultSqliteModel;

public class ChatModel extends DefaultSqliteModel{

    DebugHelper dbg  = new DebugHelper("ChatModel");

    private final String TABLE_NAME    = "chat_messages";
    private final String COL_FROM      = "jid_from";
    private final String COL_TO        = "jid_to";
    private final String COL_MESSAGE   = "message";
    private final String COL_READ      = "read";

    private final String COL_PACKAGE_ID  = "package_id"; //to control messages received;

    public ChatModel() {
        super(Router.previousContext);
        database = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if(database == null)
            database = db;
        database.execSQL("CREATE TABLE "+TABLE_NAME+" (" +
                                                        COL_FROM      + TYPE_STRING +","+
                                                        COL_TO        + TYPE_STRING +","+
                                                        COL_MESSAGE   + TYPE_STRING  +","+
                                                        COL_READ      + TYPE_INTEGER +" DEFAULT 0 "+","+
                                                        COL_PACKAGE_ID+ TYPE_STRING  +
                                                    ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dbg.par("oldVersion", oldVersion).par("newVersion", newVersion);
        if(newVersion <= 4)
        try {
            db.execSQL("ALTER TABLE "+TABLE_NAME+"" +
                       "  ADD COLUMN "+COL_PACKAGE_ID+" "+TYPE_INTEGER);
        } catch (SQLException e) {
        }
    }

    public List<ChatMessage> getMessages(Account pFrom, Account pTo)
    {
        dbg.method("getMessages").par("pSender", pFrom).par("pRecepient", pTo);

        List<ChatMessage> messages = new ArrayList<ChatMessage>();

        String sqlAll = getSqlSelectAll()
                         + " WHERE "+ COL_FROM +"||"+ COL_TO +" = '"+pFrom.getJid()+pTo.getJid() + "'" +
                              " OR "+ COL_FROM +"||"+ COL_TO +" = '"+pTo.getJid()+pFrom.getJid() + "'";
        dbg.var("sqlAll",sqlAll);
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sqlAll, null);
            if(cursor.moveToFirst()){
                do{
                ChatMessage message = new ChatMessage();
                            message.setFrom(cursor.getString(cursor.getColumnIndex(COL_FROM)));
                            message.setTo(cursor.getString(cursor.getColumnIndex(COL_TO)));
                            message.setBody(cursor.getString(cursor.getColumnIndex(COL_MESSAGE)));
                            message.setRead(cursor.getInt(cursor.getColumnIndex(COL_READ)));
                messages.add(message);
                }while(cursor.moveToNext());
            }
        } catch (Exception e) {
            cursor = null;
        }

        dbg.var("messages size", messages.size());
        return messages;
    }

    public List<ChatMessage> getUnreadMessages(Account pFrom, Account pTo)
    {
        dbg.method("getMessages").par("pSender", pFrom).par("pRecepient", pTo);

        List<ChatMessage> messages = new ArrayList<ChatMessage>();

        String sqlAll = getSqlSelectAll()
                         + " WHERE ("+ COL_FROM +"||"+ COL_TO +" = '"+pFrom.getJid()+pTo.getJid() + "'" +
                              "  OR "+ COL_FROM +"||"+ COL_TO +" = '"+pTo.getJid()+pFrom.getJid() + "'" +
                              "    )" +
                              " AND "+COL_READ+" = 0 ";
        dbg.var("sqlAll",sqlAll);
        Cursor cursor = database.rawQuery(sqlAll, null);

        if(cursor.moveToFirst()){
            do{
            ChatMessage message = new ChatMessage();
                        message.setFrom(cursor.getString(cursor.getColumnIndex(COL_FROM)));
                        message.setTo(cursor.getString(cursor.getColumnIndex(COL_TO)));
                        message.setBody(cursor.getString(cursor.getColumnIndex(COL_MESSAGE)));
                        message.setRead(cursor.getInt(cursor.getColumnIndex(COL_READ)));
            messages.add(message);
            }while(cursor.moveToNext());
        }

        dbg.var("messages size", messages.size());
        return messages;
    }

    public long addMessage(ChatMessage pChatMessage)
    {
        if(hasMessageAlreadyReceived(pChatMessage.getPackageId())>0)
            return 0;

        dbg.method("addMessage").par("pChatMessage", pChatMessage);
        ContentValues values = new ContentValues();
        values.put(COL_FROM      , pChatMessage.getFrom());
        values.put(COL_TO        , pChatMessage.getTo());
        values.put(COL_MESSAGE   , pChatMessage.getBody());
        values.put(COL_PACKAGE_ID, pChatMessage.getPackageId());
        if(pChatMessage.hasBeenRead())
           values.put(COL_READ   , 1);
        return database.insert(TABLE_NAME, null, values);

    }

    @Override
    public String getSqlSelectAll() {
        return "SELECT * FROM "+TABLE_NAME;
    }

    public int hasMessageAlreadyReceived(String pPackageId)
    {
        String sql = "SELECT 1 " +
                     "  FROM "+TABLE_NAME+" " +
                     " WHERE "+COL_PACKAGE_ID+" = '%s'" +
                     "   AND "+COL_READ+" = 0 ";
        Cursor cursor = database.rawQuery(String.format(sql, pPackageId), null);
        return cursor.getCount();
    }

    public void setMessagesRead(String pUserJid)
    {
        ContentValues values = new ContentValues();
        values.put(COL_READ, 1);

        database.update(TABLE_NAME, values, COL_FROM +" = ?  OR "+ COL_TO +" = ?", new String[]{ pUserJid, pUserJid });
    }



}
