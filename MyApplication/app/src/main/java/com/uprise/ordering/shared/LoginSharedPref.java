package com.uprise.ordering.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.uprise.ordering.constant.ApplicationConstants;

/**
 * Created by cicciolina on 10/26/16.
 */

public class LoginSharedPref {
    public LoginSharedPref() {
        super();
    }

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public void login(Context context, String username) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(ApplicationConstants.APP_CODE,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(ApplicationConstants.IS_LOGIN, username);
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.commit();
    }

    public boolean isLoggedIn(Context context) {
// used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        settings = context.getSharedPreferences(ApplicationConstants.APP_CODE, Context.MODE_PRIVATE);

        if(settings.contains(IS_USER_LOGIN) && settings.getBoolean(IS_USER_LOGIN, false)) {
            return true;
        }

        return false;
    }

    public void logOut(Context context) {
        SharedPreferences settings = context.getSharedPreferences(ApplicationConstants.APP_CODE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if(isLoggedIn(context) && settings.contains(IS_USER_LOGIN)) {
               editor.putBoolean(IS_USER_LOGIN,false);
                editor.commit();
        }
    }

    public String getUsername(Context context) {
        SharedPreferences settings;
        String username = "";
        settings = context.getSharedPreferences(ApplicationConstants.APP_CODE, Context.MODE_PRIVATE);

        if(settings.contains(ApplicationConstants.IS_LOGIN)) {
            username = settings.getString(ApplicationConstants.IS_LOGIN, "");
        }

        return username;
    }
}
