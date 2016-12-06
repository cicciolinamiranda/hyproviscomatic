package com.uprise.ordering.rest.listeners;

/**
 * Created by cicciolina on 10/16/16.
 */
public interface RestAsyncTaskListener {
    void doInBackground();
    void result();
}
