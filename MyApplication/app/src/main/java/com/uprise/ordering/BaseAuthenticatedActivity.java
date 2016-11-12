package com.uprise.ordering;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.LoginModel;
import com.uprise.ordering.util.Util;

/**
 * Created by cicciolina on 10/29/16.
 */

public class BaseAuthenticatedActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        {

//    protected CartItemsSharedPref cartItemsSharedPref;
//    protected LoginSharedPref loginSharedPref;

    // Google client to interact with Google API
    protected GoogleApiClient mGoogleApiClient;
    protected SqlDatabaseHelper sqlDatabaseHelper;
//    protected RestCallServices restCallServices;
    protected LoginModel loginModel;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
//        cartItemsSharedPref = new CartItemsSharedPref();
//        loginSharedPref = new LoginSharedPref();
        sqlDatabaseHelper = new SqlDatabaseHelper(BaseAuthenticatedActivity.this);
        checkPermissions();

        loginModel = sqlDatabaseHelper.getLoginCredentials();
////        if(loginSharedPref.isLoggedIn(BaseAuthenticatedActivity.this)) {
//        if(loginModel != null && loginModel.getUsername() != null) {
//            restCallServices.postLogin(BaseAuthenticatedActivity.this, this,
//                    loginModel.getUsername(), loginModel.getPassword() );
//        }

    }

    public void checkPermissions() {
        //TODO: check permissions

        if (/**  ContextCompat.checkSelfPermission(this,
         Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
         || ContextCompat.checkSelfPermission(this,
         Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
         || ContextCompat.checkSelfPermission(this,
         Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
         ||**/

                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                        ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Util.requestPermission(this);
        } else {
//            requestStatus();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent mainIntent;
//            if(loginSharedPref.isLoggedIn(BaseAuthenticatedActivity.this)){
            if(loginModel != null && loginModel.getUsername() != null) {
                mainIntent = new Intent(this, MainActivity.class);
            } else {
                mainIntent = new Intent(this, LandingActivity.class);
            }

            startActivity(mainIntent);
            finish();
            return true;
        }
        return false;
    }

//    @Override
//    public int getResultCode() {
//        return 0;
//    }
//
//    @Override
//    public void onSuccess(RestCalls callType, String token) {
//
//        if(token != null && !token.isEmpty() && loginModel != null && loginModel.getUsername() != null) {
//            loginModel.setToken(token);
//            sqlDatabaseHelper.updateLoginAccount(loginModel);
//        }
//    }
//
//    @Override
//    public void onFailure(RestCalls callType, String string) {
//
////        if(!loginSharedPref.isLoggedIn(BaseAuthenticatedActivity.this)) {
////        loginSharedPref.logOut(BaseAuthenticatedActivity.this);
////        finish();
////        startActivity(new Intent(BaseAuthenticatedActivity.this, LandingActivity.class));
////        }
//    }
}
