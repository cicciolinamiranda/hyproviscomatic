package com.uprise.ordering;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.view.BranchList;
import com.uprise.ordering.view.ExpandableHeightListView;

import java.util.List;

public class ExistingBranch extends AppCompatActivity {

    private LinearLayout llNoBranchAvail;
    private LinearLayout llExistingBranch;
    private List<BranchModel> branchModelList;
    private ArrayAdapter<BranchModel> adapterBranchModelList;
    private ExpandableHeightListView listViewBranch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_branch);
        getSupportActionBar().setTitle("Branches");

        llNoBranchAvail =(LinearLayout) findViewById(R.id.ll_existing_branch_no_records);
        llExistingBranch =(LinearLayout) findViewById(R.id.ll_existing_branch);

        llNoBranchAvail.setVisibility(View.VISIBLE);
        llExistingBranch.setVisibility(View.GONE);

        //TODO: add rest call api and determine if list is null. If yes, display no record layout.
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
                startActivity(new Intent(ExistingBranch.this, MainActivity.class));
                break;

            case R.id.existing_branch_add_new:
                setResult(ApplicationConstants.RESULT_FROM_ADD_BRANCH);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {

            case ApplicationConstants.RESULT_FROM_ADD_BRANCH:
                if (resultCode != RESULT_CANCELED && data != null) {
                    BranchModel branchModel = data.getParcelableExtra("branchModel");
                    branchModelList.add(branchModel);
                    populateBranchListView();
                }
                break;
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
                        ExistingBranch.this);
                listViewDialog.setPositiveButton("Edit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent editBranch = new Intent(ExistingBranch.this, AddBranchActivity.class);
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
                                adapterBranchModelList = new BranchList(ExistingBranch.this, branchModelList);
                                adapterBranchModelList.notifyDataSetChanged();
                                listViewBranch.setAdapter(adapterBranchModelList);
                            }
                        });
                listViewDialog.show();
            }
        });
    }
}
