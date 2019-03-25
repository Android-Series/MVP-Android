package com.jxq.mvp;

import android.app.Application;

/**
 * Created by jxq
 */

public class TaxiApplication extends Application {

    private static TaxiApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance  = this;
    }
    public static TaxiApplication getInstance() {
        return  instance;
    }
}
