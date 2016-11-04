package com.garrisonthomas.junkapp.entryobjects;

/**
 * Created by Garrison on 2016-08-08.
 */
public class FuelObject {

    private String fuelVendor, fuelReceiptNumber;
    private double fuelCost;

    public String getFuelVendor() {
        return fuelVendor;
    }

    public void setFuelVendor(String fuelVendor) {
        this.fuelVendor = fuelVendor;
    }

    public String getFuelReceiptNumber() {
        return fuelReceiptNumber;
    }

    public void setFuelReceiptNumber(String fuelReceiptNumber) {
        this.fuelReceiptNumber = fuelReceiptNumber;
    }

    public double getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(double fuelCost) {
        this.fuelCost = fuelCost;
    }
}
