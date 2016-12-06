package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.NotificationsModel;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.NotificationsList;

import java.util.ArrayList;

public class NotificationActivity extends BaseAuthenticatedActivity {

    private ArrayAdapter<NotificationsModel> notificationsModelArrayAdapter;
    private ListView lvNotificationsList;
    private ArrayList<NotificationsModel> notificationsModelArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        lvNotificationsList = (ListView) findViewById(R.id.list_notifications);
        //TODO: to be replaced by REST CALL API
        notificationsModelArrayList = Util.getInstance().generateNotifications();
        notificationsModelArrayAdapter = new NotificationsList(NotificationActivity.this, notificationsModelArrayList);
        notificationsModelArrayAdapter.notifyDataSetChanged();
        lvNotificationsList.setAdapter(notificationsModelArrayAdapter);
        registerForContextMenu(lvNotificationsList);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case ApplicationConstants.RESULT_FROM_DELETING_NOTIF_MSG:

                //TODO: to be replaced by rest call api
                notificationsModelArrayAdapter.clear();
                lvNotificationsList.setAdapter(notificationsModelArrayAdapter);
                break;
        }
    }
//    }
}
