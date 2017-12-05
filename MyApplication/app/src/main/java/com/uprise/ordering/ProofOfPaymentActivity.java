package com.uprise.ordering;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uprise.ordering.camera.CameraImageActivity;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.ImageModel;
import com.uprise.ordering.model.OrderModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ProofOfPaymentActivity extends AppCompatActivity implements View.OnClickListener,
        RestCallServices.RestServiceListener {

    private Spinner spinnerModeOfPayment;
    private LinearLayout llModeOfPayment;
    private LinearLayout llProofOfPaymentCamera;
    private LinearLayout llProofOfPaymentImages;
    private Button btnProofOfPaymentCamera;
    private TextView tvMaxPhotosMsg;
    private String photoFile;

    private ImageModel imageProofOfPaymentModel;
    private static int NUM_IMAGES = 2;
    public static int totalProofOfPaymentImages = 0;


    private ArrayAdapter<String> modeOfPaymentSpinnerAdapter;

    private final List<String> MODE_OF_PAYMENT = new ArrayList<>(Arrays.asList("Bank Transfer", "Cash on Delivery"));

    private String modeOfPaymentStr;

    private OrderModel orderModel;

    private final String PROOF_OF_PAYMENT_IMAGE_VIEW_ID_PREFIX = "iv_cam_proof_of_payment_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.asset_nav_back);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText(getString(R.string.title_proof_of_payment));
        setContentView(R.layout.activity_proof_of_payment);

        llModeOfPayment = (LinearLayout) findViewById(R.id.ll_mode_of_payment_options);
        llModeOfPayment.setOnClickListener(this);
        llProofOfPaymentCamera = (LinearLayout) findViewById(R.id.ll_btn_proof_of_payment_camera);
        llProofOfPaymentCamera.setOnClickListener(this);
        llProofOfPaymentImages = (LinearLayout) findViewById(R.id.ll_proof_of_payment_picture_images);
        spinnerModeOfPayment = (Spinner) findViewById(R.id.mode_of_payment_spinner);
        tvMaxPhotosMsg = (TextView) findViewById(R.id.tv_max_of_two_pic_proof_of_payment);
        btnProofOfPaymentCamera = (Button) findViewById(R.id.btn_proof_of_payment_picture_camera);
        btnProofOfPaymentCamera.setOnClickListener(this);
        modeOfPaymentSpinnerAdapter = new ArrayAdapter<>(ProofOfPaymentActivity.this, R.layout.list_item_label_spinner, MODE_OF_PAYMENT);
        modeOfPaymentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerModeOfPayment.setAdapter(modeOfPaymentSpinnerAdapter);

        setSpinnerModeOfPayment(0);
        spinnerModeOfPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerModeOfPayment(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        orderModel = getIntent().getParcelableExtra("orderModel");
    }

    private void setSpinnerModeOfPayment(int position) {
        this.modeOfPaymentStr =  MODE_OF_PAYMENT.get(position);

        if(position == 1) {
            llProofOfPaymentCamera.setVisibility(View.GONE);
            llProofOfPaymentImages.setVisibility(View.GONE);
            tvMaxPhotosMsg.setVisibility(View.GONE);
            imageProofOfPaymentModel = null;
            totalProofOfPaymentImages = 0;
        } else if(position == 0) {
            imageProofOfPaymentModel = new ImageModel(new ArrayList<Integer>(), new ArrayList<String>(),0);
            llProofOfPaymentCamera.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.activity_proof_of_payment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_submit_proof_of_payment:

                if(isFormCanBeSubmitted()){

                }
                break;
        }
        return true;
    }

    private boolean isFormCanBeSubmitted() {

        if(modeOfPaymentStr.equalsIgnoreCase(MODE_OF_PAYMENT.get(0)) &&
                (imageProofOfPaymentModel == null ||
                imageProofOfPaymentModel.getIntegerBase() == null ||
                imageProofOfPaymentModel.getIntegerBase().isEmpty())) {
            Util.getInstance().showDialog(this, getString(R.string.error_proof_of_payment_required), this.getString(R.string.action_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            return false;
        }



        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.ll_mode_of_payment_options && spinnerModeOfPayment != null) {
                spinnerModeOfPayment.performClick();
        }

        else if(id == R.id.btn_proof_of_payment_picture_camera || id == R.id.ll_btn_proof_of_payment_camera) {

            if(orderModel != null && orderModel.getOrderId() != null) {
                AlertDialog.Builder photoOfStoreDialog = new AlertDialog.Builder(
                        ProofOfPaymentActivity.this);
                photoOfStoreDialog.setPositiveButton("Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent pictureActionIntent = null;
                                pictureActionIntent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pictureActionIntent, ApplicationConstants.RESULT_GALLERY_PICTURE_PROOF_OF_PAYMENT);
                            }
                        });

                photoOfStoreDialog.setNegativeButton("Camera",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                getPhotoFromCamera(orderModel.getOrderId().toUpperCase());
                            }
                        });
                photoOfStoreDialog.show();
            }
        }
    }

    public void getPhotoFromCamera(String photoName) {

//        String DATA_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "wasabi" + File.separator;

        String strTimeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss" ).format( new Date() );
        photoFile = "photo_"+ApplicationConstants.RESULT_PICK_FROM_CAMERA_PROOF_OF_PAYMENT+"_"+photoName+"_"+strTimeStamp+".png";

        File dir = new File(ApplicationConstants.DATA_STORAGE_PROOF_OF_PAYMENT_PATH);

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
        startActivityForResult(chooserIntent, ApplicationConstants.RESULT_PICK_FROM_CAMERA_PROOF_OF_PAYMENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {

            case ApplicationConstants.RESULT_GALLERY_PROOF_OF_PAYMENT:
                if (resultCode != RESULT_CANCELED) {
                    imageProofOfPaymentModel = setImagesResultGallery(data);
                    refreshImageList(imageProofOfPaymentModel);
                }
                break;
            case ApplicationConstants.RESULT_PICK_FROM_CAMERA_PROOF_OF_PAYMENT:
                if (resultCode != RESULT_CANCELED) {
                    imageProofOfPaymentModel.getIntegerBase().add(imageProofOfPaymentModel.getNumOfImages());
                    imageProofOfPaymentModel.getStringBase().add(photoFile);
                    imageProofOfPaymentModel.setNumOfImages(imageProofOfPaymentModel.getNumOfImages() + 1);
                }
                refreshImageList(imageProofOfPaymentModel);
                break;
            case ApplicationConstants.RESULT_GALLERY_PICTURE_PROOF_OF_PAYMENT:
                if (resultCode != RESULT_CANCELED) {
                    resultFromGallery(data, imageProofOfPaymentModel, orderModel.getOrderId());

                }
                refreshImageList(imageProofOfPaymentModel);
                break;
        }
    }

    private void resultFromGallery (Intent data, ImageModel imageModel, String photoName) {
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

            String id = photoName;
            String strTimeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss" ).format( new Date() );
            photoFile = "proof_of_payment_photo_"+imageModel.getNumOfImages()+id+"_"+strTimeStamp+".png";

            File dir = new File(ApplicationConstants.DATA_STORAGE_PROOF_OF_PAYMENT_PATH);

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

    private void refreshImageList(final ImageModel imageModel) {
        //clear imageviews

        for (int k = 0; k < NUM_IMAGES; k++) {
            int resId = getResources()
                    .getIdentifier(PROOF_OF_PAYMENT_IMAGE_VIEW_ID_PREFIX + k, "id", getPackageName());
            final ImageView img =(ImageView) findViewById(resId);
            img.setImageBitmap(null);
            img.setVisibility(View.GONE);
        }
//        findViewById(imageExcessLinearId).setVisibility(View.GONE);

        int x = 0;
        for (int i = imageModel.getIntegerBase().size() - 1; i >= ((imageModel.getIntegerBase().size() - NUM_IMAGES) >= 0 ? (imageModel.getIntegerBase().size() - NUM_IMAGES) : 0); i--) {
            int resId = getResources()
                    .getIdentifier(PROOF_OF_PAYMENT_IMAGE_VIEW_ID_PREFIX + x, "id", getPackageName());
            final ImageView img = (ImageView) findViewById(resId);
            img.setTag(i);
            img.setVisibility(View.VISIBLE);
            img.setImageBitmap(RestCallServices.getBitmapFrom( imageModel.getStringBase().get(i), ApplicationConstants.RESULT_GALLERY_PROOF_OF_PAYMENT));
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProofOfPaymentActivity.this, CameraImageActivity.class);
                    intent.putExtra("bitmaps", imageModel.getStringBase());
                    intent.putExtra("resultCode", ApplicationConstants.RESULT_GALLERY_PROOF_OF_PAYMENT);
                    intent.putExtra("display", CameraImageActivity.FULLSCREEN);
                    intent.putExtra("imageIndex", (Integer) img.getTag());
                    startActivityForResult(intent, ApplicationConstants.RESULT_GALLERY_PROOF_OF_PAYMENT);
                }
            });
            x++;
        }

        findViewById(R.id.tv_max_of_two_pic_proof_of_payment).setVisibility(View.GONE);
        findViewById(R.id.ll_btn_proof_of_payment_camera).setVisibility(View.VISIBLE);
        if (imageModel.getIntegerBase().size() >= NUM_IMAGES) {

            findViewById(R.id.ll_btn_proof_of_payment_camera).setVisibility(View.GONE);
            findViewById(R.id.tv_max_of_two_pic_proof_of_payment).setVisibility(View.VISIBLE);
        }

        if(imageModel != null && imageModel.getIntegerBase() != null &&
                imageModel.getIntegerBase().size() > 0) {
            llProofOfPaymentImages.setVisibility(View.VISIBLE);
        }
        else if(imageModel != null && imageModel.getIntegerBase() != null &&
                imageModel.getIntegerBase().size() <= 0) {
            llProofOfPaymentImages.setVisibility(View.GONE);
        }

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

    @Override
    public int getResultCode() {

        //TODO: impl
        return 0;
    }

    @Override
    public void onSuccess(RestCalls callType, String string) {

        //TODO: impl

    }

    @Override
    public void onFailure(RestCalls callType, String string) {

        //TODO: impl
    }
}
