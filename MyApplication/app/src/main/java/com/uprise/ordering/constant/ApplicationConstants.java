package com.uprise.ordering.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by cicciolina on 10/15/16.
 */
public class ApplicationConstants {

    public static String APP_CODE = "Hyprviscomatic";
    public static String CART_ITEMS = "Cart_items";
    public static String IS_LOGIN = "Is_login";
    public static String IS_LOGIN_PASSWORD = "Is_login_password";
    public static String IS_LOGIN_TOKEN = "Is_login_token";
    public static final int RESULT_PICK_FROM_CAMERA_STORE = 0x03;
    public static final int RESULT_GALLERY_PICTURE_STORE = 0x02;
    public static final int RESULT_GALLERY_STORE = 0x05;

    public static final int RESULT_PICK_FROM_CAMERA_PERMIT = 0x06;
    public static final int RESULT_GALLERY_PICTURE_PERMIT = 0x07;
    public static final int RESULT_GALLERY_PERMIT = 0x08;
    public static final int RESULT_FROM_ADD_BRANCH= 0X10;
    public static final int RESULT_EDIT_BRANCH= 0X11;
    public static final String DATA_STORAGE_STORE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "hyprviscomatic_store" + File.separator;
    public static final String DATA_STORAGE_PERMIT_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "hyprviscomatic_permit" + File.separator;

    public static final String BRANCH_INTENT_OBJ = "BranchModel";
    public static final int BRANCH_PICS = 0X11;
    public static final int BRANCH_PERMIT_PICS = 0X12;

    public static final int RESULT_FROM_DELETING_NOTIF_MSG = 0x13;

    public static final int RESULT_FROM_SEARCH_SELECTED_ADDRESS = 0x14;

    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    /**
     * Request code for the autocomplete activity. This will be used to identify results from the
     * autocomplete activity in onActivityResult.
     */
    public static final int REQUEST_CODE_AUTOCOMPLETE = 0X15;

    public static final int REQUEST_CODE_ADD_BRANCH_LAT_LNG = 0X16;

    public static final int REQUEST_CODE_EDIT_BRANCH_LAT_LNG = 0x17;

}
