package com.uprise.ordering.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.support.design.widget.Snackbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.NotificationsModel;
import com.uprise.ordering.model.ProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by cicciolina on 10/15/16.
 */
public class Util {

    private static Util instance;

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


        new AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton(okButton, positiveListener)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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


    public BrandModel getMatchedBrandModel(CartItemsModel cartItemsModel, List<BrandModel> brandModels, String productId) {
        BrandModel result = new BrandModel();
        for(int i=0; i<brandModels.size(); i++) {
            if(cartItemsModel.getProductModelId().equalsIgnoreCase(productId) &&
                    brandModels.get(i).getId().equalsIgnoreCase(cartItemsModel.getBranchId())) {
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
            notificationsModel.setDate((daysAgo / 1000L)+"");
            notificationsModelArrayList.add(notificationsModel);
        }
        return notificationsModelArrayList;
    }





}
