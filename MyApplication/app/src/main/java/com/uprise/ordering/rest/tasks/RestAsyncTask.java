package com.uprise.ordering.rest.tasks;

import android.os.AsyncTask;

import com.uprise.ordering.rest.listeners.RestAsyncTaskListener;

/**
 * Created by cicciolina on 10/16/16.
 */
public class RestAsyncTask extends AsyncTask<Void, Integer, Void> {

    private RestAsyncTaskListener listener;

    public RestAsyncTask(RestAsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        this.listener.doInBackground();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.listener.result();
    }
}
