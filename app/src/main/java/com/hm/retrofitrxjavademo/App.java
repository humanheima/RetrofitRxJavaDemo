package com.hm.retrofitrxjavademo;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Created by Administrator on 2016/11/26.
 */
public class App extends Application {

    private static Context instance;

    public static Context getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Stetho.initializeWithDefaults(this);
    }
}
