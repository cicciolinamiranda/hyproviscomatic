package com.uprise.ordering.camera;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.uprise.ordering.R;

/**
 * Created by cicciolina on 10/16/16.
 */
public class PhotoListViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public PhotoListViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.photo);
    }
}
