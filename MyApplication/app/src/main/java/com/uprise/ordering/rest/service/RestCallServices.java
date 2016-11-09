package com.uprise.ordering.rest.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

import com.uprise.ordering.R;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.RegistrationModel;
import com.uprise.ordering.rest.HttpClient;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.listeners.RestAsyncTaskListener;
import com.uprise.ordering.rest.tasks.RestAsyncTask;
import com.uprise.ordering.util.NameValuePair;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cicciolina on 10/15/16.
 */
public class RestCallServices {

    public static boolean isDebugMode = true;
    private String TAG = ApplicationConstants.APP_CODE;
    private String mainUrl = "", number = "", imgUrl = "";
    private Context context;

    DataOutputStream dos;
    SqlDatabaseHelper sqlDatabaseHelper;
    private final String USER_AGENT = "Mozilla/5.0";
    public RestCallServices(){

    }


    public RestCallServices(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(TAG, Context.MODE_MULTI_PROCESS);
//        imgUrl = sharedPreferences.getString("img_path", MainActivity.IMAGE_SERVER_PATH)+"/_uploadphotos?k=645e4f2d223";
//        mainUrl = sharedPreferences.getString("rest_path", MainActivity.MAIN_SERVER_PATH)+"/_deploycheckin?k=645e4f2d223";
//        number = sharedPreferences.getString("number", MainActivity.NUMBER_TO_SEND_TO);


        context = ctx;

//        sqlDatabaseHelper = new SqlDatabaseHelper(ctx);
    }

