package com.uprise.ordering.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.uprise.ordering.model.CartItemsModel;

import java.util.ArrayList;

/**
 * Created by cicciolina on 10/16/16.
 */
public class SqlDatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;

    private static final String DATABASE_NAME = "main.db";
    private static final int DATABASE_VERSION = 1;

//    public static final String TABLE_PURCHASE_ORDER = "purchase";
//    public static final String COLUMN_POST_ID = "_id";
//    public static final String COLUMN_POST_DOWN = "down";
//    public static final String COLUMN_POST_UP = "up";
//    public static final String COLUMN_POST_FREQ = "freq";
//    public static final String COLUMN_POST_SSID = "ssid";
//    public static final String COLUMN_POST_BSSID = "bssid";
//    public static final String COLUMN_POST_PING = "ping";
//    public static final String COLUMN_POST_LATENCY = "latency";
//    public static final String COLUMN_POST_DATE = "date";
//    public static final String COLUMN_POST_LAT = "latitude";
//    public static final String COLUMN_POST_LONG = "longitude";
//    public static final String COLUMN_POST_PACKETLOSS = "packetloss";
//    public static final String COLUMN_POST_WIFISTRENGTH = "wifiStrength";


    private static final String TABLE_CART_ITEMS = "cart_items";
    private static final String COLUMN_CART_ITEMS_ID = "_id";
    private static final String COLUMN_CART_ITEMS_QTY = "quantity";
    private static final String COLUMN_CART_ITEMS_BRANCH_ID = "branchId";
    private static final String COLUMN_CART_ITEMS_PRODUCT_ID = "productModelId";
    private static final String COLUMN_CART_ITEMS_USERNAME = "username";

    // Database creation sql statement
    private static final String DATABASE_CREATE_CART_ITEMS_DATA = "create table "
            + TABLE_CART_ITEMS + "(" + COLUMN_CART_ITEMS_ID
            + " integer primary key autoincrement , "
            + COLUMN_CART_ITEMS_QTY + " text not null , "
            + COLUMN_CART_ITEMS_BRANCH_ID + " text not null, "
            + COLUMN_CART_ITEMS_PRODUCT_ID + " text not null, "
            + COLUMN_CART_ITEMS_USERNAME + " text not null); ";

    public SqlDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbWrite = this.getWritableDatabase();
        dbRead = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_CART_ITEMS_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(SqlDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }


    public long createCartItems(CartItemsModel data) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_CART_ITEMS_QTY, data.getQuantity());
        values.put(COLUMN_CART_ITEMS_BRANCH_ID, data.getBranchId());
        values.put(COLUMN_CART_ITEMS_PRODUCT_ID, data.getProductModelId());
        values.put(COLUMN_CART_ITEMS_USERNAME, data.getUserName());

        return dbWrite.insert(TABLE_CART_ITEMS, null, values);
    }

    public long updateCartItems(CartItemsModel data) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CART_ITEMS_QTY, data.getQuantity());

        String whereClause = COLUMN_CART_ITEMS_BRANCH_ID + "= '" + data.getBranchId()
                + "' AND " + COLUMN_CART_ITEMS_PRODUCT_ID + "= '" + data.getProductModelId()
                + "' AND " + COLUMN_CART_ITEMS_USERNAME + "= '" + data.getUserName()+"'";
        return dbWrite.update(TABLE_CART_ITEMS, values, whereClause, null);
    }

    public long deleteCartItem(CartItemsModel data) {
        String whereClause = COLUMN_CART_ITEMS_BRANCH_ID + "= '" + data.getBranchId()
                + "' AND " + COLUMN_CART_ITEMS_PRODUCT_ID + "= '" + data.getProductModelId()
                + "' AND " + COLUMN_CART_ITEMS_USERNAME + "= '" + data.getUserName()+"'";
        String selectQuery = "DELETE FROM " + TABLE_CART_ITEMS
                + " WHERE " + whereClause;
//        Cursor c = dbRead.rawQuery(selectQuery, null);
        dbWrite.execSQL(selectQuery);
        return 0;
    }

    public ArrayList<CartItemsModel> getAllUserCartItems(String username) {
        ArrayList<CartItemsModel> cartItemsModels = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CART_ITEMS + " WHERE "+COLUMN_CART_ITEMS_USERNAME+"='"+username+"'";

        Cursor c = dbRead.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {

            do {
                CartItemsModel cartItemsModel = new CartItemsModel();

                cartItemsModel.setBranchId(c.getString(c.getColumnIndex(COLUMN_CART_ITEMS_BRANCH_ID )));
                cartItemsModel.setProductModelId(c.getString(c.getColumnIndex(COLUMN_CART_ITEMS_PRODUCT_ID )));
                cartItemsModel.setQuantity(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_CART_ITEMS_QTY ))));
                cartItemsModel.setUserName(c.getString(c.getColumnIndex(COLUMN_CART_ITEMS_QTY )));
                cartItemsModels.add(cartItemsModel);


            } while (c.moveToNext());

        }
        return cartItemsModels;
    }






}
