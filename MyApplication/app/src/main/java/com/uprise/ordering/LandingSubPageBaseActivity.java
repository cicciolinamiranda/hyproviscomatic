package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.uprise.ordering.shared.LoginSharedPref;

/**
 * Created by cicciolina on 10/14/16.
 */
public class LandingSubPageBaseActivity extends AppCompatActivity {

    private LoginSharedPref loginSharedPref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginSharedPref = new LoginSharedPref();
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
}
