package com.garrisonthomas.junkapp.entryobjects;

/**
 * Created by Garrison on 2016-08-24.
 */

public class TransferStationObject {

    private String address, info, name, phoneNumber;
    private int minimum;
    private double rate;

    public TransferStationObject() {

        // empty default constructor, necessary for Firebase to be able to deserialize

    }

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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }
}
