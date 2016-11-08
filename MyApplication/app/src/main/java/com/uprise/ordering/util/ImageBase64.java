package com.uprise.ordering.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.uprise.ordering.constant.ApplicationConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by cicciolina on 11/9/16.
 */

public class ImageBase64 {
    final String TAG = this.getClass().getName();

    private Context context;

    private static ImageBase64 instance;

    private int width = 128, height = 128; //default

    private boolean noScale;

    private ImageBase64(Context context){
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        this.context = context;
    }

    /**
     * create a Singleton object and set a Context.
     * @param context
     * @return ImageBase64
     */

    public static ImageBase64 with(Context context){
        if(instance == null){
            synchronized (ImageBase64.class) {
                if(instance == null){
                    instance = new ImageBase64(context);
                }
            }
        }
        return instance;
    }

    /**
     * Request width and height. These are used to calculate to find the best fit for width and height.
     * The image will only scale down but not stretching. Be default, width = 128px and height = 128.
     * @param width
     * @param height
     * @return ImageBase64
     */
    public ImageBase64 requestSize(int width, int height) {
        this.height = width;
        this.width = height;
        return instance;
    }

    /**
     * set no scale
     * @return ImageBase64
     */

    public ImageBase64 noScale(){
        this.noScale = true;
        return instance;
    }

    /**
     * Encode an resource image (drawable) into a base64 image.
     * @param resId
     * @return String: an encoded image in base64 String format.
     */
    public String encodeResource(int resId){
        Bitmap bitmap = null;
        if(noScale){
            bitmap = decodeResource(resId);
        }
        else{
            bitmap = decodeSampledBitmapFromResource(resId, width, height);
        }

        String encodedImage = encodeImageToString(bitmap);

        return encodedImage;
    }

    /**
     * Encode an image into a base64 image.
     * @param filePath
     * @return String: an encoded image in base64 String format.
     * @throws FileNotFoundException
     */
    public String encodeFile(String filePath, int resultCode) throws FileNotFoundException {

        Bitmap bitmap;
        if(noScale){
            bitmap = decodeFile(filePath, resultCode);
        }
        else{
            bitmap = decodeSampledBitmapFromFile(filePath, width, height, resultCode);
        }

        String encodedImage = encodeImageToString(bitmap);

        return encodedImage;
    }

///////////////////PRIVATE METHODS

    private String encodeImageToString(Bitmap bitmap){
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        int quality = 100; //100: compress nothing
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bao);

        if(bitmap != null){//important! prevent out of memory
            bitmap.recycle();
            bitmap = null;
        }

        byte [] ba = bao.toByteArray();
        String encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
        return encodedImage;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight, int resultCode) throws FileNotFoundException {

        File dir = new File(ApplicationConstants.DATA_STORAGE_STORE_PATH);

        if(resultCode == ApplicationConstants.RESULT_GALLERY_PERMIT)  dir = new File(ApplicationConstants.DATA_STORAGE_PERMIT_PATH);
        if(!dir.exists()){
            dir.mkdir();
        }

        File file = new File(dir, filePath);
        if(!file.exists()){
//            throw new FileNotFoundException();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;
    }

    private Bitmap decodeFile(String filePath, int resultCode) throws FileNotFoundException {

        File dir = new File(ApplicationConstants.DATA_STORAGE_STORE_PATH);

        if(resultCode == ApplicationConstants.RESULT_GALLERY_PERMIT)  dir = new File(ApplicationConstants.DATA_STORAGE_PERMIT_PATH);
        if(!dir.exists()){
            dir.mkdir();
        }


        File file = new File(dir, filePath);
        if(!file.exists()){
//            throw new FileNotFoundException();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        return bitmap;
    }

    public Bitmap decodeSampledBitmapFromResource(int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize requestSize
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    public Bitmap decodeResource(int resId) {

        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

}
