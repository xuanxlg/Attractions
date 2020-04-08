package com.xuan.attractions;

import android.app.Application;

import net.gotev.speech.Speech;

public class Initializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Speech.init(this);
    }
}
