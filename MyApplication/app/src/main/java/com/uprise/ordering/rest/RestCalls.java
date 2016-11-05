package com.uprise.ordering.rest;

/**
 * Created by cicciolina on 10/16/16.
 */
public enum RestCalls {
//    CHECKIN(0x01),
//    CHECKOUT(0x02),
//    DEVICE_REGISTER(0x03),
//    DEVICE_LIST_ALL(0x04),
//    DEVICE_LIST(0x05),
//    REQUEST_STATUS(0x06),
//    TEST(0x07),
//    MAINTENANCE(0x08),
//    UNINSTALL(0x09),
    PICTURE(0x69),
    REGISTRATION(0x70);
//    SPEEDTEST_SERVER_LIST(0x11),
//    DEVICE_DETAILS(0x12);

    int id;

    RestCalls(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

}
