package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.uprise.ordering.constant.ApplicationConstants;

/**
 * Created by cicciolina on 10/20/16.
 */
public class BranchModel implements Parcelable {

    private String name;
    private String address;
    private String contactNum;
    private ImageModel  branchsPic;
    private ImageModel permitsPic;


    public BranchModel() {
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(contactNum);
        parcel.writeParcelable(branchsPic, ApplicationConstants.BRANCH_PICS);
        parcel.writeParcelable(permitsPic, ApplicationConstants.BRANCH_PICS);
    }

    public BranchModel(Parcel in) {
        this();
        name = in.readString();
        address = in.readString();
        contactNum = in.readString();
        branchsPic = in.readParcelable(ImageModel.class.getClassLoader());
        permitsPic = in.readParcelable(ImageModel.class.getClassLoader());
    }

    public static final Creator<BranchModel> CREATOR = new Creator<BranchModel>() {
        @Override
        public BranchModel createFromParcel(Parcel in) {
            return new BranchModel(in);
        }

        @Override
        public BranchModel[] newArray(int size) {
            return new BranchModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public ImageModel getBranchsPic() {
        return branchsPic;
    }

    public void setBranchsPic(ImageModel branchsPic) {
        this.branchsPic = branchsPic;
    }

    public ImageModel getPermitsPic() {
        return permitsPic;
    }

    public void setPermitsPic(ImageModel permitsPic) {
        this.permitsPic = permitsPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
