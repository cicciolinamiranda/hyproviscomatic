package com.uprise.ordering.view;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.SorterBrandModel;

import java.util.List;

/**
 * Created by cicciolina on 12/8/16.
 */

public class SortedBrandNamesList extends ArrayAdapter<SorterBrandModel> {

    private List<SorterBrandModel> sorterBrandModelList;
    private Activity context;
    private Resources resources;
    private View rowView;

    public SortedBrandNamesList(Activity context,
                                List<SorterBrandModel> sorterBrandModelList) {
        super(context, R.layout.custom_sorted_brand_names, sorterBrandModelList);

        this.context = context;
        this.resources = context.getResources();
        this.sorterBrandModelList = sorterBrandModelList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.custom_sorted_brand_names, null, true);
        TextView tvBrandName = (TextView) rowView.findViewById(R.id.tv_brand_name_label);
        tvBrandName.setText(sorterBrandModelList.get(position).getName());
        ImageView rightBtn = (ImageView) rowView.findViewById(R.id.ib_brand_name_arrow_right);
        rightBtn.setColorFilter(resources.getColor(R.color.colorPrimary));
        return rowView;
    }
}
