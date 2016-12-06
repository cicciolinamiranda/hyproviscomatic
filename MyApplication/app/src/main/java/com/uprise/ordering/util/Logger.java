package com.uprise.ordering.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by cicciolina on 11/4/16.
 */

public class Logger {
    private static final String TAG = "spyder";
    private static Logger instance = new Logger();

    private Logger() {
    }

    public static Logger getInstance() {
        return instance;
    }

    public void showMessageWithHandler(final Context context, final String tag,
                                       final String message) {

        Handler h = new Handler(context.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                showMessage(context, tag, message);
            }
        });
    }

    public void showMessage(final Context context, final String tag, final String message) {
        Log.d(tag, message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void log(final String message) {
        Log.d(TAG, message);

    }
}
