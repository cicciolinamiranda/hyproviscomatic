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

    public static final String TABLE_PURCHASE_ORDER = "purchase";
    public static final String COLUMN_POST_ID = "_id";
    public static final String COLUMN_POST_DOWN = "down";
    public static final String COLUMN_POST_UP = "up";
    public static final String COLUMN_POST_FREQ = "freq";
    public static final String COLUMN_POST_SSID = "ssid";
    public static final String COLUMN_POST_BSSID = "bssid";
    public static final String COLUMN_POST_PING = "ping";
    public static final String COLUMN_POST_LATENCY = "latency";
    public static final String COLUMN_POST_DATE = "date";
    public static final String COLUMN_POST_LAT = "latitude";
    public static final String COLUMN_POST_LONG = "longitude";
    public static final String COLUMN_POST_PACKETLOSS = "packetloss";
    public static final String COLUMN_POST_WIFISTRENGTH = "wifiStrength";

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
