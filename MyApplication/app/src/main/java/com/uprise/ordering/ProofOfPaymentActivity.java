package com.uprise.ordering;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProofOfPaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinnerModeOfPayment;
    private LinearLayout llModeOfPayment;
    private LinearLayout llProofOfPaymentCamera;
    private LinearLayout llProofOfPaymentImages;
    private Button btnProofOfPaymentCamera;
    private TextView tvMaxPhotosMsg;

    private ArrayAdapter<String> modeOfPaymentSpinnerAdapter;

    private final List<String> MODE_OF_PAYMENT = new ArrayList<>(Arrays.asList("Bank Transfer", "Cash on Delivery"));

    private String modeOfPaymentStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.asset_nav_back);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText(getString(R.string.title_proof_of_payment));
        setContentView(R.layout.activity_proof_of_payment);

        llModeOfPayment = (LinearLayout) findViewById(R.id.ll_mode_of_payment_options);
        llModeOfPayment.setOnClickListener(this);
        llProofOfPaymentCamera = (LinearLayout) findViewById(R.id.ll_btn_proof_of_payment_camera);
        llProofOfPaymentImages = (LinearLayout) findViewById(R.id.ll_proof_of_payment_picture_images);
        spinnerModeOfPayment = (Spinner) findViewById(R.id.mode_of_payment_spinner);
        tvMaxPhotosMsg = (TextView) findViewById(R.id.tv_max_of_two_pic_proof_of_payment);
        btnProofOfPaymentCamera = (Button) findViewById(R.id.btn_proof_of_payment_picture_camera);
        btnProofOfPaymentCamera.setOnClickListener(this);
        modeOfPaymentSpinnerAdapter = new ArrayAdapter<>(ProofOfPaymentActivity.this, R.layout.list_item_label_spinner, MODE_OF_PAYMENT);
        modeOfPaymentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerModeOfPayment.setAdapter(modeOfPaymentSpinnerAdapter);

        setSpinnerModeOfPayment(0);
        spinnerModeOfPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerModeOfPayment(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
    }

    private void setSpinnerModeOfPayment(int position) {
        this.modeOfPaymentStr =  MODE_OF_PAYMENT.get(position);

        if(position == 1) {
            llProofOfPaymentCamera.setVisibility(View.GONE);
            llProofOfPaymentImages.setVisibility(View.GONE);
            tvMaxPhotosMsg.setVisibility(View.GONE);
        } else if(position == 0) {
            llProofOfPaymentCamera.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.activity_proof_of_payment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_submit_proof_of_payment:

                //TODO post endpoint
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.ll_mode_of_payment_options && spinnerModeOfPayment != null) {
                spinnerModeOfPayment.performClick();
        }
    }
}
