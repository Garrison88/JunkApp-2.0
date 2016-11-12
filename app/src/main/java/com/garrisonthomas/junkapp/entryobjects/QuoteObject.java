package com.garrisonthomas.junkapp.entryobjects;

/**
 * Created by Garrison on 2016-08-09.
 */
public class QuoteObject {

    private int quoteSID;
    private double lowEnd, highEnd;
    private String time, quoteNotes, photoDownloadUrl;

    public String getPhotoDownloadUrl() {
        return photoDownloadUrl;
    }

    public void setPhotoDownloadUrl(String photoDownloadUrl) {
        this.photoDownloadUrl = photoDownloadUrl;
    }

    public int getQuoteSID() {
        return quoteSID;
    }

    public void setQuoteSID(int quoteSID) {
        this.quoteSID = quoteSID;
    }

    public double getLowEnd() {
        return lowEnd;
    }

    public void setLowEnd(double lowEnd) {
        this.lowEnd = lowEnd;
    }

    public double getHighEnd() {
        return highEnd;
    }

    public void setHighEnd(double highEnd) {
        if (highEnd != 0.0) {
            this.highEnd = highEnd;
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQuoteNotes() {
        return quoteNotes;
    }

    public void setQuoteNotes(String quoteNotes) {
//        if (quoteNotes != null) {
            this.quoteNotes = quoteNotes;
//        }
    }
}
