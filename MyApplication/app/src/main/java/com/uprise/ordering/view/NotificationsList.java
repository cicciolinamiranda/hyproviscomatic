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
import com.uprise.ordering.model.NotificationsModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cicciolina on 10/29/16.
 */

public class NotificationsList extends ArrayAdapter<NotificationsModel> {

    private final Activity context;
    private final List<NotificationsModel> web;
    private final Resources resources;
    private View rowView;

    public NotificationsList(Activity context,
                         List<NotificationsModel> web) {
    super(context, R.layout.custom_notifications_list, web);

    this.context = context;
    this.web = web;
    this.resources = context.getResources();
}

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.custom_notifications_list, null, true);
        TextView tvMsg = (TextView) rowView.findViewById(R.id.tv_notification_title);
        TextView tvDate = (TextView) rowView.findViewById(R.id.tv_notification_date);
        tvMsg.setText(web.get(position).getMessage());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        tvDate.setText(dateFormat.format(new Date(Long.parseLong(web.get(position).getDate()))));
        return rowView;
    }
}
