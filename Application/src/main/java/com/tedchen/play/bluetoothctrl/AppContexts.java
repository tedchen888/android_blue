package com.tedchen.play.bluetoothctrl;

import android.app.Application;

/**
 * Created by tedchen on 2017/3/18.
 */

public class AppContexts extends Application {

    public BluetoothCtrlService mChatService = null;

    public static AppContexts getInstance() {
        return instance;
    }

    private static AppContexts instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance  = this;
    }
}
