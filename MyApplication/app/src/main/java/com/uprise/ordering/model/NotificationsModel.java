package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cicciolina on 10/28/16.
 */

public class NotificationsModel implements Parcelable {

    private String id;
    private String title;
    private String message;
    private String date;
    private String url;
    private String status;

    public NotificationsModel() {}

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(message);
        parcel.writeString(date);
        parcel.writeString(url);
        parcel.writeString(status);
//        parcel.writeByte((byte) (isRead ? 1 : 0));
    }
    protected NotificationsModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        message = in.readString();
        date = in.readString();
//        isRead = in.readByte() != 0;
        url = in.readString();
        status = in.readString();
    }

    public static final Creator<NotificationsModel> CREATOR = new Creator<NotificationsModel>() {
        @Override
        public NotificationsModel createFromParcel(Parcel in) {
            return new NotificationsModel(in);
        }

        @Override
        public NotificationsModel[] newArray(int size) {
            return new NotificationsModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

//    public boolean isRead() {


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
