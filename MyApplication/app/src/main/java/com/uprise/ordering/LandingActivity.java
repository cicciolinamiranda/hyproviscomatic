package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends BaseAuthenticatedActivity implements View.OnClickListener {

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

//        if(loginSharedPref != null && loginSharedPref.isLoggedIn(LandingActivity.this)) {
//            startActivity(new Intent(LandingActivity.this, MainActivity.class));
//            finish();
//        }
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
}
