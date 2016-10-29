package com.uprise.ordering;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uprise.ordering.model.NotificationsModel;

public class NotificationActivity extends BaseAuthenticatedActivity {

    private ArrayAdapter<NotificationsModel> notificationsModelArrayAdapter;
    private ListView lvNotificationsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setTitle("Notifications");
    }
}
