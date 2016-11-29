package com.uprise.ordering;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.LoginModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.ExistingBranchList;
import com.uprise.ordering.view.ExpandableHeightListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExistingBranchActivity extends BaseAuthenticatedActivity implements
        RestCallServices.RestServiceListener{

    private LinearLayout llNoBranchAvail;
    private LinearLayout llExistingBranch;
    private List<BranchModel> branchModelList;
    private ArrayAdapter<BranchModel> adapterBranchModelList;
    private ExpandableHeightListView listViewBranch;
    private RestCallServices restCallServices;
    private RelativeLayout loadingLayout;
    private MenuItem previousMenu;
    private MenuItem nextMenu;
    private String nextUrl;
    private String prevUrl;
    private boolean isNotFromFirstPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_branch);

        llNoBranchAvail =(LinearLayout) findViewById(R.id.ll_existing_branch_no_records);
        llExistingBranch =(LinearLayout) findViewById(R.id.ll_existing_branch);
        listViewBranch = (ExpandableHeightListView) findViewById(R.id.list_existing_branch);
        loadingLayout = (RelativeLayout) findViewById(R.id.rl_branch_list_loading_layout);
        listViewBranch.setExpanded(true);
        branchModelList = new ArrayList<>();

        llNoBranchAvail.setVisibility(View.GONE);
        llExistingBranch.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);

        loginModel = new LoginModel();
        sqlDatabaseHelper = new SqlDatabaseHelper(ExistingBranchActivity.this);
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        restCallServices = new RestCallServices(ExistingBranchActivity.this);
        final String branchEndpoint = getResources().getString(R.string.endpoint_server)
                + getResources().getString(R.string.endpoint_get_branch);

        if(loginModel != null && loginModel.getUsername() != null) {
            restCallServices.getBranch(ExistingBranchActivity.this, this, loginModel.getToken(), branchEndpoint);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.existing_branch_add_new, menu);
        previousMenu = menu.findItem(R.id.menu_orderlist_prev);
        previousMenu.setVisible(false);
        nextMenu = menu.findItem(R.id.menu_orderlist_next);
        nextMenu.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(ExistingBranchActivity.this, MainActivity.class));
                break;

            case R.id.existing_branch_add_new:
                showAddBranchDialog();
                break;

            case R.id.menu_orderlist_prev:
                llExistingBranch.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                branchModelList = new ArrayList<>();
                restCallServices.getBranch(ExistingBranchActivity.this, this, loginModel.getToken(), prevUrl);
                break;
            case R.id.menu_orderlist_next:
                llExistingBranch.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                branchModelList = new ArrayList<>();
                restCallServices.getBranch(ExistingBranchActivity.this, this,loginModel.getToken(), nextUrl);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {

            case ApplicationConstants.RESULT_FROM_ADD_BRANCH_SIGNED_IN:
                if (resultCode != RESULT_CANCELED && data != null) {
                    BranchModel branchModel = data.getParcelableExtra("branchModel");
//                    branchModelList.add(branchModel);

//                    restCallServices.saveBranchToExistingUser(ExistingBranchActivity.this, this, branchModel, loginModel);
                    populateBranchListView();
                }
                break;
        }
    }

    private void populateBranchListView() {
        adapterBranchModelList = new ExistingBranchList(this, branchModelList);
        if(branchModelList != null && !branchModelList.isEmpty()) {
            llNoBranchAvail.setVisibility(View.GONE);
            llExistingBranch.setVisibility(View.VISIBLE);
        } else {
            llNoBranchAvail.setVisibility(View.VISIBLE);
            llExistingBranch.setVisibility(View.GONE);
        }
        loadingLayout.setVisibility(View.GONE);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listViewBranch.setAdapter(adapterBranchModelList);
                adapterBranchModelList.notifyDataSetChanged();
                registerForContextMenu(listViewBranch);
            }
        });
    }

    private void showAddBranchDialog() {
            Intent intent = new Intent(ExistingBranchActivity.this, AddBranchActivity.class);
            intent.putExtra("resultCode" , ApplicationConstants.RESULT_FROM_ADD_BRANCH_SIGNED_IN);
            startActivityForResult(intent, ApplicationConstants.RESULT_FROM_ADD_BRANCH_SIGNED_IN);
    }

    @Override
    public int getResultCode() {
        return 0;
    }

    @Override
    public void onSuccess(RestCalls callType, String string) {
        nextMenu.setVisible(false);
        previousMenu.setVisible(false);

        try {
            JSONObject jsnobject = new JSONObject(string);
            JSONArray jsonArray = new JSONArray();
            if(jsnobject != null) {
                jsonArray = jsnobject.getJSONArray("results");
            }

            if(jsnobject.getString("next") != null && !jsnobject.getString("next").isEmpty() && !jsnobject.getString("next").contentEquals("null"))  {

                nextUrl = jsnobject.getString("next");
                nextMenu.setVisible(true);
            }
            if(!isNotFromFirstPage && jsnobject.getString("previous") != null && !jsnobject.getString("previous").isEmpty()
                    && !jsnobject.getString("previous").contentEquals("null")) {
                prevUrl = jsnobject.getString("previous");
                previousMenu.setVisible(true);
            }


            if(jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    if(jsonArray.getJSONObject(i) != null) {

                        if (jsonArray.getJSONObject(i).getString("user") != null &&
                                !jsonArray.getJSONObject(i).getString("user").isEmpty()
                                && jsonArray.getJSONObject(i).getString("user").contentEquals(loginModel.getUsername())) {
                            branchModelList.add(Util.getInstance().generateBranchModelFromJson(jsonArray.getJSONObject(i)));

                        }
                    }

                }

                if(branchModelList.isEmpty() && nextUrl != null) {
                    restCallServices.getBranch(ExistingBranchActivity.this, this,loginModel.getToken(), nextUrl);
                    isNotFromFirstPage = true;
                } else {
                    populateBranchListView();
                    isNotFromFirstPage = false;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            checkIfTheresData();
        }
    }

    @Override
    public void onFailure(RestCalls callType, String string) {
        Util.getInstance().showDialog(this, string, this.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        checkIfTheresData();

    }

    private void checkIfTheresData() {
        if(branchModelList != null && !branchModelList.isEmpty()) {
            llNoBranchAvail.setVisibility(View.GONE);
            llExistingBranch.setVisibility(View.VISIBLE);
        } else {
            llNoBranchAvail.setVisibility(View.VISIBLE);
            llExistingBranch.setVisibility(View.GONE);
        }
        loadingLayout.setVisibility(View.GONE);
    }
}
