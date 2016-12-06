package com.uprise.ordering.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by cicciolina on 11/4/16.
 */

public class WifiScanManager {

    private static final String TAG = "Hyprviscomatic";
    private static final String SERVER = "server";
    private static final int PORT = 8085;
    private static WifiScanManager instance = new WifiScanManager();
    private static IStorageManager storageManager;
    private JSONObject jsonRespObj;

    private WifiScanManager() {
    }

    public static WifiScanManager getInstance() {
        return instance;
    }

    /**
     * Sets the storage manager for this instance and succeeding instances.
     *
     * @param storeMgr - kinds of storage managers available: SharedPrefManager
     */
    public static void setStorageManager(IStorageManager storeMgr) {
        storageManager = storeMgr;
    }

    /**
     * Starting point for the application. Checks if connected to network before starting anything. If
     * connected, starts thread for scan or request to server. If not, resets the stored data for next
     * scan.
     *
     * @param context - either the Activity or the context passed by the receiver.
     * @param port - the port to connect to.
     */
    public void startScanForIPWithPort(final Context context, final int port) {
        if (isConnectedToNetwork(context)) { //if connected to network
            startThreadScan(context, port);
        } else { //if not connected to network
            Log.d(TAG, "Not connected to any network");
            Toast.makeText(context, "Not connected to any network", Toast.LENGTH_SHORT).show();
            storageManager.reset();
        }
    }

    /**
     * Checks if GPS is turned on. If not prompts user to turn it on.
     *
     * @param context - either the Activity or the context passed by the receiver.
     */
    public void checkGPS(final Context context) {

        if (context !=null) {
            LocationManager mlocManager = (LocationManager) context.getSystemService(
                    Context.LOCATION_SERVICE);
            boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Location needed")
                        .setMessage("App needs your current location. Enable GPS?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });

                builder.create().show();
            }
        }
    }

    /**
     * Wrapper for showing status results.
     *
     * @param context - either the Activity or the context passed by the receiver.
     * @param server - the server connected to.
     * @param status - the status given back by the server.
     */
    private void showStatusResults(Context context, String server, String status)
            throws JSONException {
        Logger.getInstance().showMessageWithHandler(context, TAG, "Received response status ("
                + server + "): " + status);
    }

    /**
     * Sends data to saved URL. If no saved URL found, scans all sub nets of current network.
     *
     * @param context - either the Activity or the context passed by the receiver.
     * @param port - the port to connect to.
     */
    private void startThreadScan(final Context context, final int port) {
        Thread threadScan = new Thread() {
            @Override
            public void run() {

                try {

                    String savedUrl = storageManager.get(SERVER);

                    if (!areYouStillOpen(savedUrl, PORT)) {
                        scanIPSubnets(context, port);
                    }
                    jsonRespObj = sendDataTo(context, storageManager.get(SERVER),
                            storageManager.getUserCreds());

                    if (jsonRespObj == null) {
                        Logger.getInstance().showMessageWithHandler(context, TAG, "Cannot find server");
                    } else {
                        showStatusResults(context, storageManager.get(SERVER), jsonRespObj.getString("status"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        threadScan.start();
    }

    /**
     * Gets connection status to Wifi and mobile network
     *
     * @param context - either the Activity or the context passed by the receiver.
     * @return connection status of Wifi or mobile network
     */
    public boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mMobile.isConnected() || mWifi.isConnected();
    }


    public boolean isConnectedToWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }


    /**
     * Turns on Wifi for map location
     *
     * @param ctx - either the Activity or the context passed by the receiver.
     */
    public void turnOnWifi(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx
                .getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    /**
     * Turns on GPS for map location
     *
     * @param ctx - either the Activity or the context passed by the receiver.
     */
    public void turnOnGPS(Context ctx) {
        //deprecated
    /*Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
    intent.putExtra("enabled", true);
    ctx.sendBroadcast(intent);*/


        ctx.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        String provider = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

    /**
     * Returns IP address in a string.
     *
     * @param context - either the Activity or the context passed by the receiver.
     * @return IP address
     */
    public String getWifiIpAddress(Context context) {
        String ipAddress = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipAddress = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        }

        Log.d(TAG, "ipAddress: " + ipAddress);

        return ipAddress;
    }

    /**
     * Does a POST request to the given url.
     *
     * @param context - either the Activity or the context passed by the receiver.
     * @param strUrl - URL to send POST request to.
     * @param jsonRequest - JSON object that contains data to be placed in POST request.
     * @return JSON object that contains response of POST. Returns null when POST request fails.
     */
    private JSONObject sendDataTo(Context context, String strUrl, JSONObject jsonRequest) {

        JSONObject jsonResponse = null;
        URL url;

        if (strUrl == null || strUrl.isEmpty()) {
            return null;
        }

        try {
            String path = "/ping";

            url = new URL("http://" + strUrl + ":" + PORT + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "application/json");

            OutputStream wr = new BufferedOutputStream(conn.getOutputStream());
            wr.write(jsonRequest.toString().getBytes());
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder sbResponse = new StringBuilder();

            while ((line = rd.readLine()) != null) {
                sbResponse.append(line);
            }

            jsonResponse = new JSONObject(sbResponse.toString());

            rd.close();
            wr.close();

        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        }
        return jsonResponse;
    }

    /**
     * Checks availability of given URL with port.
     *
     * @param url - given URL to connect to.
     * @param port - given port to connect to.
     * @return true if URL with port is still open.
     */
    public boolean areYouStillOpen(String url, int port) {
        try {
            Socket socket = new Socket();
            SocketAddress address = new InetSocketAddress(url, port);
            socket.connect(address, 100);
            socket.close();
            return true;

        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Loops through sub nets to look for an open server with port
     *
     * @param context - either the Activity or the context passed by the receiver.
     * @param port - given port to connect to.
     * @return true if found a sub net with port open.
     */
    public boolean scanIPSubnets(Context context, int port) {
        String iIPv4 = this.getWifiIpAddress(context);
        iIPv4 = iIPv4.substring(0, iIPv4.lastIndexOf("."));
        iIPv4 += ".";

        for (int i = 1; i < 255; i++) { //loop to scan each subnet
            if (areYouStillOpen(iIPv4 + i, port)) {
                storageManager.save(SERVER, iIPv4 + i);
                return true;
            }
        }
        return false;
    }


    public final boolean isInternetOn(Context ctx) {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    public boolean isLocationEnabled(Context context) {

        LocationManager mlocManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return enabled;
    }

    public void getLocation(final Context context, LocationListener listener) {

        LocationManager mlocManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Location needed")
                    .setMessage("App needs your current location. Enable Location Settings?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });

            builder.create().show();
        } else {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, listener);
        }
    }


    public void stopGettingLocation(final Context context, LocationListener listener) {
        LocationManager mlocManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocManager.removeUpdates(listener);
    }

    public void forceWiFi(final Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(!mWifi.isConnected() && !mWifi.isConnectedOrConnecting() && !mWifi.isAvailable() && !mWifi.isRoaming()){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("WiFi needed")
                    .setMessage("App needs WiFi turned on. Enable WiFi?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            turnOnWifi(context);
                        }
                    });

            builder.create().show();
        }
    }



}
