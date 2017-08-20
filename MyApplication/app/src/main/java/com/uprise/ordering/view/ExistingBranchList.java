package com.uprise.ordering.view;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.BranchModel;

import java.util.List;

/**
 * Created by cicciolina on 11/12/16.
 */

public class ExistingBranchList extends ArrayAdapter<BranchModel> {

    private final Activity context;
    private final List<BranchModel> web;
    private final Resources resources;
    private View rowView;

    public ExistingBranchList(Activity context,
                      List<BranchModel> web) {
        super(context, R.layout.custom_existing_branch_list, web);

        this.context = context;
        this.web = web;
        this.resources = context.getResources();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.custom_existing_branch_list, null, true);
        TextView tvBranchName = (TextView) rowView.findViewById(R.id.tv_existing_branch_name);
        TextView tvBranchAddress = (TextView) rowView.findViewById(R.id.tv_existing_branch_address);
        TextView tvBranchContactNum = (TextView) rowView.findViewById(R.id.tv_existing_branch_contact_num);

        if (position % 2 == 1) {
            rowView.setBackgroundColor(resources.getColor(R.color.colorAccent));
        } else {
            rowView.setBackgroundColor(resources.getColor(R.color.colorAccentDark));
        }

        tvBranchName.setText(web.get(position).getName());
        tvBranchAddress.setText(web.get(position).getAddress());
        tvBranchContactNum.setText(web.get(position).getContactNum());
    return rowView;
    }
}
