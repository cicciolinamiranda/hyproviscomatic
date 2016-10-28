package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.uprise.ordering.fragment.ProductsFragment;
import com.uprise.ordering.shared.LoginSharedPref;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProductsFragment productsFragment;
    private LoginSharedPref loginSharedPref;
    private View headerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView tvEmail = (TextView) headerLayout.findViewById(R.id.tv_nav_header_username);
//        FragmentTransaction tx =  getSupportFragmentManager().beginTransaction();
//        productsFragment = new ProductsFragment();
//            tx.replace(R.id.content_frame, productsFragment);
//        tx.commit();
        productsFragment = (ProductsFragment) getSupportFragmentManager().findFragmentById(R.id.frag_products);
        loginSharedPref = new LoginSharedPref();
        if(!loginSharedPref.isLoggedIn(MainActivity.this)) {
            startActivity(new Intent(MainActivity.this, LandingActivity.class));
            finish();
        }

        tvEmail.setText(loginSharedPref.getUsername(MainActivity.this).toString());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch(item.getItemId()) {

            case R.id.nav_shopping_cart:
                finish();
                startActivity(new Intent(MainActivity.this, ShoppingCartActivity.class));
                break;
            case R.id.nav_logout:

                if(loginSharedPref.isLoggedIn(MainActivity.this)) {
                    loginSharedPref.logOut(MainActivity.this);
                    finish();
                    startActivity(new Intent(MainActivity.this, LandingActivity.class));
                }
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);


    }
}
