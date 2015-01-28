package com.app.ivoke.models;

import com.app.ivoke.Router;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class Database extends SQLiteOpenHelper {

    public static String DATABASE_NAME    = "ivoke.db";
    public static int    DATABASE_VERSION = 1;

    public Database() {
        super(Router.previousContext, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }
}
