package com.uprise.ordering.camera;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uprise.ordering.R;
import com.uprise.ordering.camera.PhotosAdapter.PhotoListViewHolder;
import com.uprise.ordering.rest.service.RestCallServices;

import java.util.List;

/**
 * Created by cicciolina on 10/16/16.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotoListViewHolder> {

    private Context mContext;
    private List<String> photoList;
    private int resId;
    private View layoutView;
    private OnItemClickedListener itemClickedListener;
    private int resultCode;

    public OnItemClickedListener getItemClickedListener() {
        return itemClickedListener;
    }

    public PhotosAdapter(Context mContext, int resId, List<String> photoList, int resultCode) {
        this.mContext = mContext;
        this.photoList = photoList;
        this.resId = resId;
        this.resultCode = resultCode;
    }

    public void setItemClickedListener(
            OnItemClickedListener itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    @Override
    public PhotoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(resId, null);
        PhotoListViewHolder view = new PhotoListViewHolder(layoutView);
        return view;
    }

    @Override
    public void onBindViewHolder(PhotoListViewHolder holder, final int position) {

        holder.imageView.setImageBitmap(RestCallServices.getBitmapFrom(photoList.get(position), resultCode));
        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickedListener != null) {
                    itemClickedListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    class PhotoListViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public PhotoListViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.photo);
        }
    }
}
