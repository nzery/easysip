package com.nzery.easysip.service;

import android.os.Binder;

import org.restcomm.android.sdk.JainSipClient.JainSipClient;

/**
 * Created by nzery on 18-7-13.
 */

public class SipBinder extends Binder {
    JainSipClient mSipClient;

    public SipBinder(JainSipClient sipClient) {
        mSipClient = sipClient;
    }

    public JainSipClient getSipClient() {
        return mSipClient;
    }

}
