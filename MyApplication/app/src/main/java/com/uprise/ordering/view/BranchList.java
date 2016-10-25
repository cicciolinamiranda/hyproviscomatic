package com.uprise.ordering.view;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uprise.ordering.R;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.ImageModel;
import com.uprise.ordering.rest.service.RestCallServices;

import java.util.List;

/**
 * Created by cicciolina on 10/20/16.
 */
public class BranchList extends ArrayAdapter<BranchModel> {

    private static int NUM_IMAGES = 3;
    private final Activity context;
    private final List<BranchModel> web;
    private final Resources resources;
    private View rowView;

    public BranchList(Activity context,
                      List<BranchModel> web) {
        super(context, R.layout.custom_branch_list, web);

        this.context = context;
        this.web = web;
        this.resources = context.getResources();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.custom_branch_list, null, true);
        TextView tvBranchName = (TextView) rowView.findViewById(R.id.tv_item_branch_name);
        TextView tvBranchAddress = (TextView) rowView.findViewById(R.id.tv_item_branch_address);
        TextView tvBranchPhone = (TextView) rowView.findViewById(R.id.tv_item_branch_contact_num);
        tvBranchName.setText(web.get(position).getName());
        tvBranchAddress.setText(web.get(position).getAddress());
        tvBranchPhone.setText(web.get(position).getContactNum());
        refreshImageList("iv_cam_permit_",R.id.ll_photo_permit_imageExcessDisplay, R.id.tv_permit_imageExcessCount, web.get(position).getPermitsPic(), ApplicationConstants.RESULT_GALLERY_PERMIT);
        refreshImageList("iv_cam_store_",R.id.ll_photo_store_imageExcessDisplay, R.id.tv_store_imageExcessCount, web.get(position).getBranchsPic(), ApplicationConstants.RESULT_GALLERY_STORE);
        return rowView;
    }

    private void refreshImageList(final String idPrefix, final int imageExcessLinearId, final int imageExcessCount, final ImageModel imageModel, final int resultCode) {
        //clear imageviews
        for (int k = 0; k < NUM_IMAGES; k++) {
            int resId = this.resources
                    .getIdentifier(idPrefix + k, "id", context.getPackageName());
            final ImageView img =(ImageView) rowView.findViewById(resId);
            img.setImageBitmap(null);
        }
        rowView.findViewById(imageExcessLinearId).setVisibility(View.GONE);

        int x = 0;
        for (int i = imageModel.getIntegerBase().size() - 1; i >= ((imageModel.getIntegerBase().size() - NUM_IMAGES) >= 0 ? (imageModel.getIntegerBase().size() - NUM_IMAGES) : 0); i--) {
            int resId = resources
                    .getIdentifier(idPrefix + x, "id", context.getPackageName());
            final ImageView img = (ImageView) rowView.findViewById(resId);
            img.setTag(i);
            img.setVisibility(View.VISIBLE);
            img.setImageBitmap(RestCallServices.getBitmapFrom( imageModel.getStringBase().get(i), resultCode));
//            img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(BranchActivity.this, CameraImageActivity.class);
//                    intent.putExtra("bitmaps", imageModel.getStringBase());
//                    intent.putExtra("display", CameraImageActivity.FULLSCREEN);
//                    intent.putExtra("imageIndex", (Integer) img.getTag());
//                    startActivityForResult(intent, resultCode);
//                }
//            });
            x++;
        }

        if (imageModel.getIntegerBase().size() > NUM_IMAGES) {
            rowView.findViewById(imageExcessLinearId).setVisibility(View.VISIBLE);
            ((TextView) rowView.findViewById(imageExcessCount)).setText("+" + (imageModel.getIntegerBase().size() - NUM_IMAGES));
        }
    }
}
