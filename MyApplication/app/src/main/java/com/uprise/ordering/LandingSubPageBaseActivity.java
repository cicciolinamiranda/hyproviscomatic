package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MenuItem;

/**
 * Created by cicciolina on 10/14/16.
 */
public class LandingSubPageBaseActivity extends BaseAuthenticatedActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent mainIntent;
        switch (item.getItemId()) {
            case android.R.id.home:
                mainIntent = new Intent(this, LandingActivity.class);
                startActivity(mainIntent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent mainIntent = new Intent(this, LandingActivity.class);
            startActivity(mainIntent);
            finish();
            return true;
        }
        return false;
    }
}
