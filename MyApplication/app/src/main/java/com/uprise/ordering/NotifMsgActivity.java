package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.NotificationsModel;

public class NotifMsgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_msg);

        NotificationsModel notificationsModel = getIntent().getParcelableExtra("notificationsModel");

        if(null != notificationsModel) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.notif_msg_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(NotifMsgActivity.this, NotificationActivity.class));
                break;

            case R.id.notif_msg_action_delete:
                setResult(ApplicationConstants.RESULT_FROM_DELETING_NOTIF_MSG);
                finish();
                break;
        }
        return true;
    }
}
