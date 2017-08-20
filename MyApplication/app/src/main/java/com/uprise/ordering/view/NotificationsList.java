package com.uprise.ordering.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uprise.ordering.NotifMsgActivity;
import com.uprise.ordering.R;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.NotificationsModel;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.custom_notifications_list, null, true);

        if (position % 2 == 0) {
            rowView.setBackgroundColor(resources.getColor(R.color.colorAccent));
        } else {
            rowView.setBackgroundColor(resources.getColor(R.color.colorAccentDark));
        }

        TextView tvTitle = (TextView) rowView.findViewById(R.id.tv_notification_title);
        TextView tvMsg = (TextView) rowView.findViewById(R.id.tv_notification_content);
        LinearLayout llItemNotif = (LinearLayout) rowView.findViewById(R.id.ll_item_notifications);
        tvTitle.setText(web.get(position).getTitle());
        tvMsg.setText(web.get(position).getMessage());
        llItemNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notifIntent = new Intent(context, NotifMsgActivity.class);
                notifIntent.putExtra("notificationsModel", web.get(position));
                context.startActivityForResult(notifIntent, ApplicationConstants.RESULT_FROM_DELETING_NOTIF_MSG);
//                context.finish();
            }
        });
        return rowView;
    }


}