    public Bitmap getBitmap(Context ctx, String path) {
        Bitmap imageBitmap = null;
        try {
            InputStream is = ctx.openFileInput(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            imageBitmap = BitmapFactory.decodeStream(is, null, options);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return imageBitmap;
    }

    public String postRegistration(final Context ctx, final RegistrationModel registrationModel, final RestServiceListener listener) {
        String response = "";
        final String registrationEndpoint = ctx.getResources().getString(R.string.endpoint_server)
                + ctx.getResources().getString(R.string.endpoint_post_registration);

        Log.d(ApplicationConstants.APP_CODE, "registration api url:" + registrationEndpoint);

//        InputStream inputStream = null;
//        HttpURLConnection urlConnection = null;

        final ArrayList<NameValuePair> params = new ArrayList<>();

        final JSONObject body = new JSONObject();
        final JSONObject shopObj = new JSONObject();

//        params.add(new NameValuePair("username", registrationModel.getEmail()));
//        params.add(new NameValuePair("password", registrationModel.getPassword()));
//        params.add(new NameValuePair("email", registrationModel.getEmail()));
//        params.add(new NameValuePair("first_name", registrationModel.getShopName()));

//               Profile
        try {
//            body.put("email", registrationModel.getEmail().toString());

            shopObj.put("shop_address", registrationModel.getShopAddress().toString());
            shopObj.put("phone", registrationModel.getContactNum().toString());
            shopObj.put("shipping_address", registrationModel.getShippingAddress().toString());
            shopObj.put("shop_name", registrationModel.getShopName().toString());

//            params.add(new NameValuePair("shop", shopObj.toString()));
        } catch (JSONException e) {
            Log.d(ApplicationConstants.APP_CODE, "JSONException :" + e.getMessage());
        }

        final JSONArray branchJsonArray = new JSONArray();
        for (BranchModel branchModel: registrationModel.getBranchModels()) {
            JSONObject branchJsonObj = new JSONObject();
            JSONArray photosJsonArray = new JSONArray();
            try {
                branchJsonObj.put("name", branchModel.getName());

                //TODO: create a latlng. map on the fe
                branchJsonObj.put("lat", "0");
                branchJsonObj.put("lng","0");
                //TODO: add phone
                branchJsonObj.put("phone", branchModel.getContactNum());
                branchJsonObj.put("address", branchModel.getAddress().toString());


//                      Photos of store
                for(String storeImgPath: branchModel.getBranchsPic().getStringBase()) {
                    JSONObject storePhotoJson = new JSONObject();
                    Bitmap storeBmpImage = getBitmapFrom(storeImgPath, ApplicationConstants.RESULT_GALLERY_STORE);
//                    String imgBase64 = ImageBase64
//                            .with(ctx)
//                            .requestSize(512, 512)
//                            .encodeFile(storeImgPath, ApplicationConstants.RESULT_GALLERY_STORE);
                    storePhotoJson.put("image", "data:image/png;base64,"+bitmapToBase64(storeBmpImage));
                    Log.i(TAG, "STORE IMAGE:-->" + "data:image/png;base64,"+bitmapToBase64(storeBmpImage));
                    storePhotoJson.put("description", branchModel.getName() + " "+storeImgPath);
                    photosJsonArray.put(storePhotoJson);
                }

//                       Photos of permit
                for(String permitImgPath: branchModel.getPermitsPic().getStringBase()) {
                    JSONObject permitPhotoJsonObj = new JSONObject();
                    Bitmap permitBmpImage = getBitmapFrom(permitImgPath, ApplicationConstants.RESULT_GALLERY_PERMIT);
//                    String imgBase64 = ImageBase64
//                            .with(ctx)
//                            .requestSize(512, 512)
//                            .encodeFile(permitImgPath, ApplicationConstants.RESULT_GALLERY_PERMIT);
                    permitPhotoJsonObj.put("image", "data:image/png;base64,"+bitmapToBase64(permitBmpImage));
                    Log.i(TAG, "PERMIT IMAGE:--->" + "data:image/png;base64,"+bitmapToBase64(permitBmpImage));
                    permitPhotoJsonObj.put("description", branchModel.getName() + " "+permitImgPath);
                    photosJsonArray.put(permitPhotoJsonObj);
                }

                branchJsonObj.put("photos", photosJsonArray);
//                branchJsonObj.put("photos", new JSONArray());
                branchJsonArray.put(branchJsonObj);
                body.put("username", registrationModel.getEmail().toString());
                body.put("password", registrationModel.getPassword().toString());
                body.put("branches", branchJsonArray);
                body.put("shop", shopObj);
//                params.add(new NameValuePair("branches", branchJsonArray.toString()));

            } catch (JSONException e) {
                Log.d(ApplicationConstants.APP_CODE, "JSONException :" + e.getMessage());
            }
        }

//        params.add(new NameValuePair("Body", body.toString()));

        new RestAsyncTask(new RestAsyncTaskListener() {
            String jsonResults;
            @Override
            public void doInBackground() {
                JSONObject obj = HttpClient.SendHttpPost(registrationEndpoint, body);
                if (obj != null ) jsonResults = obj.toString();
            }

            @Override
            public void result() {
                if (jsonResults == null || jsonResults.isEmpty()) {
                    RestCallServices.this.failedPost(listener, RestCalls.REGISTRATION
                            , ctx.getString(R.string.unable_to_register), params);
                } else {

                    listener.onSuccess(RestCalls.REGISTRATION, jsonResults);
                }

            }
        }).execute();


        return response;
    }


    public void submitImage(final Context ctx, final RestServiceListener listener
            , final String phoneNumber, final String id, final List<String>images, final int index) {

        new RestAsyncTask(new RestAsyncTaskListener() {

            String jsonResults;
            String imgPath = images.get(index);
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

            @Override
            public void doInBackground() {
                params.add(new NameValuePair("device_id", id));
                params.add(new NameValuePair("description", " "));
                Bitmap bmpImage = getBitmapFrom(imgPath, listener.getResultCode());

                if(bmpImage != null) {
                    params.add(new NameValuePair("image", bitmapToBase64(bmpImage)));
                    params.add(new NameValuePair("filename", imgPath));

                    //sqlDatabaseHelper.createImage(new RestData(mainUrl, params));
                    jsonResults = postImageBase64(ctx, params);
                }
            }

            @Override
            public void result() {
                if (jsonResults == null) {
                    RestCallServices.this.failedPost(listener, RestCalls.PICTURE
                            , "failed sending picture in " + imgPath, params);
                } else {

                    listener.onSuccess(RestCalls.PICTURE, jsonResults);
                }

                int z= index + 1;
                if (z < images.size()) {
                    submitImage(ctx, listener, phoneNumber, id, images, z);
                }
            }
        }).execute();


    }

    private Bitmap rotateBitmap(int degree, Bitmap original) {
        Matrix matrix = new Matrix();

        matrix.setRotate(degree, original.getWidth() / 2, original.getHeight() / 2);
        matrix.postTranslate(original.getHeight(), 0);

        Bitmap rotatedBitmap = Bitmap
                .createBitmap(original.getWidth(), original.getHeight(), original.getConfig());
        Canvas tmpCanvas = new Canvas(rotatedBitmap);
        tmpCanvas.drawBitmap(original, matrix, null);
        tmpCanvas.setBitmap(null);

        return rotatedBitmap;
    }

    public enum Direction {VERTICAL, HORIZONTAL}

    ;

    /**
     * Creates a new bitmap by flipping the specified bitmap vertically or horizontally.
     *
     * @param src Bitmap to flip
     * @param type Flip direction (horizontal or vertical)
     * @return New bitmap created by flipping the given one vertically or horizontally as specified by
     * the <code>type</code> parameter or the original bitmap if an unknown type is specified.
     **/
    public static Bitmap flip(Bitmap src, Direction type) {
        Matrix matrix = new Matrix();

        if (type == Direction.VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        } else if (type == Direction.HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return src;
        }

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private static int getOrientationFromExif(String imagePath) {
        int orientation = -1;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    orientation = 0;

                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            Log.e(ApplicationConstants.APP_CODE, "Unable to get image exif orientation", e);
        }

        return orientation;
    }

    private String postImageBase64(Context ctx, ArrayList<NameValuePair> params) {
        String jsonResults = "";
        InputStream is = null;
        try {



      /*if(bmpImage != null) {
        if (imgNumber == 0) {
          String base64 = bitmapToBase64(bmpImage);
          params.add(new NameValuePair("image", base64));
          Log.d(MainActivity.APP_CODE, base64);

        }

        params.add(new NameValuePair("Image" + (imgNumber+ 1), bitmapToBase64(bmpImage)));
      }*/

            jsonResults = get(imgUrl, params);
            Log.d(ApplicationConstants.APP_CODE,"ImG URL:"+imgUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResults;
    }


    String boundary = "-------------" + System.currentTimeMillis();
    private static final String LINE_FEED = "\r\n";
    private static final String TWO_HYPHENS = "--";


    private JSONObject postData;
    private List<String> fileList;
    private int count = 0;
    private int imgCount = 0;


    private String postImage(Context ctx, String url, String phoneNumber, String deviceId,
                             String imgPath) {

        Log.d(ApplicationConstants.APP_CODE, "imagePath url:" + url);

        StringBuilder stringBuilder = new StringBuilder();

        String strResponse = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) new URL(url.toString()).openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            //urlConnection.setRequestProperty("Authorization", "Bearer " + Config.getConfigInstance().getAccessToken());
            urlConnection.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setChunkedStreamingMode(1024);
            urlConnection.setRequestMethod("POST");
            dos = new DataOutputStream(urlConnection.getOutputStream());

            //add id to data
            Iterator<String> keys = postData.keys();
            while (keys.hasNext()) {
                try {
                    String id = String.valueOf(keys.next());
                    addFormField(id, "" + postData.get(id));
                    System.out.println(id + " : " + postData.get(id));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                dos.writeBytes(LINE_FEED);
                dos.flush();
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
     /* if (fileList != null && fileList.size() > 0 && !fileList.isEmpty()) {
        for (int i = 0; i < fileList.size(); i++) {

          File file = new File(fileList.get(i));
          if (file != null) ;
          addFilePart("photos[" + i + "][image]", file);
        }
      }*/

            File file = new File("temp/wasabi.jpg");
            FileInputStream initialStream = ctx.openFileInput(imgPath);

            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);
            OutputStream outStream = new FileOutputStream(file);
            outStream.write(buffer);
/*
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inSampleSize = 4;
      Bitmap bmpImage = BitmapFactory.decodeStream(is, null, options);
*/

            if (file != null) {
                ;
            }
            imgCount++;
            addFilePart("Image" + imgCount, file);

            // forming th java.net.URL object

            build();
            urlConnection.connect();
            int statusCode = 0;
            try {
                urlConnection.connect();
                statusCode = urlConnection.getResponseCode();
            } catch (EOFException e1) {
                if (count < 5) {
                    urlConnection.disconnect();
                    count++;
                    String temp = postImage(ctx, url, phoneNumber, deviceId, imgPath);
                    if (temp != null && !temp.equals("")) {
                        return temp;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 200 represents HTTP OK
            if (statusCode == HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                strResponse = readStream(inputStream);
            } else {
                System.out.println(urlConnection.getResponseMessage());
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                strResponse = readStream(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
        return strResponse;

    }

    public void addHeaderField(String name, String value) {
        try {
            dos.writeBytes(name + ": " + value + LINE_FEED);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void build() {
        try {
            dos.writeBytes(LINE_FEED);
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
        /* Close Stream */
            if (null != in) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /***
     * @return string of JSON
     */
    public String get(final String strUrl, final ArrayList<NameValuePair> params) {

        Log.d(ApplicationConstants.APP_CODE, "url:" + strUrl);
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {

            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setAllowUserInteraction(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing URL", e);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error connecting API", e);
            return null;
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "Error connecting API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return jsonResults.toString();

    }



    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public interface RestServiceListener {

        public int getResultCode();
        public void onSuccess(RestCalls callType, String string);

        public void onFailure(RestCalls callType, String string);
    }

    public void addFormField(String fieldName, String value) {
        try {
            dos.writeBytes(TWO_HYPHENS + boundary + LINE_FEED);
            dos.writeBytes(
                    "Content-Disposition: form-data; name=\"" + fieldName + "\"" + LINE_FEED + LINE_FEED/*+ value + LINE_FEED*/);
            /*dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + LINE_FEED);*/
            dos.writeBytes(value + LINE_FEED);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addFilePart(String fieldName, File uploadFile) {
        try {
            dos.writeBytes(TWO_HYPHENS + boundary + LINE_FEED);
            dos.writeBytes(
                    "Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + uploadFile
                            .getName() + "\"" + LINE_FEED);
            dos.writeBytes(LINE_FEED);

            FileInputStream fStream = new FileInputStream(uploadFile);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;

            while ((length = fStream.read(buffer)) != -1) {
                dos.write(buffer, 0, length);
            }
            dos.writeBytes(LINE_FEED);
            dos.writeBytes(TWO_HYPHENS + boundary + TWO_HYPHENS + LINE_FEED);
        /* close streams */
            fStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

//    public String bitmapToBase641(Bitmap bitmap) {
////        Bitmap bm = BitmapFactory.decodeFile(bitmap);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//        byte[] b = baos.toByteArray();
//
//        return Base64.encodeToString(b, Base64.NO_WRAP);
//    }

    public String bitmapToBase64(Bitmap bitmap) {

//       bitmap = scaleDown(bitmap, 1200, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmaps object
       // bitmap.recycle();
        byte[] b = baos.toByteArray();
//
//        try {
//            baos.close();
//            baos = null;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap getBitmapFrom(String path, int resultCode) {
        Bitmap imageBitmap = null;
//        String DATA_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "wasabi" + File.separator;

        try {
            File dir = new File(ApplicationConstants.DATA_STORAGE_STORE_PATH);

            if(resultCode == ApplicationConstants.RESULT_GALLERY_PERMIT)  dir = new File(ApplicationConstants.DATA_STORAGE_PERMIT_PATH);
            if(!dir.exists()){
                dir.mkdir();
            }
            File mFile = new File(dir,path);

            if( !mFile.exists() ) {
                mFile.createNewFile();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 4;
            imageBitmap = BitmapFactory.decodeFile (mFile.getAbsolutePath(), options);
            Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth() / 2, imageBitmap.getHeight() / 2, false);
        } catch( IOException e ) {
            e.printStackTrace();
        } catch(Exception e){

        }
        return imageBitmap;
    }

    private void failedPost(final RestServiceListener listener, RestCalls callType, String strResult
            , final ArrayList<NameValuePair> params) {
//        switch (callType) {
//            case PICTURE:
//                sqlDatabaseHelper.createImage(new RestData(imgUrl, params));
//                break;
//        }
        listener.onFailure(callType, strResult);


    }

    private void failedBasicNameValuePairPost(final RestServiceListener listener, RestCalls callType, String strResult
            , final ArrayList<BasicNameValuePair> params) {
//        switch (callType) {
//            case PICTURE:
//                sqlDatabaseHelper.createImage(new RestData(imgUrl, params));
//                break;
//        }
        listener.onFailure(callType, strResult);


    }

}
