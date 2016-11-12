package com.garrisonthomas.junkapp.entryobjects;


import com.google.firebase.database.Exclude;

/**
 * Created by Garrison on 2016-08-07.
 */

public class DailyJournalObject {

    private String driver, driverTime, navigator, navTime,
            endOfDayNotes, date, truckNumber;
    @Exclude
    private int percentOfGoal, percentOnDumps, totalGrossProfit, totalDumpCost;
    //    @Exclude
    private boolean isArchived;

    public DailyJournalObject() {

        // empty default constructor, necessary for Firebase to be able to deserialize

    }

    public int getTotalDumpCost() {
        return totalDumpCost;
    }

    public void setTotalDumpCost(int totalDumpCost) {
        this.totalDumpCost = totalDumpCost;
    }

    public int getTotalGrossProfit() {
        return totalGrossProfit;
    }

    public void setTotalGrossProfit(int totalGrossProfit) {
        this.totalGrossProfit = totalGrossProfit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriverTime() {
        return driverTime;
    }

    public void setDriverTime(String driverTime) {
        this.driverTime = driverTime;
    }

    public String getNavigator() {
        return navigator;
    }

    public void setNavigator(String navigator) {
        this.navigator = navigator;
    }

    public String getNavTime() {
        return navTime;
    }

    public void setNavTime(String navTime) {
        this.navTime = navTime;
    }

    public String getEndOfDayNotes() {
        return endOfDayNotes;
    }

    public void setEndOfDayNotes(String endOfDayNotes) {
        this.endOfDayNotes = endOfDayNotes;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public int getPercentOfGoal() {
        return percentOfGoal;
    }

    public void setPercentOfGoal(int percentOfGoal) {
        this.percentOfGoal = percentOfGoal;
    }

    public int getPercentOnDumps() {
        return percentOnDumps;
    }

    public void setPercentOnDumps(int percentOnDumps) {
        this.percentOnDumps = percentOnDumps;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

}
