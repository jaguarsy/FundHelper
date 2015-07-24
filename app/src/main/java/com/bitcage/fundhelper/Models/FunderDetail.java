package com.bitcage.fundhelper.Models;

import android.graphics.Bitmap;

/**
 * Created by JohnnyCage on 2015-7-24.
 */
public class FunderDetail {

    public String Date;
    public String UnitWorth;
    public String EstimateWorth;
    public String EstimateRiseRate;
    public String EstimateDate;
    public Bitmap Image;

    public FunderDetail(String date,
                        String unitWorth,
                        String estimateWorth,
                        String estimateRiseRate,
                        String estimateDate,
                        Bitmap image) {
        this.Date = date;
        this.UnitWorth = unitWorth;
        this.EstimateWorth = estimateWorth;
        this.EstimateRiseRate = estimateRiseRate;
        this.EstimateDate = estimateDate;
        this.Image = image;
    }
}
