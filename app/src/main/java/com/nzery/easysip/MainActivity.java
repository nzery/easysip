package com.nzery.easysip;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.nzery.easysip.service.SipService;

import org.restcomm.android.sdk.JainSipClient.JainSipCall;
import org.restcomm.android.sdk.JainSipClient.JainSipClient;
import org.restcomm.android.sdk.RCConnection;
import org.restcomm.android.sdk.util.RCClient;
import org.restcomm.android.sdk.util.RCLogger;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.HashMap;


public class MainActivity extends Activity implements JainSipCall.JainSipCallListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    SipService.SipBinder binder;
    SurfaceViewRenderer localRender;
    SurfaceViewRenderer remoteRender;
    EglBase eglBase;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        localRender.release();
        remoteRender.release();
        eglBase.release();
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = ((SipService.SipBinder) service);
            binder.init(localRender, remoteRender);
            binder.regListener(MainActivity.this);
            RCLogger.d(TAG, "onServiceConnected name" + name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            RCLogger.d(TAG, "onServiceDisconnected name:" + name);
            if (binder != null) {
                binder.unRegListener(MainActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eglBase = EglBase.create();
        localRender = findViewById(R.id.pip_video_view);
        remoteRender = findViewById(R.id.fullscreen_video_view);
        localRender.init(eglBase.getEglBaseContext(), null);
        localRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        remoteRender.init(eglBase.getEglBaseContext(), null);
        remoteRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        Intent serviceIntent = new Intent(this,
                SipService.class);
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.make_call) {
            binder.callOut("101");
        } else if (view.getId() == R.id.hangup_call) {
            binder.hangupCall();
        } else if (view.getId() == R.id.answer_call) {
            binder.answerCall();
        }
    }

    @Override
    public void onCallOutgoingPeerRingingEvent(String jobId) {
        RCLogger.d(TAG, "onCallOutgoingPeerRingingEvent jobId:" + jobId);
        runOnUiThread(() -> localRender.setVisibility(View.VISIBLE));

    }

    @Override
    public void onCallOutgoingConnectedEvent(String jobId, String sdpAnswer, HashMap<String, String> customHeaders) {
        RCLogger.d(TAG, "onCallOutgoingConnectedEvent jobId:" + jobId /*+ " sdpAnswer:" + sdpAnswer*/ + " customHeaders" + customHeaders);
        runOnUiThread(() -> remoteRender.setVisibility(View.VISIBLE));
    }

    @Override
    public void onCallIncomingConnectedEvent(String jobId) {
        RCLogger.d(TAG, "onCallIncomingConnectedEvent jobId:" + jobId);
        runOnUiThread(() -> remoteRender.setVisibility(View.VISIBLE));
    }

    @Override
    public void onCallLocalDisconnectedEvent(String jobId) {
        RCLogger.d(TAG, "onCallLocalDisconnectedEvent jobId:" + jobId);
        termited();
    }

    @Override
    public void onCallPeerDisconnectedEvent(String jobId) {
        RCLogger.d(TAG, "onCallPeerDisconnectedEvent jobId:" + jobId);
        termited();
    }

    @Override
    public void onCallIncomingCanceledEvent(String jobId) {
        RCLogger.d(TAG, "onCallIncomingCanceledEvent jobId:" + jobId);
        termited();
    }

    @Override
    public void onCallIgnoredEvent(String jobId) {
        RCLogger.d(TAG, "onCallIgnoredEvent jobId:" + jobId);
        termited();
    }

    @Override
    public void onCallErrorEvent(String jobId, RCClient.ErrorCodes status, String text) {
        RCLogger.d(TAG, "onCallErrorEvent jobId:" + jobId + " status:" + status + " text:" + text);
        termited();
    }

    @Override
    public void onCallArrivedEvent(String jobId, String peer, String sdpOffer, HashMap<String, String> customHeaders) {
        RCLogger.d(TAG, "onCallArrivedEvent jobId:" + jobId + " peer:" + peer /*+ " sdpOffer:" + sdpOffer*/ + " customHeaders:" + customHeaders);
        runOnUiThread(() -> localRender.setVisibility(View.VISIBLE));
    }

    @Override
    public void onCallDigitsEvent(String jobId, RCClient.ErrorCodes status, String text) {
        RCLogger.d(TAG, "onCallDigitsEvent jobId:" + jobId + " status:" + status + " text:" + text);
    }

    private void termited() {
        runOnUiThread(() -> {
            localRender.setVisibility(View.GONE);
            remoteRender.setVisibility(View.GONE);
        });
    }

}
