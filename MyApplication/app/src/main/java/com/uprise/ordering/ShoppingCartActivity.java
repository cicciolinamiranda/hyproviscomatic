package com.uprise.ordering;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.uprise.ordering.model.CartItemsModel;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {

    private LinearLayout llNoRecords;
    private LinearLayout llShopCartList;
    private ListView lvShoppingCartList;
    private ArrayList<CartItemsModel> cartItemsModelArrayList;
    private ArrayAdapter<CartItemsModel> cartItemsModelArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
    }
}
