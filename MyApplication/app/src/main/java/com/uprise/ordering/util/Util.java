package com.uprise.ordering.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.uprise.ordering.R;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.LocationDetailsModel;
import com.uprise.ordering.model.LoginModel;
import com.uprise.ordering.model.NotificationsModel;
import com.uprise.ordering.model.OrderItemsModel;
import com.uprise.ordering.model.OrderModel;
import com.uprise.ordering.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by cicciolina on 10/15/16.
 */
public class Util {

    private static Util instance;

    public static List<LatLng> latLngs = Arrays.asList(new LatLng(14.520445d, 121.053886d)
                                                        , new LatLng(14.5406258d, 121.063888d)
                                                    ,new LatLng(14.5235562d, 121.0511223d)
                                                    ,new LatLng(14.530399d, 121.053896)
                                                    ,new LatLng(14.5211223d, 121.055423d)
                                                    , new LatLng(14.5406253d, 121.063838d)
                                                    ,new LatLng(14.520125d, 121.053336d)
                                                    ,new LatLng(14.556399d, 121.054896)
                                            , new LatLng(14.5426253d, 121.063838d)
                                            ,new LatLng(14.520145d, 121.053336d)
                                            ,new LatLng(14.556699d, 121.054396));
    private static List<String> url = Arrays.asList("http://gazettereview.com/wp-content/uploads/2015/12/PEN-STYLE-2.jpg"
                                    ,"http://www.trappsclassichumidors.com/wp-content/uploads/2014/08/shutterstock_116632465.jpg"
                                    , "http://i.imgur.com/z4FaR3m.png"
                                    ,"http://www.beste-cigarettebrands.com/wp-content/uploads/2014/05/v2pro_series3_package.png"
                                    ,"http://www.dhresource.com/260x260s/f2-albu-g2-M00-E4-D0-rBVaGlbOi2iANimbAADaxFGu69Y909.jpg/new-updated-e-cigarette-vapor-storm-h30-8.jpg"
                                    ,"http://image.dhgate.com/albu_384349298_00/1.0x0.jpg"
                                    ,"http://image.made-in-china.com/43f34j00UZTtuGKrjYkf/Europa-First-Union-and-Famous-Electronic-Cigarette-Brand-Into-The-Market.jpg"
                                    ,"https://upload.wikimedia.org/wikipedia/commons/2/2d/E_Cigarettes,_Ego,_Vaporizers_and_Box_Mods_(17679064871).jpg"
                                    ,"http://e-cigarette-starter-kit.info/wp-content/uploads/2011/04/ecigarettebrands.png"
                                    ,"http://www.cigarettesbrands.com/wp-content/uploads/2013/12/e-cigarette.png"
                                    ,"http://www.ego-cigarettes.com/photo/pc889464-black_mini_affordable_brands_of_disposable_electric_cigarette_for_green_smoking.jpg");

    private Util() {

    }

    public static Util getInstance() {

        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }


    public String getUserPhoneNumber(Context ctx) {
        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String phNumber = tMgr.getLine1Number();
        return (phNumber == null || phNumber.isEmpty()) ? "" : phNumber;
    }

