package com.app.ivoke.objects.defaults;

import com.app.ivoke.helpers.DebugHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DefaultSqliteModel extends SQLiteOpenHelper {

    public static final String TYPE_STRING     = " TEXT";
    public static final String TYPE_INTEGER    = " INTEGER";
    public static final String TYPE_DATE       = " TEXT";
    public static final String TYPE_INTEGER_PK = " INTEGER PRIMARY KEY DESC";

    public static final int    DATABASE_VERSION = 4;
    public static final String DATABASE_NAME    = "ivoke_device.db";
    public SQLiteDatabase database;

    DebugHelper dbg = new DebugHelper(this);

    public DefaultSqliteModel(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public abstract String getSqlSelectAll();

    public void closeDatabase()
    {
        if(database!=null)
           database.close();
    }
}
