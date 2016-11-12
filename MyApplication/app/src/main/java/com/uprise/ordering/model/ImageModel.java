package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by cicciolina on 10/20/16.
 */
public class ImageModel implements Parcelable{
    private String url;
    private ArrayList<Integer> integerBase;
    private ArrayList<String> stringBase;
    private int numOfImages;


    public ImageModel() {
    }

    public ImageModel(ArrayList<Integer> integerBase, ArrayList<String> stringBase, int numOfImages) {
        this.integerBase = integerBase;
        this.stringBase = stringBase;
        this.numOfImages = numOfImages;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(stringBase);
        parcel.writeList(integerBase);
        parcel.writeInt(numOfImages);
    }

    public ImageModel(Parcel in) {
        this.stringBase = in.readArrayList(null);
//        in.readList(stringBase, ImageModel.class.getClassLoader());
        this.integerBase = in.readArrayList(null);
//        in.readList(integerBase, ImageModel.class.getClassLoader());
        this.numOfImages = in.readInt();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public ArrayList<Integer> getIntegerBase() {
        return integerBase;
    }

    public void setIntegerBase(ArrayList<Integer> integerBase) {
        this.integerBase = integerBase;
    }

    public ArrayList<String> getStringBase() {
        return stringBase;
    }

    public void setStringBase(ArrayList<String> stringBase) {
        this.stringBase = stringBase;
    }

    public int getNumOfImages() {
        return numOfImages;
    }

    public void setNumOfImages(int numOfImages) {
        this.numOfImages = numOfImages;
    }



    @Override
    public int describeContents() {
        return 0;
    }

}
