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

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.custom_orderlist, null, true);
        TextView tvOrderNumber = (TextView) rowView.findViewById(R.id.tv_order_number);
        TextView tvOrderDate = (TextView) rowView.findViewById(R.id.tv_order_date);
        TextView tvOrderStatus = (TextView) rowView.findViewById(R.id.tv_order_status);
        tvOrderNumber.setText(web.get(position).getOrderId());
        tvOrderStatus.setText(web.get(position).getOrderStatus());

        //TODO: MUST NOT BE HIDDEN. PROVIDE CORRECT DATE MAPPING
        tvOrderDate.setVisibility(View.GONE);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
//        tvOrderDate.setText(dateFormat.format(new Date(Long.parseLong(web.get(position).getDate()))));
        return rowView;
    }
}
