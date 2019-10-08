package com.ian.alog.sample;

import android.app.Application;

import com.ubtrobot.alog.ALog;

/**
 * Created by Ian on 19-10-8.
 */

public class SampleApplication extends Application {

    public void onCreate() {
        super.onCreate();
        ALog.init(getApplicationContext());
    }

}
