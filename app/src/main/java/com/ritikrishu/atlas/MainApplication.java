package com.ritikrishu.atlas;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

public class MainApplication extends Application {
    Messenger mService;
    boolean mBound = false;

    static MainApplication instance;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            mService = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mBound = false;
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public Messenger getService(){
        return mService;
    }

    public static MainApplication getInstance(){
        return instance;
    }
}
