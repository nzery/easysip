package com.nzery.easysip;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.nzery.easysip.service.SipService;

import org.restcomm.android.sdk.util.RCLogger;

/**
 * Created by nzery on 18-7-13.
 */

public class EasySipApplication extends Application {

    private static final String TAG = EasySipApplication.class.getSimpleName();

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RCLogger.d(TAG, "onServiceConnected compName:" + name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            RCLogger.d(TAG, "onServiceDisconnected compName:" + name);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Intent serviceIntent = new Intent(this,
                SipService.class);
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
    }
}
