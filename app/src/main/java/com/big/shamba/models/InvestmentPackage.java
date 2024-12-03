package com.big.shamba.models;


import android.util.Log;

import java.io.Serializable;

public class InvestmentPackage implements Serializable {
    private static final String TAG = "Package";
    private String packageId; //ID of the package
    private String name;
    private String type; //POULTRY/SWINE/CATTLE
    private Range range; // e.g from $10 - $150
    private String imgUrl;
    private float interestRate; // 1.0 - 100.0
    private int timePeriod; // in days

    // Default constructor required for calls to DataSnapshot.getValue(Package.class)
    public InvestmentPackage() {
    }

    public InvestmentPackage(String name, String type, Range range, float interestRate, int timePeriod) {
        this.name = name;
        this.type = type;
        this.range = range;
        this.interestRate = interestRate;
        this.timePeriod = timePeriod;
        Log.d(TAG, "Package: " + this);
    }

    //With PackageId
    public InvestmentPackage(String packageId, String name, String type, Range range, float interestRate, int timePeriod) {
        this.packageId = packageId;
        this.name = name;
        this.type = type;
        this.range = range;
        this.interestRate = interestRate;
        this.timePeriod = timePeriod;
        Log.d(TAG, "Package: " + this);
    }

    public InvestmentPackage(String packageId, String name, String type, Range range, String imgUrl, float interestRate, int timePeriod) {
        this.packageId = packageId;
        this.name = name;
        this.type = type;
        this.range = range;
        this.imgUrl = imgUrl;
        this.interestRate = interestRate;
        this.timePeriod = timePeriod;
        Log.d(TAG, "Package: " + this);
    }

    public InvestmentPackage(String name, String type, Range range, String imgUrl, float interestRate, int timePeriod) {
        this.name = name;
        this.type = type;
        this.range = range;
        this.imgUrl = imgUrl;
        this.interestRate = interestRate;
        this.timePeriod = timePeriod;
        Log.d(TAG, "Package: " + this);
    }

    @Override
    public String toString() {
        return "InvestmentPackage{" +
                "packageId='" + packageId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", range=" + range +
                ", imgUrl='" + imgUrl + '\'' +
                ", interestRate=" + interestRate +
                ", timePeriod=" + timePeriod +
                '}';
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }

    public static class Range {
        private double min;
        private double max;

        // Default constructor required for calls to DataSnapshot.getValue(Range.class)
        public Range() {
            Log.d(TAG, "Range: " + this);
        }

        public Range(double min, double max) {
            this.min = min;
            this.max = max;
            Log.d(TAG, "Range: " + this);
        }

        @Override
        public String toString() {
            return "Range{" +
                    "min=" + min +
                    ", max=" + max +
                    '}';
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }
    }
}
