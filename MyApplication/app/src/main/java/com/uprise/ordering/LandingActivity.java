package com.uprise.ordering;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.uprise.ordering.shared.LoginSharedPref;

public class LandingActivity extends BaseAuthenticatedActivity implements View.OnClickListener {


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mLoginLogoView;
    private Button btnSignIn;
    private TextView tvCreateNewAccount;
    private TextView tvDistributorShop;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoginActivity.UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        getSupportActionBar().hide();

        btnSignIn =(Button) findViewById(R.id.email_sign_in_button);
        tvCreateNewAccount = (TextView) findViewById(R.id.tv_create_new_account);
        tvDistributorShop = (TextView) findViewById(R.id.tv_distributor_shop);

//        btnRegister =(Button) findViewById(R.id.btn_landing_register);
//        btnShop =(Button) findViewById(R.id.btn_landing_shop);
//
        btnSignIn.setOnClickListener(this);
        tvCreateNewAccount.setOnClickListener(this);
        tvDistributorShop.setOnClickListener(this);
//        btnRegister.setOnClickListener(this);
//        btnShop.setOnClickListener(this);

        getSupportActionBar().hide();

//        if(loginSharedPref != null && loginSharedPref.isLoggedIn(LandingActivity.this)) {
//            startActivity(new Intent(LandingActivity.this, MainActivity.class));
//            finish();
//        }


        mEmailView = (EditText) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginLogoView = findViewById(R.id.rl_login_logo);
        mProgressView = findViewById(R.id.login_loading_layout);

        loginSharedPref = new LoginSharedPref();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:
//                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
//                finish();
                attemptLogin();
                break;
            case R.id.tv_create_new_account:
                tvCreateNewAccount.setTextColor(getResources().getColor(R.color.material_orange));
                startActivity(new Intent(LandingActivity.this, RegistrationActivity.class));
                finish();
                break;
            case R.id.tv_distributor_shop:
                tvDistributorShop.setTextColor(getResources().getColor(R.color.material_orange));
                startActivity(new Intent(LandingActivity.this, DistributorShopActivity.class));
                finish();
                break;
        }
    }

//    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//
//        getLoaderManager().initLoader(0, null, this);
//    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            //TODO: WHEN API IS AVAILABLE
//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
        }


        if(!loginSharedPref.isLoggedIn(LandingActivity.this)) {
            loginSharedPref.login(LandingActivity.this, email);
            finish();
            startActivity(new Intent(LandingActivity.this, MainActivity.class));
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginLogoView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginLogoView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginLogoView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
