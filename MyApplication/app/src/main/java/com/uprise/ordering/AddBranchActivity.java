package com.uprise.ordering;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.uprise.ordering.camera.CameraImageActivity;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.ImageModel;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddBranchActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageModel imageStoreModel;
    private ImageModel imagePermitModel;
//    private String shopName;
    private static int NUM_IMAGES = 3;
    private String photoFile;
    public static int totalStoreImages = 0;
    public static int totalPermitImages = 0;
    private EditText etBranchName;
    private EditText etBranchAdd;
    private  EditText etBranchPhone;
    private Button btnAddBranch;
    private ImageButton btnPicsOfStore;
    private  ImageButton btnPicsOfPermit;
    private int selectedBranchId;
    private int resultCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_branch);
        imageStoreModel  = new ImageModel(new ArrayList<Integer>(), new ArrayList<String>(),0);
        imagePermitModel = new ImageModel(new ArrayList<Integer>(), new ArrayList<String>(),0);
//        shopName = getIntent().getStringExtra("shopName");
        etBranchName = (EditText) findViewById(R.id.et_dialog_add_branch_name);
        etBranchAdd = (EditText) findViewById(R.id.et_dialog_add_branch_address);
        etBranchPhone = (EditText) findViewById(R.id.et_dialog_add_branch_phone);
        btnPicsOfStore = (ImageButton) findViewById(R.id.btn_shop_picture_camera);
        btnPicsOfPermit=(ImageButton) findViewById(R.id.btn_shop_picture_permit_camera);
//        llPicsOfStore=(LinearLayout) findViewById(R.id.ll_shop_picture_camera);
//        llPicsOfPermit=(LinearLayout) findViewById(R.id.ll_shop_picture_permit_camera);
        btnAddBranch = (Button) findViewById(R.id.btn_branch_add);
        btnAddBranch.setOnClickListener(this);
        btnPicsOfStore.setOnClickListener(this);
        btnPicsOfPermit.setOnClickListener(this);

        BranchModel branchModel = getIntent().getParcelableExtra("branchModel");
        findViewById(R.id.ll_photo_store_imageExcessDisplay).setOnClickListener(this);
        findViewById(R.id.ll_photo_permit_imageExcessDisplay).setOnClickListener(this);
        if (null != branchModel && ApplicationConstants.RESULT_EDIT_BRANCH == getIntent().getIntExtra("resultCode",0)) {
            resultCode = ApplicationConstants.RESULT_EDIT_BRANCH;
            etBranchName.setText(branchModel.getName());
            etBranchAdd.setText(branchModel.getAddress());
            etBranchPhone.setText(branchModel.getContactNum());
            imageStoreModel = branchModel.getBranchsPic();
            imagePermitModel = branchModel.getPermitsPic();
            if(ApplicationConstants.RESULT_EDIT_BRANCH == getIntent().getIntExtra("resultCode",0)) selectedBranchId = getIntent().getIntExtra("id",0);
            refreshImageList("iv_cam_store_", R.id.btn_shop_picture_camera, imageStoreModel, ApplicationConstants.RESULT_GALLERY_STORE);
            refreshImageList("iv_cam_permit_", R.id.btn_shop_picture_permit_camera, imagePermitModel, ApplicationConstants.RESULT_GALLERY_PERMIT);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_shop_picture_camera:

                if(etBranchName.getText().toString().isEmpty()) {
                    etBranchName.setError("Branch Name is required in adding a photo");
                }
                else {
                    AlertDialog.Builder photoOfStoreDialog = new AlertDialog.Builder(
                            AddBranchActivity.this);
                    photoOfStoreDialog.setPositiveButton("Gallery",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent pictureActionIntent = null;
                                    pictureActionIntent = new Intent(
                                            Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pictureActionIntent, ApplicationConstants.RESULT_GALLERY_PICTURE_STORE);
                                }
                            });

                    photoOfStoreDialog.setNegativeButton("Camera",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    getPhotoFromCamera(ApplicationConstants.RESULT_PICK_FROM_CAMERA_STORE);
                                }
                            });
                    photoOfStoreDialog.show();
                }
                break;
            case R.id.btn_shop_picture_permit_camera:

                if(etBranchName.getText().toString().isEmpty()) {
                    etBranchName.setError("Branch Name is required in adding a photo");
                }
                else {
                    AlertDialog.Builder photoOfPermitDialog = new AlertDialog.Builder(
                            AddBranchActivity.this);
                    photoOfPermitDialog.setPositiveButton("Gallery",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent pictureActionIntent = null;
                                    pictureActionIntent = new Intent(
                                            Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pictureActionIntent, ApplicationConstants.RESULT_GALLERY_PICTURE_PERMIT);
                                }
                            });

                    photoOfPermitDialog.setNegativeButton("Camera",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    getPhotoFromCamera(ApplicationConstants.RESULT_PICK_FROM_CAMERA_PERMIT);
                                }
                            });
                    photoOfPermitDialog.show();
                }
                break;