    //---sends an SMS message to another device---
    public void sendSMS(final Context ctx, String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, 0,
                new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        //sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }



    public void showDialog(Context ctx, String message, String okButton, String cancelButton
            , DialogInterface.OnClickListener positiveListener
            , DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton(okButton, positiveListener)
                .setNegativeButton(cancelButton, negativeListener)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void showDialog(Context ctx, String message, String okButton
            , DialogInterface.OnClickListener positiveListener) {

        if(ctx != null) {
            new AlertDialog.Builder(ctx)
                    .setMessage(message)
                    .setPositiveButton(okButton, positiveListener)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


    public void showSnackBarToast(Context context, String message){
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            Snackbar.make(((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        } else{
            // do something for phones running an SDK before lollipop
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Calculate SqLite nearest points
     * http://stackoverflow.com/questions/3695224/sqlite-getting-nearest-locations-with-latitude-and-longitude
     */

    public static PointF calculateDerivedPosition(PointF point,
                                                  double range, double bearing)
    {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;

    }

    // generate some random amount of child objects (1..10)
    public ArrayList<ProductModel> generateProductModels() {
        ArrayList<ProductModel> productModels = new ArrayList<>();
        for(int i=0; i < 11; i++) {
            ProductModel productModel = new ProductModel();
            productModel.setId("product_"+i);
            productModel.setName("Product "+i);
            productModel.setBrands(generateBrands());
            productModels.add(productModel);
        }

        return productModels;
    }

    //Mock only
    public ArrayList<BrandModel> generateBrands() {
        ArrayList<BrandModel> brands = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            BrandModel brandModel = new BrandModel();
            brandModel.setId("brand_"+i);
            brandModel.setBrandName("Brand " + i);
            brandModel.setPrice(10d + brandModel.getPrice() + i);
            brandModel.setBrandPhotoUrl(Util.url.get(i));
            brands.add(brandModel);
        }
        return brands;
    }

    //Mock only
    public ProductModel getMatchedProductModel(CartItemsModel cartItemsModel, List<ProductModel> productModels) {
        ProductModel result = new ProductModel();
        for(int i=0; i<productModels.size(); i++) {
            if(productModels.get(i).getId().equalsIgnoreCase(cartItemsModel.getProductModelId())) {
                result = productModels.get(i);
            }
        }
        return result;
    }

    public BrandModel getMatchedBrandModel(CartItemsModel cartItemsModel, List<BrandModel> brandModels) {
        BrandModel result = new BrandModel();
        for(int i=0; i<brandModels.size(); i++) {
            if(brandModels.get(i).getId().equalsIgnoreCase(cartItemsModel.getBrandId())) {
                result = brandModels.get(i);
            }
        }
        return result;
    }


    public ProductModel getMatchedProductModel(CartItemsModel cartItemsModel, List<ProductModel> productModels, String brandId) {
        ProductModel result = new ProductModel();
        for(int i=0; i<productModels.size(); i++) {
            if(cartItemsModel.getBrandId().equalsIgnoreCase(brandId) &&
                    productModels.get(i).getId().equalsIgnoreCase(cartItemsModel.getProductModelId()) &&
                    productModels.get(i).getAttributeId().equalsIgnoreCase(cartItemsModel.getAttributeId())) {
                result = productModels.get(i);
            }
        }
        return result;
    }


    public BrandModel getMatchedBrandModel(CartItemsModel cartItemsModel, List<BrandModel> brandModels, String productId) {
        BrandModel result = new BrandModel();
        for(int i=0; i<brandModels.size(); i++) {
            if(cartItemsModel.getProductModelId().equalsIgnoreCase(productId) &&
                    brandModels.get(i).getId().equalsIgnoreCase(cartItemsModel.getBrandId()) &&
                    brandModels.get(i).getAttributeId().equalsIgnoreCase(cartItemsModel.getAttributeId())) {
                result = brandModels.get(i);
            }
        }
        return result;
    }

    public boolean isProductsAndCartItemsNotEmpty(List<ProductModel> productModels,
                                                       List<CartItemsModel> cartItemsModels) {
        return cartItemsModels != null && !cartItemsModels.isEmpty() && productModels != null
                && !productModels.isEmpty();
    }

    public boolean isBrandsAndCartItemsNotEmpty(List<BrandModel> brandModels,
                                                  List<CartItemsModel> cartItemsModels) {
        return cartItemsModels != null && !cartItemsModels.isEmpty() && brandModels != null
                && !brandModels.isEmpty();
    }

    //Mock only
    public ArrayList<NotificationsModel> generateNotifications() {
        ArrayList<NotificationsModel> notificationsModelArrayList = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            NotificationsModel notificationsModel = new NotificationsModel();
            notificationsModel.setTitle("Title "+i);
            notificationsModel.setMessage("Message "+i);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            long daysAgo = cal.getTimeInMillis();
            notificationsModel.setDate(daysAgo+"");
            notificationsModelArrayList.add(notificationsModel);
        }
        return notificationsModelArrayList;
    }

    //Mock only

    public ArrayList<OrderModel> generateOrders(Context ctx) {
        ArrayList<OrderModel> orderModels = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            OrderModel orderModel = new OrderModel();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            long daysAgo = cal.getTimeInMillis();
            orderModel.setOrderId(UUID.randomUUID().toString());
            orderModel.setDate(daysAgo+"");
//            orderModel.setOrderStatus(OrderStatus.APPROVED);
//            orderModel.setCartItemsModels(generateCartItems(ctx));
            orderModels.add(orderModel);
        }
        return orderModels;
    }

    public ArrayList<CartItemsModel> generateCartItems(Context ctx) {

//        LoginSharedPref loginSharedPref = new LoginSharedPref();
        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(ctx);
        LoginModel loginModel = sqlDatabaseHelper.getLoginCredentials();

        ArrayList<CartItemsModel> cartItemsModels = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
        CartItemsModel cartItemsModel = new CartItemsModel();
            cartItemsModel.setProductModelId("product_"+i);
            cartItemsModel.setBrandId("brand_"+i);
            cartItemsModel.setAttributeId(1+i*i+"");
            cartItemsModel.setQuantity(i*2);
            cartItemsModel.setUserName(loginModel.getUsername());
            cartItemsModels.add(cartItemsModel);

        }
        return cartItemsModels;
    }

    public double computeBrandEstimatedTotal(List<BrandModel> brandModels, List<CartItemsModel> cartItemsModels) {
        double total = 0d;


        for (CartItemsModel model : cartItemsModels) {
            if (Util.getInstance().isBrandsAndCartItemsNotEmpty(brandModels, cartItemsModels)) {


                BrandModel matchedBrandModel = Util.getInstance().getMatchedBrandModel(model, brandModels);

                if (matchedBrandModel != null && matchedBrandModel.getProducts() != null &&
                        !matchedBrandModel.getProducts().isEmpty()) {
                    ProductModel matchedProductModel = Util.getInstance().getMatchedProductModel(model, matchedBrandModel.getProducts(), matchedBrandModel.getId());
                    if (matchedProductModel != null) {
                        total += matchedProductModel.getPrice() * model.getQuantity();
                    }
                }

            }
        }
        return total;
    }


    public double computeEstimatedTotal(List<ProductModel> productModels, List<CartItemsModel> cartItemsModels) {
        double total = 0d;


        for (CartItemsModel model : cartItemsModels) {
            if (Util.getInstance().isProductsAndCartItemsNotEmpty(productModels, cartItemsModels)) {


                ProductModel matchedProductModel = Util.getInstance().getMatchedProductModel(model, productModels);

                if (matchedProductModel != null &&
                        !matchedProductModel.getBrands().isEmpty()) {
                    BrandModel matchedBrandModel = Util.getInstance().getMatchedBrandModel(model, matchedProductModel.getBrands(), matchedProductModel.getId());
                    if (matchedBrandModel != null) {
                        total += matchedBrandModel.getPrice() * model.getQuantity();
                    }
                }

            }
        }
        return total;
    }

    public static void requestPermission(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE
                        , Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.CAMERA
                        , Manifest.permission.READ_PHONE_STATE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


    }

    public static LatLng getLocationInLatLngRad(double radiusInMeters, LatLng currentLocation) {
        double x0 = currentLocation.longitude;
        double y0 = currentLocation.latitude;

        Random random = new Random();

        // Convert radius from meters to degrees.
        double radiusInDegrees = radiusInMeters / 111320f;

        // Get a random distance and a random angle.
        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        // Get the x and y delta values.
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Compensate the x value.
        double new_x = x / Math.cos(Math.toRadians(y0));

        double foundLatitude;
        double foundLongitude;

        foundLatitude = y0 + y;
        foundLongitude = x0 + new_x;

//        Location copy = new Location(currentLocation);
//        copy.setLatitude(foundLatitude);
//        copy.setLongitude(foundLongitude);

        LatLng copy = new LatLng(foundLatitude, foundLongitude);
        return copy;
    }

    /**
     * Helper method to format information about a place nicely.
     */
    public static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(ApplicationConstants.APP_CODE, res.getString(R.string.place_details, name, address));
        return Html.fromHtml(res.getString(R.string.place_details, name, address));

    }

    public BrandModel generateBrandModelFromJson(JSONObject jsonObject) {
        BrandModel brandModel = new BrandModel();



        try {
            if(jsonObject.getString("id") != null) brandModel.setId(jsonObject.getString("id"));
            if(jsonObject.getString("name") != null) brandModel.setBrandName(jsonObject.getString("name"));
            if(jsonObject.getString("image") != null) brandModel.setBrandPhotoUrl(jsonObject.getString("image"));

            if(jsonObject.getString("attributes") != null) {
                JSONArray jsonAttributesArray = jsonObject.getJSONArray("attributes");
                ArrayList<ProductModel> products = new ArrayList<>();

                for(int i = 0; i < jsonAttributesArray.length(); i++) {
                    ProductModel productModel = new ProductModel();
                    JSONObject attributeItem = jsonAttributesArray.getJSONObject(i);
                    if(attributeItem.getString("id") != null) productModel.setAttributeId(attributeItem.getString("id"));
                    if(attributeItem.getString("name") != null & attributeItem.getString("name").equalsIgnoreCase("product") && attributeItem.getJSONObject("value") != null) {
                        JSONObject jsonValue = attributeItem.getJSONObject("value");
                        if(jsonValue.getString("product") != null) productModel.setId(jsonValue.getString("product"));
                        if(jsonValue.getString("product_name") != null) productModel.setName(jsonValue.getString("product_name"));
                        if(jsonValue.getString("image") != null) productModel.setProductPhotoUrl(jsonValue.getString("image"));
                        if(jsonValue.getString("price") != null && !jsonValue.getString("price").isEmpty()) productModel.setPrice(Double.parseDouble(jsonValue.getString("price")));
                        products.add(productModel);
                    }
                }
                brandModel.setProducts(products);
            }
            return brandModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ProductModel generateProductModelFromJson(JSONObject jsonObject) {
        ProductModel productModel = new ProductModel();



        try {
            if(jsonObject.getString("id") != null) productModel.setId(jsonObject.getString("id"));
            if(jsonObject.getString("name") != null) productModel.setName(jsonObject.getString("name"));

            if(jsonObject.getString("attributes") != null) {
                JSONArray jsonAttributesArray = jsonObject.getJSONArray("attributes");
                ArrayList<BrandModel> brands = new ArrayList<>();

                for(int i = 0; i < jsonAttributesArray.length(); i++) {
                    BrandModel brandModel = new BrandModel();
                    JSONObject attributeItem = jsonAttributesArray.getJSONObject(i);
                    if(attributeItem.getString("id") != null) brandModel.setAttributeId(attributeItem.getString("id"));
                    if(attributeItem.getString("name") != null & attributeItem.getString("name").equalsIgnoreCase("brand") && attributeItem.getJSONObject("value") != null) {
                        JSONObject jsonValue = attributeItem.getJSONObject("value");
                        if(jsonValue.getString("brand") != null) brandModel.setId(jsonValue.getString("brand"));
                        if(jsonValue.getString("brand_name") != null) brandModel.setBrandName(jsonValue.getString("brand_name"));
                        if(jsonValue.getString("image") != null) brandModel.setBrandPhotoUrl(jsonValue.getString("image"));
                        if(jsonValue.getString("price") != null && !jsonValue.getString("price").isEmpty()) brandModel.setPrice(Double.parseDouble(jsonValue.getString("price")));
                        brands.add(brandModel);
                    }
                }
                productModel.setBrands(brands);
            }
            return productModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BrandModel generateBrandsDistributorShopFromJson(JSONObject jsonObject) {
        BrandModel brandModel = new BrandModel();



        try {
            if(jsonObject.getString("id") != null) brandModel.setId(jsonObject.getString("id"));
            if(jsonObject.getString("name") != null) brandModel.setBrandName(jsonObject.getString("name"));
            if(jsonObject.getString("image") != null) brandModel.setBrandPhotoUrl(jsonObject.getString("image"));

            if(jsonObject.getString("attributes") != null) {
                JSONArray jsonAttributesArray = jsonObject.getJSONArray("attributes");
                ArrayList<ProductModel> products = new ArrayList<>();

                for(int i = 0; i < jsonAttributesArray.length(); i++) {
                    ProductModel productModel = new ProductModel();
                    JSONObject attributeItem = jsonAttributesArray.getJSONObject(i);
                    if(attributeItem.getString("id") != null) productModel.setAttributeId(attributeItem.getString("id"));
                    if(attributeItem.getString("name") != null & attributeItem.getString("name").equalsIgnoreCase("product") && attributeItem.getJSONObject("value") != null) {
                        JSONObject jsonValue = attributeItem.getJSONObject("value");
                        if(jsonValue.getString("product") != null) productModel.setId(jsonValue.getString("product"));
                        if(jsonValue.getString("product_name") != null) productModel.setName(jsonValue.getString("product_name"));
                        if(jsonValue.getString("image") != null) productModel.setProductPhotoUrl(jsonValue.getString("image"));
                        products.add(productModel);
                    }
                }
                brandModel.setProducts(products);
            }
            return brandModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ProductModel generateDistributorShopFromJson(JSONObject jsonObject) {
        ProductModel productModel = new ProductModel();



        try {
            if(jsonObject.getString("id") != null) productModel.setId(jsonObject.getString("id"));
            if(jsonObject.getString("name") != null) productModel.setName(jsonObject.getString("name"));

            if(jsonObject.getString("attributes") != null) {
                JSONArray jsonAttributesArray = jsonObject.getJSONArray("attributes");
                ArrayList<BrandModel> brands = new ArrayList<>();

                for(int i = 0; i < jsonAttributesArray.length(); i++) {
                    BrandModel brandModel = new BrandModel();
                    JSONObject attributeItem = jsonAttributesArray.getJSONObject(i);
                    if(attributeItem.getString("id") != null) brandModel.setAttributeId(attributeItem.getString("id"));
                    if(attributeItem.getString("name") != null & attributeItem.getString("name").equalsIgnoreCase("brand") && attributeItem.getJSONObject("value") != null) {
                        JSONObject jsonValue = attributeItem.getJSONObject("value");
                        if(jsonValue.getString("brand") != null) brandModel.setId(jsonValue.getString("brand"));
                        if(jsonValue.getString("brand_name") != null) brandModel.setBrandName(jsonValue.getString("brand_name"));
                        if(jsonValue.getString("image") != null) brandModel.setBrandPhotoUrl(jsonValue.getString("image"));
                        brands.add(brandModel);
                    }
                }
                productModel.setBrands(brands);
            }
            return productModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    public LocationDetailsModel generateLocationDetailsModelFromJson(JSONObject jsonObject) {
        LocationDetailsModel shopsLocation = new LocationDetailsModel();

        try {
            if(jsonObject.getString("lat") != null && jsonObject.getString("lng") != null) {
                double lat = 0;
                double lng = 0;
                if(!jsonObject.getString("lat").isEmpty()) lat = parseDoubleWithDefault(jsonObject.getString("lat"));
                if(!jsonObject.getString("lng").isEmpty()) lng = parseDoubleWithDefault(jsonObject.getString("lng"));
                shopsLocation.setLocation(new LatLng(lat, lng));
            }

            if(jsonObject.getString("address") != null) shopsLocation.setAddress(jsonObject.getString("address"));
            return shopsLocation;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static LatLng convertStrToLocation(String lat, String lng) {
        return new LatLng(parseDoubleWithDefault(lat), parseDoubleWithDefault(lng));
    }

    public OrderModel generatePaymentRespFromJson(JSONObject jsonObject) {
        OrderModel orderModel = new OrderModel();
        try {

            if(jsonObject.getString("id") != null && !jsonObject.getString("id").isEmpty()) {
                orderModel.setOrderId(jsonObject.getString("id"));
            }
            if(jsonObject.getString("status") != null && !jsonObject.getString("status").isEmpty()) {
                orderModel.setOrderStatus(jsonObject.getString("status"));
            }

            if(jsonObject.getString("payment_method") != null && !jsonObject.getString("payment_method").isEmpty()) {
                orderModel.setPaymentMethod(jsonObject.getString("payment_method"));
            }

            return orderModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderModel;
    }

    public OrderModel generateOrderModelFromJson(JSONObject jsonObject) {
        OrderModel orderModel = new OrderModel();
        ArrayList<OrderItemsModel> orderItemsModels = new ArrayList<>();
        try {
            if(jsonObject.getString("items") != null) {

                JSONArray jsonItemsArray = jsonObject.getJSONArray("items");


                for(int i = 0; i < jsonItemsArray.length(); i++) {
                    double price = 0;
                    int quantity = 0;
                    OrderItemsModel orderItemsModel = new OrderItemsModel();
                    JSONObject jsonItem = jsonItemsArray.getJSONObject(i);
                    if (jsonItem.getString("product") != null &&
                            !jsonItem.getString("product").isEmpty())
                        orderItemsModel.setProductName(jsonItem.getString("product"));
                    if (jsonItem.getString("brand") != null &&
                            !jsonItem.getString("brand").isEmpty())
                        orderItemsModel.setBrandName(jsonItem.getString("brand"));
                    if (jsonItem.getString("price") != null &&
                            !jsonItem.getString("price").isEmpty())
                        price = Double.parseDouble(jsonItem.getString("price"));

                    orderItemsModel.setPrice(price);
                    if (jsonItem.getString("quantity") != null &&
                            !jsonItem.getString("quantity").isEmpty())
                        quantity = Integer.parseInt(jsonItem.getString("quantity"));
                    orderItemsModel.setQuantity(quantity);
                    orderItemsModels.add(orderItemsModel);
                }
                orderModel.setOrderItemsModels(orderItemsModels);
            }

            if(jsonObject.getString("id") != null && !jsonObject.getString("id").isEmpty()) {
                orderModel.setOrderId(jsonObject.getString("id"));
            }
            if(jsonObject.getString("status") != null && !jsonObject.getString("status").isEmpty()) {
                orderModel.setOrderStatus(jsonObject.getString("status"));
            }

            if(jsonObject.getString("payment_method") != null && !jsonObject.getString("payment_method").isEmpty()) {
                orderModel.setPaymentMethod(jsonObject.getString("payment_method"));
            }

            if(jsonObject.getString("discount") != null && !jsonObject.getString("discount").isEmpty()) {
                double discount = Double.parseDouble(jsonObject.getString("discount"));
                orderModel.setDiscount(discount);
            }

            if(jsonObject.getString("shipping_fee") != null && !jsonObject.getString("shipping_fee").isEmpty()) {
                double shippingFree = Double.parseDouble(jsonObject.getString("shipping_fee"));
                orderModel.setShippingFee(shippingFree);
            }


            if(jsonObject.getString("total") != null && !jsonObject.getString("total").isEmpty()) {
                double total = Double.parseDouble(jsonObject.getString("total"));
                orderModel.setTotalAmount(total);
            }

            if(jsonObject.getString("date_added") != null && !jsonObject.getString("date_added").isEmpty()) {
                orderModel.setDate(jsonObject.getString("date_added"));
            }

            return orderModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderModel;
    }


    public BranchModel generateBranchModelFromJson(JSONObject jsonObject) {
        BranchModel branchModel = new BranchModel();

        try {
            if(jsonObject.getString("name") != null && !jsonObject.getString("name").isEmpty()) branchModel.setName(jsonObject.getString("name"));
            if(jsonObject.getString("address") != null && !jsonObject.getString("address").isEmpty()) branchModel.setAddress(jsonObject.getString("address"));
            if(jsonObject.getString("phone") != null && !jsonObject.getString("phone").isEmpty()) branchModel.setContactNum(jsonObject.getString("phone"));
            if(jsonObject.getString("lat") != null && !jsonObject.getString("lat").isEmpty()) branchModel.setLat(jsonObject.getString("lat"));
            if(jsonObject.getString("lng") != null && !jsonObject.getString("lng").isEmpty()) branchModel.setLng(jsonObject.getString("lng"));

            return branchModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    public NotificationsModel generateNotificationsFromJson(JSONObject jsonObject) {
        NotificationsModel notificationsModel = new NotificationsModel();
        try {
            if(jsonObject.getString("title") != null && !jsonObject.getString("title").isEmpty()) notificationsModel.setTitle(jsonObject.getString("title"));
            if(jsonObject.getString("id") != null && !jsonObject.getString("id").isEmpty()) notificationsModel.setId(jsonObject.getString("id"));
            if(jsonObject.getString("content") != null && !jsonObject.getString("content").isEmpty()) notificationsModel.setMessage(jsonObject.getString("content"));
            if(jsonObject.getString("status") != null && !jsonObject.getString("status").isEmpty()) notificationsModel.setStatus(jsonObject.getString("status"));
            if(jsonObject.getString("date_added") != null && !jsonObject.getString("date_added").isEmpty()) notificationsModel.setDate(jsonObject.getString("date_added"));
            if(jsonObject.getString("url") != null && !jsonObject.getString("url").isEmpty()) notificationsModel.setUrl(jsonObject.getString("url"));
            return notificationsModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            Util.getInstance().showSnackBarToast(ctx, "You need to be connected to a network");
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static double parseDoubleWithDefault(String s) {

        if(s == null || s.isEmpty()) return 0;
        try {
            return Double.parseDouble(s);
        }
        catch (NumberFormatException e) {
            // It's OK to ignore "e" here because returning a default value is the documented behaviour on invalid input.
            return 0;
        }
    }
}
