package com.zhaoyin.analytics;

import android.app.Application;

public class JBApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JBAnalytics.initAnalytics(this.getApplicationContext(),"https://app.goldrock.cn");

    }
}
