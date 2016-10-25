package com.uprise.ordering.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cicciolina on 10/16/16.
 */
public class SqlDatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;

    private static final String DATABASE_NAME = "main.db";
    private static final int DATABASE_VERSION = 1;

    public SqlDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbWrite = this.getWritableDatabase();
        dbRead = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
