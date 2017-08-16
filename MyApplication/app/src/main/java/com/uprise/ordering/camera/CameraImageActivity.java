package com.uprise.ordering.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.uprise.ordering.AddBranchActivity;
import com.uprise.ordering.R;
import com.uprise.ordering.constant.ApplicationConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicciolina on 10/16/16.
 */
public class CameraImageActivity extends AppCompatActivity implements
        OnFragmentInteractionListener, PhotoFullScreenFragment.OnDeletePhotoListener{

    private List<String> images;
    private int display;
    private int resultCode;

    public final static int FULLSCREEN = 1020;
    public final static int GRID = 1021;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_image);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Photos");
        actionBar.setElevation(0);

        Intent intent = getIntent();
        images = intent.getStringArrayListExtra("bitmaps");
        display = intent.getIntExtra("display", 0);
        resultCode = intent.getIntExtra("resultCode", 0);

        Fragment frag = null;

        if(display == FULLSCREEN) {
            Log.d(ApplicationConstants.APP_CODE,"imageIndex: "+intent.getIntExtra("imageIndex", 0));
            frag =
                    PhotoFullScreenFragment.newInstance(intent.getIntExtra("imageIndex", 0),
                            images.get(intent.getIntExtra("imageIndex", 0)),resultCode);
            ((PhotoFullScreenFragment)frag).setDeletePhotoListener(this);
        }else
            frag = PhotoListFragment.newInstance(images, "",resultCode);

        renderFragment(frag);
    }

    @Override
    public void onDeletePhoto(boolean deleted, String imgUri) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("bitmaps", new ArrayList<>(images));
        setResult(RESULT_OK, intent);
        if(display == FULLSCREEN) {
//      frag =
//        PhotoFullScreenFragment.newInstance(this.getIntent().getStringExtra("imageUri"), "");
//      ((PhotoFullScreenFragment)frag).setDeletePhotoListener(this);
            this.finish();
        }else {
            onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public List<String> getImages() {
        return images;
    }

    private void renderFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.see_all_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("bitmaps", new ArrayList<>(images));
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.delete:


//                String DATA_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "wasabi" + File.separator;
                String imageUri = this.getIntent().getStringExtra("imageUri");
                if(imageUri == null || imageUri.isEmpty()){
                    imageUri = PhotoFullScreenFragment.imageUri;
                }

                imageUri = ApplicationConstants.DATA_STORAGE_STORE_PATH + imageUri;

                if(resultCode == ApplicationConstants.RESULT_GALLERY_PERMIT) imageUri = ApplicationConstants.DATA_STORAGE_PERMIT_PATH + imageUri;
                File file = new File(imageUri);
                file.setWritable(true);
                boolean deleted = file.delete();
                if(deleted && resultCode == ApplicationConstants.RESULT_GALLERY_STORE){
                Log.d("Access:AssetMgmt", "Photo Store successfully deleted: " + imageUri);
                    AddBranchActivity.totalStoreImages--;

                 }
                else if(deleted && resultCode == ApplicationConstants.RESULT_GALLERY_PERMIT){
                    Log.d("Access:AssetMgmt", "Photo PERMIT successfully deleted: " + imageUri);
                    AddBranchActivity.totalPermitImages--;
                }
                else {
                    Log.d("Access:AssetMgmt", "Photo failed to be deleted: " + imageUri);
                }
                //deletePhotoListener.onDeletePhoto(deleted);

                images.remove(PhotoFullScreenFragment.imageIndex);

                this.onDeletePhoto(deleted, imageUri);
                break;
        }
        return true;
    }
}
