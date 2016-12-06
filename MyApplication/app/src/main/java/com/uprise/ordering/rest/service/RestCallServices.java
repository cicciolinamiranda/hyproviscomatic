package com.uprise.ordering.rest.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.uprise.ordering.R;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.LoginModel;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicciolina on 10/15/16.
 */
public class RestCallServices {

    public static boolean isDebugMode = true;
    private String TAG = ApplicationConstants.APP_CODE;
    private String mainUrl = "", number = "", imgUrl = "";
    private String gPlacesUrl;
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

                branchJsonObj.put("lat", "0");
//                Log.i(TAG, "LAT:-->" + branchModel.getLat());
                branchJsonObj.put("lng", "0");
//                Log.i(TAG, "LNG:-->" + branchModel.getLng());
                branchJsonObj.put("phone", branchModel.getContactNum());
                branchJsonObj.put("address", branchModel.getAddress());


//                      Photos of store
                int numStorePics = 0;
                for(String storeImgPath: branchModel.getBranchsPic().getStringBase()) {
                    JSONObject storePhotoJson = new JSONObject();
                    Bitmap storeBmpImage = getBitmapFrom(storeImgPath, ApplicationConstants.RESULT_GALLERY_STORE);
                    storePhotoJson.put("image", "data:image/png;base64,"+bitmapToBase64(storeBmpImage));
                    storePhotoJson.put("description", "Photo of Permit No. "+ numStorePics);
                    photosJsonArray.put(storePhotoJson);
                    numStorePics++;
                }

//                       Photos of permit
                int numPermitPics = 0;
                for(String permitImgPath: branchModel.getPermitsPic().getStringBase()) {
                    JSONObject permitPhotoJsonObj = new JSONObject();
                    Bitmap permitBmpImage = getBitmapFrom(permitImgPath, ApplicationConstants.RESULT_GALLERY_PERMIT);
                    permitPhotoJsonObj.put("image", "data:image/png;base64,"+bitmapToBase64(permitBmpImage));
                    permitPhotoJsonObj.put("description", "Photo of Permit No. "+ numPermitPics);
                    photosJsonArray.put(permitPhotoJsonObj);
                    numPermitPics++;
                }

