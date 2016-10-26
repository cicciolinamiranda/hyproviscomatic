package com.uprise.ordering;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignIn;
    private Button btnRegister;
    private Button btnShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        getSupportActionBar().hide();

        btnSignIn =(Button) findViewById(R.id.btn_landing_sign_in);
        btnRegister =(Button) findViewById(R.id.btn_landing_register);
        btnShop =(Button) findViewById(R.id.btn_landing_shop);

        btnSignIn.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnShop.setOnClickListener(this);

        getSupportActionBar().hide();
        checkPermissions();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_landing_sign_in:
                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.btn_landing_register:
                startActivity(new Intent(LandingActivity.this, RegistrationActivity.class));
                finish();
                break;
            case R.id.btn_landing_shop:
//                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                finish();
                break;
        }
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
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(this);
        } else {
//            requestStatus();
        }
    }


    public static void requestPermission(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

    }
}
