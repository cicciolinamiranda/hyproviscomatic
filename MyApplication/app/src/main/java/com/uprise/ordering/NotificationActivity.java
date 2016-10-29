package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.NotificationsModel;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.NotificationsList;

import java.util.ArrayList;

public class NotificationActivity extends BaseAuthenticatedActivity  {

    private ArrayAdapter<NotificationsModel> notificationsModelArrayAdapter;
    private ListView lvNotificationsList;
    private ArrayList<NotificationsModel> notificationsModelArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setTitle("Notifications");

        lvNotificationsList = (ListView) findViewById(R.id.list_notifications);

        //TODO: to be replaced by REST CALL API
        notificationsModelArrayList = Util.getInstance().generateNotifications();
        notificationsModelArrayAdapter = new NotificationsList(NotificationActivity.this, notificationsModelArrayList);
//        notificationsModelArrayAdapter.notifyDataSetChanged();
        lvNotificationsList.setAdapter(notificationsModelArrayAdapter);
        registerForContextMenu(lvNotificationsList);
        lvNotificationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent notifIntent = new Intent(NotificationActivity.this, NotifMsgActivity.class);
                notifIntent.putExtra("msg", notificationsModelArrayList.get(i).getMessage());
                startActivityForResult(notifIntent, ApplicationConstants.RESULT_FROM_DELETING_NOTIF_MSG);
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(NotificationActivity.this, MainActivity.class));
                break;
        }
        return true;
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Intent notifIntent = new Intent(NotificationActivity.this, NotifMsgActivity.class);
//                notifIntent.putExtra("msg", notificationsModelArrayList.get(position).getMessage());
//                startActivityForResult(notifIntent, ApplicationConstants.RESULT_FROM_DELETING_NOTIF_MSG);
//                finish();
//    }
}
