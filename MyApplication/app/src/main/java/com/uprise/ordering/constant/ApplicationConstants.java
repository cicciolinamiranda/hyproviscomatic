package com.uprise.ordering.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by cicciolina on 10/15/16.
 */
public class ApplicationConstants {

    public static String APP_CODE = "Hyprviscomatic";
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
}
