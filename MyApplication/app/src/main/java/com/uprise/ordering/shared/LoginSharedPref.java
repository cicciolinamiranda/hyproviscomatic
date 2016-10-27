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

    public void login(Context context, String username) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(ApplicationConstants.APP_CODE,Context.MODE_MULTI_PROCESS);
        editor = settings.edit();
        editor.putString(ApplicationConstants.IS_LOGIN, username);
        editor.commit();
    }

    public boolean isLoggedIn(Context context) {
// used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        String username = "";
        settings = context.getSharedPreferences(ApplicationConstants.APP_CODE, Context.MODE_MULTI_PROCESS);

        if(settings.contains(ApplicationConstants.IS_LOGIN)) {
            username = settings.getString(ApplicationConstants.IS_LOGIN, "");
        }

        if(!username.isEmpty()) {
            return true;
        }
        return false;
    }

    public void logOut(Context context) {
        SharedPreferences settings = context.getSharedPreferences(ApplicationConstants.APP_CODE, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        if(isLoggedIn(context) && settings.contains(ApplicationConstants.IS_LOGIN)) {
               editor.putString(ApplicationConstants.IS_LOGIN,"");
                editor.commit();
        }
    }

    public String getUsername(Context context) {
        SharedPreferences settings;
        String username = "";
        settings = context.getSharedPreferences(ApplicationConstants.APP_CODE, Context.MODE_MULTI_PROCESS);

        if(settings.contains(ApplicationConstants.IS_LOGIN)) {
            username = settings.getString(ApplicationConstants.IS_LOGIN, "");
        }

        return username;
    }
}
