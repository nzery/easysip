package com.nzery.easysip.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.restcomm.android.sdk.JainSipClient.JainSipClient;
import org.restcomm.android.sdk.RCDevice;
import org.restcomm.android.sdk.listener.RCDeviceListener;
import org.restcomm.android.sdk.util.RCClient;
import org.restcomm.android.sdk.util.RCLogger;

import java.util.HashMap;

/**
 * Created by nzery on 18-7-13.
 */

public class SipService extends Service implements JainSipClient.JainSipClientListener {
    private static final String TAG = SipService.class.getSimpleName();

    private Handler mHandler;
    private String mJobId;
    JainSipClient mClient;
    SharedPreferences mConfig;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SipBinder(mClient);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mConfig = getSharedPreferences("sip-config", Context.MODE_PRIVATE);
        mConfig.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            }
        });
        HandlerThread handlerThread = new HandlerThread("sip-thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        mClient = new JainSipClient(mHandler);
        mJobId = Long.toString(System.currentTimeMillis());
        HashMap<String, Object> configuration = new HashMap<>();
        configuration.put(RCDevice.ParameterKeys.SIGNALING_DOMAIN, mConfig.getString(RCDevice.ParameterKeys.SIGNALING_DOMAIN, "pbx.starnetuc.com:5065"));
        configuration.put(RCDevice.ParameterKeys.SIGNALING_USERNAME, mConfig.getString(RCDevice.ParameterKeys.SIGNALING_USERNAME, "812304"));
        configuration.put(RCDevice.ParameterKeys.SIGNALING_PASSWORD, mConfig.getString(RCDevice.ParameterKeys.SIGNALING_PASSWORD, "28053888"));
        mClient.open(mJobId, SipService.this, configuration, SipService.this);
    }

    @Override
    public void onDestroy() {
        mClient.close(mJobId);
        super.onDestroy();
    }

    @Override
    public void onClientOpenedReply(String jobId, RCDeviceListener.RCConnectivityStatus connectivityStatus, RCClient.ErrorCodes status, String text) {
        RCLogger.d(TAG, "onClientOpenedReply jobId:" + jobId + " status:" + connectivityStatus + " errorcodes:" + status + " text:" + text + " threadId:" + Thread.currentThread().getId());
    }

    @Override
    public void onClientErrorReply(String jobId, RCDeviceListener.RCConnectivityStatus connectivityStatus, RCClient.ErrorCodes status, String text) {
        RCLogger.d(TAG, "onClientErrorReply jobId:" + jobId + " status:" + connectivityStatus + " errorcodes:" + status + " text:" + text);
    }

    @Override
    public void onClientClosedEvent(String jobId, RCClient.ErrorCodes status, String text) {
        RCLogger.d(TAG, "onClientClosedEvent jobId:" + jobId + " errorcodes:" + status + " text:" + text);
    }

    @Override
    public void onClientReconfigureReply(String jobId, RCDeviceListener.RCConnectivityStatus connectivityStatus, RCClient.ErrorCodes status, String text) {
        RCLogger.d(TAG, "onClientReconfigureReply jobId:" + jobId + " status:" + connectivityStatus + " errorcodes:" + status + " text:" + text);
    }

    @Override
    public void onClientConnectivityEvent(String jobId, RCDeviceListener.RCConnectivityStatus connectivityStatus) {
        RCLogger.d(TAG, "onClientConnectivityEvent jobId:" + jobId + " status:" + connectivityStatus);
    }

    @Override
    public void onClientMessageArrivedEvent(String jobId, String peer, String messageText) {
        RCLogger.d(TAG, "onClientMessageArrivedEvent jobId:" + jobId + " peer:" + peer + " message:" + messageText);
    }

    @Override
    public void onClientMessageReply(String jobId, RCClient.ErrorCodes status, String text) {
        RCLogger.d(TAG, "onClientMessageReply jobId:" + jobId + " errorCodes:" + status + " text:" + text);
    }

    @Override
    public void onClientRegisteringEvent(String jobId) {
        RCLogger.d(TAG, "onClientRegisteringEvent jobId:" + jobId);
    }
}
