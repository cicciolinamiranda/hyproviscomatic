package com.uprise.ordering;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.RegistrationModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.BranchList;
import com.uprise.ordering.view.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends LandingSubPageBaseActivity implements View.OnClickListener, RestCallServices.RestServiceListener {

    private static int NUM_IMAGES = 3;

    private EditText shopName;
    private EditText shopAddress;
    private EditText contactNum;
    private EditText shippingAddress;
    private EditText email;
    private EditText password;
//    private ImageButton btnPicsOfStore;
//    private ImageButton btnPicsOfPermit;
//    private LinearLayout llPicsOfStore;
//    private LinearLayout llPicsOfPermit;
    View focusView = null;
    private Button btnSubmit;
    private Button btnAddBranch;
    private BranchModel branchModel;
    private List<BranchModel> branchModelList;
    private ArrayAdapter<BranchModel> adapterBranchModelList;
    private ExpandableHeightListView listViewBranch;
    private BranchModel editBranchModel;
    private int editId;
    private int editResultCode;
    private RestCallServices restCallServices;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setTitle("Registration");

        shopName =(EditText) findViewById(R.id.et_reg_shopname);
        shopAddress=(EditText) findViewById(R.id.et_reg_shopaddress);
        contactNum=(EditText) findViewById(R.id.et_reg_shop_contact_num);
        shippingAddress=(EditText) findViewById(R.id.et_reg_shop_shipping_address);
        email = (EditText) findViewById(R.id.et_reg_email);
        password = (EditText) findViewById(R.id.et_reg_password);
        btnAddBranch = (Button) findViewById(R.id.btn_add_branch);
        btnAddBranch.setOnClickListener(this);
        btnSubmit=(Button) findViewById(R.id.btn_reg_submit);
        btnSubmit.setOnClickListener(this);
        listViewBranch = (ExpandableHeightListView) findViewById(R.id.list_reg_branch);
        listViewBranch.setExpanded(true);
        branchModelList = new ArrayList<>();

        restCallServices = new RestCallServices(this);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_reg_submit:
                //TODO: More work. Call Api when available

                if(isFormCanBeSaved()) {
                    RegistrationModel registrationModel = new RegistrationModel();

                    registrationModel.setBranchModels(branchModelList);
                    registrationModel.setContactNum(contactNum.getText().toString());
                    registrationModel.setEmail(email.getText().toString());
                    registrationModel.setPassword(password.getText().toString());
                    registrationModel.setShippingAddress(shippingAddress.getText().toString());
                    registrationModel.setShopName(shopName.getText().toString());
                    registrationModel.setShopAddress(shopAddress.getText().toString());
                    restCallServices.postRegistration(RegistrationActivity.this, registrationModel, this);
                    progressDialog = ProgressDialog.show(this, getString(R.string.registration_inp), String.format(getString(R.string.currently_registering), shopName.getText().toString()));

                }

//                Util.getInstance().showSnackBarToast(RegistrationActivity.this, "Registration Submitted");
//                startActivity(new Intent(RegistrationActivity.this, LandingActivity.class));
//                finish();
                break;
            case R.id.btn_add_branch:
                showAddBranchDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Access:AssetMgmt",
                "onActivityResult requestCode" + requestCode + " resultCode " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case ApplicationConstants.RESULT_FROM_ADD_BRANCH:
                if (resultCode != RESULT_CANCELED && data != null) {
                    branchModel = new BranchModel();
                    branchModel = data.getParcelableExtra("branchModel");
                    branchModelList.add(branchModel);
                    populateBranchListView();
                }
                break;
            case ApplicationConstants.RESULT_EDIT_BRANCH:
                if (resultCode != RESULT_CANCELED && data != null)  {
                    editBranchModel = data.getParcelableExtra("branchModel");
                    editId = data.getIntExtra("id",0);
                    editResultCode = data.getIntExtra("resultCode",0);

                    if(editResultCode == ApplicationConstants.RESULT_EDIT_BRANCH) {
                        branchModelList.set(editId, editBranchModel);
                        populateBranchListView();
                    }
                }
                break;
        }

    }

    private void showAddBranchDialog() {
            Intent intent = new Intent(RegistrationActivity.this, AddBranchActivity.class);
            startActivityForResult(intent, ApplicationConstants.RESULT_FROM_ADD_BRANCH);
//            finish();
    }

    private void populateBranchListView() {
        adapterBranchModelList = new BranchList(this, branchModelList);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listViewBranch.setAdapter(adapterBranchModelList);
                adapterBranchModelList.notifyDataSetChanged();
                registerForContextMenu(listViewBranch);
            }
        });
        listViewBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, final long l) {

                AlertDialog.Builder listViewDialog = new AlertDialog.Builder(
                        RegistrationActivity.this);
                listViewDialog.setPositiveButton("Edit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent editBranch = new Intent(RegistrationActivity.this, AddBranchActivity.class);
                                editBranch.putExtra("branchModel", branchModelList.get(i));
                                editBranch.putExtra("id",i);
                                editBranch.putExtra(("resultCode"), ApplicationConstants.RESULT_EDIT_BRANCH);
                                startActivityForResult(editBranch, ApplicationConstants.RESULT_EDIT_BRANCH );

//                                            finish();
                            }
                        });

                listViewDialog.setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                branchModelList.remove(branchModelList.get(i));
                                adapterBranchModelList = new BranchList(RegistrationActivity.this, branchModelList);
                                adapterBranchModelList.notifyDataSetChanged();
                                listViewBranch.setAdapter(adapterBranchModelList);
                            }
                        });
                listViewDialog.show();
            }
        });
    }


    @Override
    public int getResultCode() {
        return 0;
    }

    @Override
    public void onSuccess(RestCalls callType, String string) {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        Util.getInstance().showDialog(this, "Registration Submitted. Please wait" +
                "for your Email Notification for its approval", this.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        startActivity(getIntent());
        finish();
    }

    @Override
    public void onFailure(RestCalls callType, String string) {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        Util.getInstance().showDialog(this, string+getString(R.string.try_again), this.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
//        Util.getInstance().showSnackBarToast(RegistrationActivity.this, getString(R.string.unable_to_register));
    }

    private boolean isFormCanBeSaved() {
        boolean canBeAdded = true;

        if (shopName.getText().toString().isEmpty()) {
            shopName.setError(RegistrationActivity.this.getString(R.string.is_required));
            canBeAdded = false;
        }
        if (shopAddress.getText().toString().isEmpty()) {
            shopAddress.setError(RegistrationActivity.this.getString(R.string.is_required));
            canBeAdded = false;
        }

        if (shippingAddress.getText().toString().isEmpty()) {
            shippingAddress.setError(RegistrationActivity.this.getString(R.string.is_required));
            canBeAdded = false;
        }

        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            canBeAdded = false;
        } else if (!isEmailValid(emailStr)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            canBeAdded = false;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            canBeAdded = false;
        }

        if(branchModelList == null || branchModelList.isEmpty()) {
            Util.getInstance().showDialog(this, "Please add atleast one(1) Branch", this.getString(R.string.action_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            canBeAdded = false;
        }

        if(canBeAdded && focusView != null) focusView.requestFocus();

        return canBeAdded;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
