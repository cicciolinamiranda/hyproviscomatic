package com.uprise.ordering.view;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uprise.ordering.R;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.OrderModel;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.custom_orderlist, null, true);

        if (position % 2 == 1) {
            rowView.setBackgroundColor(resources.getColor(R.color.colorAccent));
        } else {
            rowView.setBackgroundColor(resources.getColor(R.color.colorAccentDark));
        }

        TextView tvOrderNumber = (TextView) rowView.findViewById(R.id.tv_order_number);
        TextView tvOrderDate = (TextView) rowView.findViewById(R.id.tv_order_date);
        TextView tvOrderStatus = (TextView) rowView.findViewById(R.id.tv_order_status);
        TextView tvOrderAmount = (TextView) rowView.findViewById(R.id.tv_order_amount);
        TextView tvOrderDiscount = (TextView) rowView.findViewById(R.id.tv_order_discount);
        TextView tvOrderShippingFee = (TextView) rowView.findViewById(R.id.tv_order_shipping_fee);


        tvOrderAmount.setText(String.format("%.2f",web.get(position).getTotalAmount()) + " Php");
        tvOrderDiscount.setText(String.format("%.2f",web.get(position).getDiscount()) + " Php");
        tvOrderShippingFee.setText(String.format("%.2f",web.get(position).getShippingFee()) + " Php");

        tvOrderNumber.setText(web.get(position).getOrderId());
        tvOrderStatus.setText(ApplicationConstants.PURCHASE_STATUS.get(web.get(position).getOrderStatus()));


        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

        long dv = Long.valueOf(web.get(position).getDate())*1000;
        tvOrderDate.setText(dateFormat.format(new Date(dv))+" | "+timeFormat.format(new Date(dv)));
        return rowView;
    }
}
