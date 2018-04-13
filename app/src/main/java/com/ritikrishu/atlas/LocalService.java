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
import com.indooratlas.android.sdk.IARegion;

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

    static class IncomingHandler extends Handler implements IALocationListener, IARegion.Listener {
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
                    final IARegion region = IARegion.floorPlan("floor plan id");
                    mIALocationManager.setLocation(IALocation.from(region));
                    mIALocationManager.requestLocationUpdates(IALocationRequest.create(), this);
                    mIALocationManager.registerRegionListener(this);
                    break;
                case STOP:
                    Toast.makeText(mContext, "Atlas service stopped !!!", Toast.LENGTH_LONG).show();
                    mIALocationManager.removeLocationUpdates(this);
                    mIALocationManager.unregisterRegionListener(this);
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
            Log.d(TAG, "Provider changed to -> " + s);

            String status = "UNKNOWN";
            switch (i){
                case IALocationManager.STATUS_AVAILABLE:
                    status = "Location service running normally.";
                    break;
                case IALocationManager.STATUS_LIMITED:
                    status = "Location service is running but with limited accuracy and functionality.";
                    break;
                case IALocationManager.STATUS_CALIBRATION_CHANGED:
                    status = "Calibration Quality Indicator.";
                    break;
                case IALocationManager.STATUS_TEMPORARILY_UNAVAILABLE:
                    status = "Location service temporarily unavailable. This could be due to no network connectivity.";
                    break;
                case IALocationManager.STATUS_OUT_OF_SERVICE:
                    status = "Location service is not available and the condition is not expected to resolve itself soon.";
                    break;
            }
            Log.d(TAG, "Atlas service status received -> " + status);
            Toast.makeText(mContext, "Atlas service status received -> " + status, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onEnterRegion(IARegion iaRegion) {
            if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
                Log.d(TAG, "Entered " + iaRegion.getName());
                Log.d(TAG, "floor plan ID: " + iaRegion.getId());
            }
            else if (iaRegion.getType() == IARegion.TYPE_VENUE) {
                Log.d(TAG, "Location changed to " + iaRegion.getId());
                mIALocationManager.setLocation(new IALocation.Builder()
                        .withRegion(iaRegion).build());
            }
        }

        @Override
        public void onExitRegion(IARegion iaRegion) {

        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}