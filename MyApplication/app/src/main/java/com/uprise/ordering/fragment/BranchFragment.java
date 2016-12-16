package com.uprise.ordering.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.uprise.ordering.R;
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

/**
 * Created by cicciolina on 12/16/16.
 */

public class BranchFragment extends Fragment implements
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
    private View rowView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rowView = inflater.inflate(R.layout.activity_existing_branch, container, false);
        llNoBranchAvail =(LinearLayout) rowView.findViewById(R.id.ll_existing_branch_no_records);
        llExistingBranch =(LinearLayout) rowView.findViewById(R.id.ll_existing_branch);
        listViewBranch = (ExpandableHeightListView) rowView.findViewById(R.id.list_existing_branch);
        loadingLayout = (RelativeLayout) rowView.findViewById(R.id.rl_branch_list_loading_layout);
        listViewBranch.setExpanded(true);
        branchModelList = new ArrayList<>();

        restCallServices = new RestCallServices(getActivity());

        final String branchEndpoint = getResources().getString(R.string.endpoint_server)
                + getResources().getString(R.string.endpoint_get_branch);

        if(Util.getInstance().isNetworkAvailable(getActivity())) {
            restCallServices.getResellers(getActivity(), this, branchEndpoint);
            llNoBranchAvail.setVisibility(View.GONE);
            llExistingBranch.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            setHasOptionsMenu(true);
        } else {
            llNoBranchAvail.setVisibility(View.VISIBLE);
        }
        return rowView;
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
                            branchModelList.add(Util.getInstance().generateBranchModelFromJson(jsonArray.getJSONObject(i)));
                    }

                }

                if(branchModelList.isEmpty() && nextUrl != null) {
                    restCallServices.getResellers(getActivity(), this, nextUrl);
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

    private void populateBranchListView() {
        adapterBranchModelList = new ExistingBranchList(getActivity(), branchModelList);
        if(branchModelList != null && !branchModelList.isEmpty()) {
            llNoBranchAvail.setVisibility(View.GONE);
            llExistingBranch.setVisibility(View.VISIBLE);
        } else {
            llNoBranchAvail.setVisibility(View.VISIBLE);
            llExistingBranch.setVisibility(View.GONE);
        }
        loadingLayout.setVisibility(View.GONE);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listViewBranch.setAdapter(adapterBranchModelList);
                adapterBranchModelList.notifyDataSetChanged();
                registerForContextMenu(listViewBranch);
            }
        });
    }

    @Override
    public void onFailure(RestCalls callType, String string) {
        Util.getInstance().showDialog(getActivity(), string, this.getString(R.string.action_ok),
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pagination_menu, menu);
        previousMenu = menu.findItem(R.id.menu_orderlist_prev);
        previousMenu.setVisible(false);
        nextMenu = menu.findItem(R.id.menu_orderlist_next);
        nextMenu.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                switch (item.getItemId()) {

            case R.id.menu_orderlist_prev:
                llExistingBranch.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                branchModelList = new ArrayList<>();
                restCallServices.getResellers(getContext(), this, prevUrl);
                break;
            case R.id.menu_orderlist_next:
                llExistingBranch.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                branchModelList = new ArrayList<>();
                restCallServices.getResellers(getContext(), this, nextUrl);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        restCallServices = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restCallServices = null;
    }
}
