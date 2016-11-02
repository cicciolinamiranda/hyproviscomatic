package com.uprise.ordering;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.RegistrationModel;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.BranchList;
import com.uprise.ordering.view.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends LandingSubPageBaseActivity implements View.OnClickListener {

    private static int NUM_IMAGES = 3;

    private EditText shopName;
    private EditText shopAddress;
    private EditText contactNum;
    private EditText shippingAddress;
    private ImageButton btnPicsOfStore;
    private ImageButton btnPicsOfPermit;
    private LinearLayout llPicsOfStore;
    private LinearLayout llPicsOfPermit;
    private Button btnSubmit;
    private Button btnAddBranch;
    private RegistrationModel registrationModel;
    private BranchModel branchModel;
    private List<BranchModel> branchModelList;
    private ArrayAdapter<BranchModel> adapterBranchModelList;
    private ExpandableHeightListView listViewBranch;
    private BranchModel editBranchModel;
    private int editId;
    private int editResultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setTitle("Registration");

        shopName =(EditText) findViewById(R.id.et_reg_shopname);
        shopAddress=(EditText) findViewById(R.id.et_reg_shopaddress);
        contactNum=(EditText) findViewById(R.id.et_reg_shop_contact_num);
        shippingAddress=(EditText) findViewById(R.id.et_reg_shop_shipping_address);
        btnAddBranch = (Button) findViewById(R.id.btn_add_branch);
        btnAddBranch.setOnClickListener(this);
        btnSubmit=(Button) findViewById(R.id.btn_reg_submit);
        btnSubmit.setOnClickListener(this);
        listViewBranch = (ExpandableHeightListView) findViewById(R.id.list_reg_branch);
        listViewBranch.setExpanded(true);
        registrationModel = new RegistrationModel();
        branchModelList = new ArrayList<>();




    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_reg_submit:
                //TODO: More work. Call Api when available
                Util.getInstance().showSnackBarToast(RegistrationActivity.this, "Registration Submitted");
                startActivity(new Intent(RegistrationActivity.this, LandingActivity.class));
                finish();
                break;
            case R.id.ll_photo_store_imageExcessDisplay:
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
//                    adapterBranchModelList = new BranchList(this, branchModelList);
//
//                    this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            listViewBranch.setAdapter(adapterBranchModelList);
//                            adapterBranchModelList.notifyDataSetChanged();
//                            registerForContextMenu(listViewBranch);
//                        }
//                    });
//                    listViewBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, final View view, final int i, final long l) {
//
//                            AlertDialog.Builder listViewDialog = new AlertDialog.Builder(
//                                    RegistrationActivity.this);
//                            listViewDialog.setPositiveButton("Edit",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface arg0, int arg1) {
//                                            Intent editBranch = new Intent(RegistrationActivity.this, BranchActivity.class);
//                                            editBranch.putExtra("branchModel", branchModelList.get(i));
//                                            editBranch.putExtra("id",i);
//                                            editBranch.putExtra(("resultCode"), ApplicationConstants.RESULT_EDIT_BRANCH);
//                                            setResult(RESULT_OK, editBranch );
////                                            finish();
//                                        }
//                                    });
//
//                            listViewDialog.setNegativeButton("Delete",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface arg0, int arg1) {
//                                            branchModelList.remove(branchModelList.get(i));
//                                            adapterBranchModelList = new BranchList(RegistrationActivity.this, branchModelList);
//                                            adapterBranchModelList.notifyDataSetChanged();
//                                            listViewBranch.setAdapter(adapterBranchModelList);
//                                        }
//                                    });
//                            listViewDialog.show();
//                        }
//                    });
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
        if(shopName.getText().toString().isEmpty()) {
            shopName.setError("Shopname is required in adding a branch");
        }
        else {
            Intent intent = new Intent(RegistrationActivity.this, AddBranchActivity.class);
            intent.putExtra("shopName", shopName.getText().toString());
            startActivityForResult(intent, ApplicationConstants.RESULT_FROM_ADD_BRANCH);
//            finish();
        }
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


}
