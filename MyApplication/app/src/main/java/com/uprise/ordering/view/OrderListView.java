package com.uprise.ordering.view;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.ArrayAdapter;

import com.uprise.ordering.R;
import com.uprise.ordering.model.OrderModel;

import java.util.List;

/**
 * Created by cicciolina on 11/2/16.
 */

public class OrderListView extends ArrayAdapter<OrderModel> {

    private final Activity context;
    private final List<OrderModel> web;
    private final Resources resources;
    private View rowView;

    public OrderListView(Activity context,
                             List<OrderModel> web) {
        super(context, R.layout.custom_orderlist, web);

        this.context = context;
        this.web = web;
        this.resources = context.getResources();
    }
}