                branchJsonObj.put("photos", photosJsonArray);
                branchJsonArray.put(branchJsonObj);
                body.put("username", registrationModel.getEmail().toString());
                body.put("password", registrationModel.getPassword().toString());
                body.put("branches", branchJsonArray);
                body.put("shop", shopObj);

            } catch (JSONException e) {
                Log.d(ApplicationConstants.APP_CODE, "JSONException :" + e.getMessage());
            }
        }


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

    public void postLogin(final Context ctx, final RestServiceListener listener, final String username, final String password) {
        final String loginEndpoint = ctx.getResources().getString(R.string.endpoint_server)
                + ctx.getResources().getString(R.string.endpoint_post_login);


        new RestAsyncTask(new RestAsyncTaskListener() {
            ArrayList<org.apache.http.NameValuePair> params = new ArrayList<>();
            String token;
            @Override
            public void doInBackground() {
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                JSONObject obj = HttpClient.SendHttpPost(loginEndpoint, params);
                try {

                    if(obj.getString("token") != null )token = obj.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void result() {
                if (token == null || token.isEmpty()) {

                    RestCallServices.this.failedPost(listener, RestCalls.LOGIN
                            , ctx.getString(R.string.unable_to_login));
                } else {

                    listener.onSuccess(RestCalls.LOGIN, token);
                }


            }
        }).execute();
    }

    public void saveBranchToExistingUser(final Context ctx, final RestServiceListener listener,
                                         final BranchModel branchModel, final LoginModel loginModel) {
        final String branchEndpoint = ctx.getResources().getString(R.string.endpoint_server)
                + ctx.getResources().getString(R.string.endpoint_get_branch);

        new RestAsyncTask(new RestAsyncTaskListener() {
            JSONObject branchJsonObj = new JSONObject();
            String resultStr;

            @Override
            public void doInBackground() {
                JSONArray photosJsonArray = new JSONArray();
                try {
                    branchJsonObj.put("user", loginModel.getUsername());
                    branchJsonObj.put("name", branchModel.getName());

//                    branchJsonObj.put("lat", branchModel.getLat());
                    branchJsonObj.put("lat", "0");
//                Log.i(TAG, "LAT:-->" + branchModel.getLat());
                    branchJsonObj.put("lng", "0");
//                    branchJsonObj.put("lng", branchModel.getLng());
//                Log.i(TAG, "LNG:-->" + branchModel.getLng());
                    branchJsonObj.put("phone", branchModel.getContactNum());
                    branchJsonObj.put("address", branchModel.getAddress());


//                      Photos of store
                    int numStorePics = 0;
                    for(String storeImgPath: branchModel.getBranchsPic().getStringBase()) {
                        JSONObject storePhotoJson = new JSONObject();
                        Bitmap storeBmpImage = getBitmapFrom(storeImgPath, ApplicationConstants.RESULT_GALLERY_STORE);
                        storePhotoJson.put("image", "data:image/png;base64,"+bitmapToBase64(storeBmpImage));
                        storePhotoJson.put("description", "Photo of Permit No. "+ numStorePics);
                        photosJsonArray.put(storePhotoJson);
                        numStorePics++;
                    }

//                       Photos of permit
                    int numPermitPics = 0;
                    for(String permitImgPath: branchModel.getPermitsPic().getStringBase()) {
                        JSONObject permitPhotoJsonObj = new JSONObject();
                        Bitmap permitBmpImage = getBitmapFrom(permitImgPath, ApplicationConstants.RESULT_GALLERY_PERMIT);
                        permitPhotoJsonObj.put("image", "data:image/png;base64,"+bitmapToBase64(permitBmpImage));
                        Log.i(TAG, "PERMIT IMAGE:--->" + "data:image/png;base64,"+bitmapToBase64(permitBmpImage));
                        permitPhotoJsonObj.put("description", "Photo of Permit No. "+ numPermitPics);
                        photosJsonArray.put(permitPhotoJsonObj);
                        numPermitPics++;
                    }

                    branchJsonObj.put("photos", photosJsonArray);

                    JSONObject resultObj = HttpClient.SenHttpPostWithAuthentication(branchEndpoint, branchJsonObj, loginModel.getToken());
                    if(resultObj != null) resultStr = resultObj.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void result() {
                if (resultStr == null || resultStr.isEmpty()) {

                    RestCallServices.this.failedPost(listener, RestCalls.ADD_BRANCH
                            , ctx.getString(R.string.unable_to_save_branch));
                } else {
                    listener.onSuccess(RestCalls.ADD_BRANCH,  resultStr);
                }
            }
        }).execute();
    }

    public void getBranch(final Context ctx, final RestServiceListener listener, final String token,
                          final String branchEndpoint) {

        new RestAsyncTask(new RestAsyncTaskListener() {
            JSONObject obj;
            String jsonResult;
            @Override
            public void doInBackground() {
                obj = HttpClient.SendHttpGetWithoutParamWithAuthorization(branchEndpoint, token);
                if(obj   != null) {
                    jsonResult = obj.toString();
                }

            }

            @Override
            public void result() {

                if (jsonResult == null || jsonResult.isEmpty()) {

                    RestCallServices.this.failedPost(listener, RestCalls.BRANCH
                            , ctx.getString(R.string.unable_to_retrieve_branch));
                } else {
                    try {
                        JSONObject jsnobject = new JSONObject(jsonResult);

                        if(null != jsnobject.get("results")) {
                            listener.onSuccess(RestCalls.LOGIN,  jsonResult);
                        }
                        else if(null != jsnobject.get("detail")) {
                            RestCallServices.this.failedPost(listener, RestCalls.BRANCH
                                    , jsnobject.get("detail").toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        RestCallServices.this.failedPost(listener, RestCalls.BRANCH
                                , ctx.getString(R.string.unable_to_retrieve_branch));

                    }



                }
            }
        }).execute();
    }

    public void postPurchase(final Context ctx, final RestServiceListener listener,
                             final LoginModel loginModel, final List<CartItemsModel> cartItemsModels, double total) {
        final String purchaseEndpoint = ctx.getResources().getString(R.string.endpoint_server)
                + ctx.getResources().getString(R.string.endpoint_get_purchase);
        final JSONObject purchaseObject = new JSONObject();
        JSONArray itemsJsonArray = new JSONArray();
        try {
            purchaseObject.put("user", loginModel.getUsername());

            if(cartItemsModels != null && cartItemsModels.size() >0) {

                for(CartItemsModel cartItemsModel: cartItemsModels) {
                    JSONObject itemObjJson = new JSONObject();
                    itemObjJson.put("quantity", cartItemsModel.getQuantity());
//                    itemObjJson.put("price", cartItemsModel.getPrice());
                    itemObjJson.put("product", cartItemsModel.getProductModelId());
                    itemObjJson.put("brand", cartItemsModel.getBrandId());
                    itemObjJson.put("attribute_id", cartItemsModel.getAttributeId());
                    itemsJsonArray.put(itemObjJson);
                }
            }

            purchaseObject.put("items", itemsJsonArray);
//            purchaseObject.put("total", total);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new RestAsyncTask(new RestAsyncTaskListener() {
            ArrayList<org.apache.http.NameValuePair> params = new ArrayList<>();
            String resultStr;


            @Override
            public void doInBackground() {
                JSONObject obj = HttpClient.SenHttpPostWithAuthentication(purchaseEndpoint, purchaseObject, loginModel.getToken());
                try {

                    if(obj != null && obj.getString("items") != null )resultStr = obj.getString("items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void result() {
                if (resultStr == null || resultStr.isEmpty()) {

                    RestCallServices.this.failedPost(listener, RestCalls.PURCHASE
                            , ctx.getString(R.string.unable_to_checkout_order));
                } else {

                    listener.onSuccess(RestCalls.PURCHASE, resultStr);
                }


            }
        }).execute();
    }

    public void getProducts(final Context ctx, final RestServiceListener listener, final String token
            , final String productsEndpoint) {
//        final String productsEndpoint = ctx.getResources().getString(R.string.endpoint_server)
//                + ctx.getResources().getString(R.string.endpoint_get_products);
        new RestAsyncTask(new RestAsyncTaskListener() {
            JSONObject obj;
            String jsonResult;
            @Override
            public void doInBackground() {
                obj = HttpClient.SendHttpGetWithoutParamWithAuthorization(productsEndpoint, token);
                if(obj   != null) {
                    jsonResult = obj.toString();
                }

            }

            @Override
            public void result() {

                if (jsonResult == null || jsonResult.isEmpty()) {

                    RestCallServices.this.failedPost(listener, RestCalls.LOGIN
                            , ctx.getString(R.string.unable_to_retrieve_branch));
                } else {
                    try {
                        JSONObject jsnobject = new JSONObject(jsonResult);

                        if(null != jsnobject.get("results")) {
                            listener.onSuccess(RestCalls.PRODUCTS,  jsonResult);
                        }
                        else if(null != jsnobject.get("detail")) {
                            RestCallServices.this.failedPost(listener, RestCalls.LOGIN
                                    , jsnobject.get("detail").toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        RestCallServices.this.failedPost(listener, RestCalls.PRODUCTS
                                , ctx.getString(R.string.unable_to_retrieve_products));

                    }



                }
            }
        }).execute();
    }


    public void getNotifications(final Context ctx, final RestServiceListener listener,
                                 final LoginModel loginModel, final String notifEndpoint) {
//        final String purchaseEndpoint = ctx.getResources().getString(R.string.endpoint_server)
//                + ctx.getResources().getString(R.string.endpoint_get_notifications);

        new RestAsyncTask(new RestAsyncTaskListener() {
            String resultStr;


            @Override
            public void doInBackground() {
                JSONObject obj = HttpClient.SendHttpGetWithoutParamWithAuthorization(notifEndpoint, loginModel.getToken());

                if(obj != null) {
//                         resultStr = obj.getString("results");
                    resultStr = obj.toString();
                }



            }

            @Override
            public void result() {
                if (resultStr == null || resultStr.isEmpty()) {

                    RestCallServices.this.failedPost(listener, RestCalls.NOTIFICATIONS
                            , ctx.getString(R.string.unable_to_get_notif));
                } else {

                    listener.onSuccess(RestCalls.NOTIFICATIONS, resultStr);
                }


            }
        }).execute();
    }

    public void getPurchaseList(final Context ctx, final RestServiceListener listener, final LoginModel loginModel,
                                final String purchaseEndpoint) {
//        final String purchaseEndpoint = ctx.getResources().getString(R.string.endpoint_server)
//                + ctx.getResources().getString(R.string.endpoint_get_purchase);

        new RestAsyncTask(new RestAsyncTaskListener() {
            String resultStr;


            @Override
            public void doInBackground() {
                JSONObject obj = HttpClient.SendHttpGetWithoutParamWithAuthorization(purchaseEndpoint, loginModel.getToken());

                if(obj != null) {
//                         resultStr = obj.getString("results");
                    resultStr = obj.toString();
                }



            }

            @Override
            public void result() {
                if (resultStr == null || resultStr.isEmpty()) {

                    RestCallServices.this.failedPost(listener, RestCalls.PURCHASE
                            , ctx.getString(R.string.unable_to_checkout_order));
                } else {

                    listener.onSuccess(RestCalls.PURCHASE, resultStr);
                }


            }
        }).execute();
    }


    public void getDistributorShop(final Context ctx, final RestServiceListener listener, final String productsEndpoint) {
//        final String productsEndpoint = ctx.getResources().getString(R.string.endpoint_server)
//                + ctx.getResources().getString(R.string.endpoint_get_products);
        new RestAsyncTask(new RestAsyncTaskListener() {
            JSONObject obj;
            String jsonResult;
            @Override
            public void doInBackground() {
                obj = HttpClient.SendHttpGetWithoutParamAndAuth(productsEndpoint);
                if(obj   != null) {
                    jsonResult = obj.toString();
                }

            }

            @Override
            public void result() {

                if (jsonResult == null || jsonResult.isEmpty()) {

                    RestCallServices.this.failedPost(listener, RestCalls.LOGIN
                            , ctx.getString(R.string.unable_to_retrieve_branch));
                } else {
                    try {
                        JSONObject jsnobject = new JSONObject(jsonResult);

                        if(null != jsnobject.get("results")) {
                            listener.onSuccess(RestCalls.PRODUCTS,  jsonResult);
                        }
                        else if(null != jsnobject.get("detail")) {
                            RestCallServices.this.failedPost(listener, RestCalls.LOGIN
                                    , jsnobject.get("detail").toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        RestCallServices.this.failedPost(listener, RestCalls.PRODUCTS
                                , ctx.getString(R.string.unable_to_retrieve_products));

                    }



                }
            }
        }).execute();
    }






    public interface RestServiceListener {

        public int getResultCode();
        public void onSuccess(RestCalls callType, String string);

        public void onFailure(RestCalls callType, String string);
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

    private void failedPost(final RestServiceListener listener, RestCalls callType, String strResult) {
        listener.onFailure(callType, strResult);


    }

}
