package com.garrisonthomas.junkapp.entryobjects;

/**
 * Created by Garrison on 2016-08-06.
 */

public class JobObject {

    private int SID;
    private int receiptNumber;
    private Integer ccExpDate;
    private Long ccNumber;
    private double grossSale, netSale;
    private String time, payType, jobNotes, jobType;

    public Integer getCcExpDate() {
        return ccExpDate;
    }

    public void setCcExpDate(Integer ccExpDate) {

        if (ccExpDate != null) {
            this.ccExpDate = ccExpDate;
        }
    }

    public Long getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(Long ccNumber) {

        if (ccNumber != null) {
            this.ccNumber = ccNumber;
        }

    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public JobObject() {

        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public int getSID() {

        return SID;

    }

    public double getGrossSale() {

        return grossSale;

    }

    public double getNetSale() {

        return netSale;

    }

    public String getTime() {

        return time;

    }

    public int getReceiptNumber() {

        return receiptNumber;

    }

    public String getPayType() {

        return payType;

    }

    public String getJobNotes() {

        return jobNotes;

    }

    public void setSID(int SID) {
        this.SID = SID;
    }

    public void setGrossSale(double grossSale) {

        this.grossSale = grossSale;

    }


    public void setNetSale(double netSale) {

        this.netSale = netSale;

    }

    public void setTime(String time) {

        this.time = time;

    }

    public void setReceiptNumber(int receiptNumber) {

        this.receiptNumber = receiptNumber;

    }

    public void setPayType(String payType) {

        this.payType = payType;

    }

    public void setJobNotes(String jobNotes) {

        if (!jobNotes.equals("")) {
            this.jobNotes = jobNotes;
        }

    }

}
