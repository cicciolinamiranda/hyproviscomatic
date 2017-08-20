package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.uprise.ordering.model.NotificationsModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifMsgActivity extends AppCompatActivity {

    private TextView tvNotifTitle;
    private TextView tvNotifMsg;
    private TextView tvNotifDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_msg);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.asset_nav_back);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText(getString(R.string.label_notifications));
        tvNotifTitle = (TextView) findViewById(R.id.tv_notification_title);
        tvNotifMsg = (TextView) findViewById(R.id.tv_notification_msg);
        tvNotifDate = (TextView) findViewById(R.id.tv_notification_date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

        NotificationsModel notificationsModel = getIntent().getParcelableExtra("notificationsModel");

        if(null != notificationsModel) {
            if(notificationsModel.getTitle() != null) tvNotifTitle.setText(notificationsModel.getTitle());
            if(notificationsModel.getMessage() != null) tvNotifMsg.setText(notificationsModel.getMessage());
            if(notificationsModel.getDate() != null) {
                long dv = Long.valueOf(notificationsModel.getDate())*1000;
                tvNotifDate.setText(dateFormat.format(new Date(dv)));
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater mi = getMenuInflater();
//        mi.inflate(R.menu.notif_msg_menu, menu);
//
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(NotifMsgActivity.this, NotificationActivity.class));
                break;

//            case R.id.notif_msg_action_delete:
//                setResult(ApplicationConstants.RESULT_FROM_DELETING_NOTIF_MSG);
//                finish();
//                break;
        }
        return true;
    }
}
