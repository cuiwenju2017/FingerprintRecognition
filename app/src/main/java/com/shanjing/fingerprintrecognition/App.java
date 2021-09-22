package com.shanjing.fingerprintrecognition;

import android.app.Application;

import com.tencent.mmkv.MMKV;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化MMKV
        MMKV.initialize(this);
    }
}
