package com.garrisonthomas.junkapp.entryobjects;

/**
 * Created by Garrison on 2016-09-05.
 */
public class RebateObject {

    private String rebateLocation, materialType;
    private int receiptNumber;
    private int rebateAmount;
    private int percentPrevious;
    private double rebateTonnage;

    public int getPercentPrevious() {
        return percentPrevious;
    }

    public void setPercentPrevious(int percentPrevious) {
        this.percentPrevious = percentPrevious;
    }

    public RebateObject() {

        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public String getRebateLocation() {
        return rebateLocation;
    }

    public void setRebateLocation(String rebateLocation) {
        this.rebateLocation = rebateLocation;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public double getRebateTonnage() {
        return rebateTonnage;
    }

    public void setRebateTonnage(double rebateTonnage) {
        this.rebateTonnage = rebateTonnage;
    }

    public int getRebateAmount() {
        return rebateAmount;
    }

    public void setRebateAmount(int rebateAmount) {
        this.rebateAmount = rebateAmount;
    }

    public int getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(int receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
}
