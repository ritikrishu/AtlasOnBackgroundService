package com.ritikrishu.atlas;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;

public class LocalService extends Service{
    // Binder given to clients
    final IncomingHandler mIncomingHandler = new IncomingHandler();
    final Messenger mMessenger = new Messenger(mIncomingHandler);
    public static final int START = 1;
    public static final int STOP = 2;



    @Override
    public void onCreate() {
        super.onCreate();
        mIncomingHandler.initLocationManager(this);
    }

    static class IncomingHandler extends Handler implements IALocationListener  {
        private static final String TAG = "SGATLAS";
        IALocationManager mIALocationManager;
        Context mContext;
        IncomingHandler initLocationManager(Context ctx){
            mContext = ctx;
            mIALocationManager = IALocationManager.create(ctx);
            return this;
        }
        @Override
        public void handleMessage(Message msg) {
            if(mIALocationManager == null) {
                return;
            }
            switch (msg.what) {
                case START:
                    Toast.makeText(mContext, "Atlas service started !!!", Toast.LENGTH_LONG).show();
                    mIALocationManager.requestLocationUpdates(IALocationRequest.create(), this);
                    break;
                case STOP:
                    Toast.makeText(mContext, "Atlas service stopped !!!", Toast.LENGTH_LONG).show();
                    mIALocationManager.removeLocationUpdates(this);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        @Override
        public void onLocationChanged(IALocation iaLocation) {
            Log.d(TAG, "Atlas service location received -> " + iaLocation.getLatitude() + " " + iaLocation.getLongitude());
            Toast.makeText(mContext, "Atlas service location received -> " + iaLocation.getLatitude() + " " + iaLocation.getLongitude(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d(TAG, "Atlas service status received -> " + s);
            Toast.makeText(mContext, "Atlas service status received -> " + s, Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}