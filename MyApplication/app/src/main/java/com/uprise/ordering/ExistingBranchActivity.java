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

        //TODO: add rest call api and determine if list is null. If yes, display no record layout.
        sqlDatabaseHelper = new SqlDatabaseHelper(ExistingBranchActivity.this);
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        restCallServices = new RestCallServices(ExistingBranchActivity.this);

        if(loginModel != null && loginModel.getUsername() != null) {
            restCallServices.getBranch(ExistingBranchActivity.this, this, loginModel.getToken());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.existing_branch_add_new, menu);

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
        try {
            JSONObject jsnobject = new JSONObject(string);
            JSONArray jsonArray = new JSONArray();
            if(jsnobject != null) {
                jsonArray = jsnobject.getJSONArray("results");
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
                populateBranchListView();
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    }
}