//            case R.id.ll_photo_store_imageExcessDisplay:
//                clickImageExcess(imageStoreModel, ApplicationConstants.RESULT_GALLERY_STORE);
//                break;
//            case R.id.ll_photo_permit_imageExcessDisplay:
//                clickImageExcess(imagePermitModel, ApplicationConstants.RESULT_GALLERY_PERMIT);
//                break;
            case R.id.btn_branch_add:
                if(isFormCanBeSaved()) {
                    Intent branchIntent = new Intent(AddBranchActivity.this, RegistrationActivity.class);
                    branchIntent.putExtra("branchModel",toBeSaved());

                    if(resultCode == ApplicationConstants.RESULT_EDIT_BRANCH) {
                        branchIntent.putExtra("id",selectedBranchId);
                        branchIntent.putExtra("resultCode", ApplicationConstants.RESULT_EDIT_BRANCH);
//                        startActivity(branchIntent);
//                        finish();
                    }
                        setResult(RESULT_OK, branchIntent);
                        finish();


                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {

            case ApplicationConstants.RESULT_GALLERY_STORE:
                if (resultCode != RESULT_CANCELED) {
                    imageStoreModel = setImagesResultGallery(data);
                    refreshImageList("iv_cam_store_", R.id.btn_shop_picture_camera,   imageStoreModel, ApplicationConstants.RESULT_GALLERY_STORE);
                }
                break;
            case ApplicationConstants.RESULT_GALLERY_PERMIT:
                if (resultCode != RESULT_CANCELED) {
                    imagePermitModel = setImagesResultGallery(data);
                    refreshImageList("iv_cam_permit_",R.id.btn_shop_picture_permit_camera,   imagePermitModel, ApplicationConstants.RESULT_GALLERY_PERMIT);
                }
                break;
            case ApplicationConstants.RESULT_PICK_FROM_CAMERA_STORE:
                if (resultCode != RESULT_CANCELED) {
//                    imagesStoreBase.add(numStoreImages);
//                    imagesStoreBaseStrings.add(photoFile);
//                    numStoreImages++;
                    imageStoreModel.getIntegerBase().add(imageStoreModel.getNumOfImages());
                    imageStoreModel.getStringBase().add(photoFile);
                    imageStoreModel.setNumOfImages(imagePermitModel.getNumOfImages() + 1);
                }
                refreshImageList("iv_cam_store_", R.id.btn_shop_picture_camera,   imageStoreModel, ApplicationConstants.RESULT_GALLERY_STORE);
                break;
            case ApplicationConstants.RESULT_PICK_FROM_CAMERA_PERMIT:
                if (resultCode != RESULT_CANCELED) {
//                    imagesPermitBase.add(numPermitImages);
//                    imagesPermitBaseStrings.add(photoFile);
//                    numPermitImages++;
                    imagePermitModel.getIntegerBase().add(imagePermitModel.getNumOfImages());
                    imagePermitModel.getStringBase().add(photoFile);
                    imagePermitModel.setNumOfImages(imagePermitModel.getNumOfImages() + 1);
                }
                refreshImageList("iv_cam_permit_", R.id.btn_shop_picture_permit_camera,   imagePermitModel, ApplicationConstants.RESULT_GALLERY_PERMIT);
                break;

            case ApplicationConstants.RESULT_GALLERY_PICTURE_STORE:
                if (resultCode != RESULT_CANCELED) {
                    resultFromGallery(data, imageStoreModel, ApplicationConstants.RESULT_GALLERY_STORE);

                }
                refreshImageList("iv_cam_store_", R.id.btn_shop_picture_camera,   imageStoreModel, ApplicationConstants.RESULT_GALLERY_STORE);
                break;
            case ApplicationConstants.RESULT_GALLERY_PICTURE_PERMIT:
                if (resultCode != RESULT_CANCELED) {
                    resultFromGallery(data, imagePermitModel, ApplicationConstants.RESULT_GALLERY_PERMIT);

                }
                refreshImageList("iv_cam_permit_", R.id.btn_shop_picture_permit_camera,   imagePermitModel, ApplicationConstants.RESULT_GALLERY_PERMIT);
                break;
//            case ApplicationConstants.RESULT_EDIT_BRANCH:
//                BranchModel branchModel = data.getParcelableExtra("branchModel");
//                etBranchName.setText(branchModel.getName());
//                etBranchAdd.setText(branchModel.getAddress());
//                etBranchPhone.setText(branchModel.getContactNum());
//                imageStoreModel = branchModel.getBranchsPic();
//                imagePermitModel = branchModel.getPermitsPic();
//                refreshImageList("iv_cam_store_",  imageStoreModel, ApplicationConstants.RESULT_GALLERY_STORE);
//                refreshImageList("iv_cam_permit_",  imagePermitModel, ApplicationConstants.RESULT_GALLERY_PERMIT);
//                break;
        }
    }

    public void getPhotoFromCamera(int requestCode) {

//        String DATA_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "wasabi" + File.separator;

        String strTimeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss" ).format( new Date() );
        photoFile = "photo_"+requestCode+"_"+etBranchName.getText().toString()+"_"+strTimeStamp+".png";

        File dir = new File(ApplicationConstants.DATA_STORAGE_STORE_PATH);

        if(requestCode == ApplicationConstants.RESULT_GALLERY_PERMIT) dir = new File(ApplicationConstants.DATA_STORAGE_PERMIT_PATH);
        if(!dir.exists()){
            dir.mkdir();
        }
        File mFile = new File(dir,photoFile);
        try {
            if( mFile.createNewFile() ) {
                Log.i(ApplicationConstants.APP_CODE, "success" );
            }
        } catch( IOException e ) {
            e.printStackTrace();
        }

        Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
        startActivityForResult(chooserIntent, requestCode);

    }

    private ImageModel setImagesResultGallery(Intent data) {
        ImageModel imageModel = new ImageModel(new ArrayList<Integer>(), new ArrayList<String>(),0);
        if (data != null) {
            List<String> arr = data.getStringArrayListExtra("bitmaps");

            if (arr != null) {
                imageModel.setStringBase(new ArrayList<>(arr));
            }
            imageModel.getIntegerBase().clear();
            for(int i = 0; i < imageModel.getStringBase().size(); i++){
                imageModel.getIntegerBase().add(i);
            }

            imageModel.setNumOfImages(imageModel.getStringBase().size());
        }
        return imageModel;
    }

    private void refreshImageList(final String idPrefix, final int btnId, final ImageModel imageModel, final int resultCode) {
        //clear imageviews
        for (int k = 0; k < NUM_IMAGES; k++) {
            int resId = getResources()
                    .getIdentifier(idPrefix + k, "id", getPackageName());
            final ImageView img =(ImageView) findViewById(resId);
            img.setImageBitmap(null);
            img.setVisibility(View.GONE);
        }
//        findViewById(imageExcessLinearId).setVisibility(View.GONE);

        int x = 0;
        for (int i = imageModel.getIntegerBase().size() - 1; i >= ((imageModel.getIntegerBase().size() - NUM_IMAGES) >= 0 ? (imageModel.getIntegerBase().size() - NUM_IMAGES) : 0); i--) {
            int resId = getResources()
                    .getIdentifier(idPrefix + x, "id", getPackageName());
            final ImageView img = (ImageView) findViewById(resId);
            img.setTag(i);
            img.setVisibility(View.VISIBLE);
            img.setImageBitmap(RestCallServices.getBitmapFrom( imageModel.getStringBase().get(i), resultCode));
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddBranchActivity.this, CameraImageActivity.class);
                    intent.putExtra("bitmaps", imageModel.getStringBase());
                    intent.putExtra("resultCode", resultCode);
                    intent.putExtra("display", CameraImageActivity.FULLSCREEN);
                    intent.putExtra("imageIndex", (Integer) img.getTag());
                    startActivityForResult(intent, resultCode);
                }
            });
            x++;
        }

        findViewById(btnId).setVisibility(View.VISIBLE);
        if (imageModel.getIntegerBase().size() >= NUM_IMAGES) {

            findViewById(btnId).setVisibility(View.GONE);
            //MAX OF THREE. THIS NOT NEEDED
//            findViewById(imageExcessLinearId).setVisibility(View.GONE);
//            ((TextView) findViewById(imageExcessCount)).setText("+" + (imageModel.getIntegerBase().size() - NUM_IMAGES));
        }
    }

    private void resultFromGallery (Intent data, ImageModel imageModel, int requestCode) {
        if (data != null) {

            /**/Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage, filePath,
                    null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String selectedImagePath = c.getString(columnIndex);
            c.close();

//                        String DATA_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "wasabi" + File.separator;

            String id = etBranchName.getText().toString();
            String strTimeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss" ).format( new Date() );
            photoFile = "store_photo_"+imageModel.getNumOfImages()+id+"_"+strTimeStamp+".png";

            File dir = new File(ApplicationConstants.DATA_STORAGE_STORE_PATH);

            if(requestCode == ApplicationConstants.RESULT_GALLERY_PERMIT) dir = new File(ApplicationConstants.DATA_STORAGE_PERMIT_PATH);
            if(!dir.exists()){
                dir.mkdir();
            }
            File mFile = new File(dir,photoFile);
            try {
                if( mFile.createNewFile()  ) {
                    Log.i( "Access:AssetManagement", "success" );
                }
            } catch( IOException e ) {
                e.printStackTrace();
            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(mFile);

                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath); // load

                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageModel.getIntegerBase().add(imageModel.getNumOfImages());
            imageModel.getStringBase().add(photoFile);
            imageModel.setNumOfImages(imageModel.getNumOfImages()+1);

        } else {
            Toast.makeText(getApplicationContext(), "Cancelled",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public boolean isFormCanBeSaved() {
        boolean canBeAdded = true;

        if (etBranchName.getText().toString().isEmpty()) {
            etBranchName.setError(AddBranchActivity.this.getString(R.string.is_required));
            canBeAdded = false;
        }
        if (etBranchAdd.getText().toString().isEmpty()) {
            etBranchAdd.setError(AddBranchActivity.this.getString(R.string.is_required));
            canBeAdded = false;
        }
        if (imageStoreModel.getIntegerBase().isEmpty()) {
            Util.getInstance().showDialog(this, "Please upload atleast 1 photo of your Branch", this.getString(R.string.action_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            canBeAdded = false;
        }
        if (imagePermitModel.getIntegerBase().isEmpty()) {
            Util.getInstance().showDialog(this, "Please upload atleast 1 photo of your permit", this.getString(R.string.action_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            canBeAdded = false;
        }
        return canBeAdded;
    }

    private BranchModel toBeSaved() {
        BranchModel branchModel = new BranchModel();
        branchModel.setName(etBranchName.getText().toString());
        branchModel.setContactNum(etBranchPhone.getText().toString());
        branchModel.setAddress(etBranchAdd.getText().toString());
        branchModel.setPermitsPic(imagePermitModel);
        branchModel.setBranchsPic(imageStoreModel);
        return branchModel;
    }

    private void clickImageExcess(final ImageModel imageModel, int resultCode) {
        Intent intent = new Intent(AddBranchActivity.this, CameraImageActivity.class);
        intent.putExtra("bitmaps", imageModel.getStringBase());
        startActivityForResult(intent, resultCode);
    }
}
