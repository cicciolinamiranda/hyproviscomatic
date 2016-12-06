package com.uprise.ordering.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cicciolina on 11/4/16.
 */

public interface IStorageManager {

    void save(String name, String data);

    String get(String name);

    void reset();

    JSONObject getUserCreds() throws JSONException;

    void saveUserCreds(String userName) throws JSONException;
}
