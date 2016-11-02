package com.uprise.ordering;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.uprise.ordering.model.NotificationsModel;

public class NotifMsgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_msg);
        getSupportActionBar().setTitle("Notifications");

        NotificationsModel notificationsModel = getIntent().getParcelableExtra("notificationsModel");

        if(null != notificationsModel) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(NotifMsgActivity.this, NotificationActivity.class));
                break;
        }
        return true;
    }
}
