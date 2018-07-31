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

import org.restcomm.android.sdk.JainSipClient.JainSipCall;
import org.restcomm.android.sdk.JainSipClient.JainSipClient;
import org.restcomm.android.sdk.RCConnection;
import org.restcomm.android.sdk.RCDevice;
import org.restcomm.android.sdk.listener.RCDeviceListener;
import org.restcomm.android.sdk.util.RCClient;
import org.restcomm.android.sdk.util.RCLogger;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nzery on 18-7-13.
 */

public class SipService extends Service implements JainSipClient.JainSipClientListener, JainSipCall.JainSipCallListener {
    private static final String TAG = SipService.class.getSimpleName();

    private Handler mHandler;
    private String mJobId;
    JainSipClient mClient;
    SharedPreferences mConfig;
    List<JainSipCall.JainSipCallListener> listeners = new ArrayList<>();


    private PeerConnectionFactory factory;
    private PeerConnection peerConnection;
    SDPObserver sdpObserver = new SDPObserver();
    private boolean isCallOut = true;
    private String currentNum = "";
    private List<IceCandidate> iceCandidates = new LinkedList<>();
    private List<IceCandidate> remoteIceCandidates = new LinkedList<>();
    private SessionDescription localAnswerSdp = null;
    VideoSource videoSource;
    VideoCapturer videoCapturer;
    private VideoSink localRender;
    private VideoSink remoteRender;

    public class SipBinder extends Binder {

        public SipBinder() {
        }

        public void init(VideoSink lTrack, VideoSink rTrack) {
            localRender = lTrack;
            remoteRender = rTrack;
        }

        public void regListener(JainSipCall.JainSipCallListener listener) {
            listeners.add(listener);
            RCLogger.i(TAG, "regListener current size:" + listeners.size());
        }

        public void unRegListener(JainSipCall.JainSipCallListener listener) {
            listeners.remove(listener);
            RCLogger.i(TAG, "unRegListener current size:" + listeners.size());
        }

        public void callOut(String number) {
            currentNum = number;
            createConnection();
            peerConnection.createOffer(sdpObserver, new MediaConstraints());
        }

        public void answerCall() {
            if (localAnswerSdp != null) {
                peerConnection.setLocalDescription(sdpObserver, localAnswerSdp);
            }
        }

        public void hangupCall() {
            mClient.disconnect(mJobId, "user hangup", SipService.this);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SipBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mConfig = getSharedPreferences("sip-config", Context.MODE_PRIVATE);
        mConfig.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {

        });
        HandlerThread handlerThread = new HandlerThread("sip-thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        mClient = new JainSipClient(mHandler);
        mJobId = Long.toString(System.currentTimeMillis());
        HashMap<String, Object> configuration = new HashMap<>();
        configuration.put(RCDevice.ParameterKeys.DEBUG_JAIN_SIP_LOGGING_ENABLED, true);

        configuration.put(RCDevice.ParameterKeys.SIGNALING_DOMAIN, mConfig.getString(RCDevice.ParameterKeys.SIGNALING_DOMAIN, "192.168.11.100"));
        configuration.put(RCDevice.ParameterKeys.SIGNALING_USERNAME, mConfig.getString(RCDevice.ParameterKeys.SIGNALING_USERNAME, "102"));
        configuration.put(RCDevice.ParameterKeys.SIGNALING_PASSWORD, mConfig.getString(RCDevice.ParameterKeys.SIGNALING_PASSWORD, "28053888"));
        mClient.open(mJobId, SipService.this, configuration, SipService.this);


        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions());
        factory = PeerConnectionFactory.builder().createPeerConnectionFactory();

    }

    private void createConnection() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        PeerConnection.IceServer iceServer1 = new PeerConnection.IceServer("stun:192.168.11.100");
        PeerConnection.IceServer iceServer = new PeerConnection.IceServer("turn:192.168.11.100", "nouser", "nopasswd");
        iceServers.add(iceServer1);
        iceServers.add(iceServer);
        peerConnection = factory.createPeerConnection(iceServers, new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                RCLogger.d(TAG, "onSignalingChange signalingState:" + signalingState);
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

                RCLogger.d(TAG, "onIceConnectionChange state:" + iceConnectionState);
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                RCLogger.d(TAG, "onIceConnectionReceivingChange b:" + b);
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                RCLogger.d(TAG, "onIceGatheringChange state:" + iceGatheringState);
                if (iceGatheringState == PeerConnection.IceGatheringState.COMPLETE
                        && peerConnection.getLocalDescription() != null) {
                    if (isCallOut) {
                        HashMap<String, Object> param = new HashMap<>();
                        param.put(RCConnection.ParameterKeys.CONNECTION_PEER, currentNum);
                        param.put("sdp", generateSipSdp(peerConnection.getLocalDescription(), iceCandidates));
                        mClient.call(mJobId, param, SipService.this);
                    } else {
                        HashMap<String, Object> param = new HashMap<>();
                        param.put("sdp", generateSipSdp(peerConnection.getLocalDescription(), iceCandidates));
                        mClient.accept(mJobId, param, SipService.this);
                    }
                }
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                RCLogger.d(TAG, "onIceCandidate candidate:" + iceCandidate);
                iceCandidates.add(iceCandidate);
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                RCLogger.d(TAG, "onIceCandidatesRemoved candidates:" + iceCandidates);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                RCLogger.d(TAG, "onAddStream stream:" + mediaStream);
                if (mediaStream.videoTracks.size() == 1) {
                    VideoTrack videoTrack = mediaStream.videoTracks.get(0);
                    videoTrack.addSink(remoteRender);
                }
            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                RCLogger.d(TAG, "onRemoveStream stream:" + mediaStream);
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                RCLogger.d(TAG, "onDataChannel dataChannel:" + dataChannel);
            }

            @Override
            public void onRenegotiationNeeded() {
                RCLogger.d(TAG, "onRenegotiationNeeded");
            }

            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                RCLogger.d(TAG, "onAddTrack rtpReceiver:" + rtpReceiver + " streams:" + mediaStreams);
            }

            @Override
            public void onTrack(RtpTransceiver rtpTransceiver) {
                RCLogger.d(TAG, "onTrack rtpTransceiver:" + rtpTransceiver);
            }
        });

        peerConnection.addTrack(createAudioTrack());
        videoCapturer = createCameraCapturer(new Camera1Enumerator(false));
        peerConnection.addTrack(createVideoTrack(videoCapturer));
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private String generateSipSdp(SessionDescription offerSdp, List<IceCandidate> iceCandidates) {
        // concatenate all candidates in one String
        String audioCandidates = "";
        String videoCandidates = "";
        boolean isVideo = false;
        for (IceCandidate candidate : iceCandidates) {
            //Log.e(TAG, "@@@@ candidate.sdp: " + candidate.sdp);
            // Remember that chrome distinguishes audio vs video with different names than FF.
            // Chrome uses 'audio' while FF uses 'sdparta_0' for audio
            // and chrome uses 'video' while FF uses 'sdparta_1' for video
            if (candidate.sdpMid.equals("audio") || candidate.sdpMid.equals("sdparta_0")) {
                audioCandidates += "a=" + candidate.sdp + "\r\n";
            }
            if (candidate.sdpMid.equals("video") || candidate.sdpMid.equals("sdparta_1")) {
                videoCandidates += "a=" + candidate.sdp + "\r\n";
                isVideo = true;
            }
        }
        //Log.e(TAG, "@@@@ audio candidates: " + audioCandidates);
        //Log.e(TAG, "@@@@ video candidates: " + videoCandidates);
        //Log.e(TAG, "@@@@ Before replace: " + offerSdp.description);
        // first, audio
        // place the candidates after the 'a=rtcp:' string; use replace all because
        // we are supporting both audio and video so more than one replacements will be made
        //String resultString = offerSdp.description.replaceFirst("(a=rtcp:.*?\\r\\n)", "$1" + audioCandidates);

        Matcher matcher = Pattern.compile("(a=rtcp:.*?\\r\\n)").matcher(offerSdp.description);
        int index = 0;
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            if (index == 0) {
                // audio
                matcher.appendReplacement(stringBuffer, "$1" + audioCandidates);
            } else {
                // video
                matcher.appendReplacement(stringBuffer, "$1" + videoCandidates);
            }
            index++;
        }
        matcher.appendTail(stringBuffer);

        //Log.v(TAG, "@@@@ After replace: " + stringBuffer.toString());

        return stringBuffer.toString();
    }

    private AudioTrack createAudioTrack() {
        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        AudioTrack localAudioTrack = factory.createAudioTrack("ARDAMSa0", audioSource);
        localAudioTrack.setEnabled(true);
        return localAudioTrack;
    }

    private VideoTrack createVideoTrack(VideoCapturer capturer) {
        videoSource = factory.createVideoSource(capturer);
        capturer.startCapture(1280, 720, 30);

        VideoTrack localVideoTrack = factory.createVideoTrack("ARDAMSv0", videoSource);
        localVideoTrack.setEnabled(true);
        localVideoTrack.addSink(localRender);
        return localVideoTrack;
    }


    private class SDPObserver implements SdpObserver {

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            RCLogger.d(TAG, "onCreateSuccess sdp:" + sessionDescription.description + " type:" + sessionDescription.type);
            if (sessionDescription.type == SessionDescription.Type.OFFER) {
                peerConnection.setLocalDescription(sdpObserver, sessionDescription);
            } else {
                localAnswerSdp = sessionDescription;
            }
        }

        @Override
        public void onSetSuccess() {
            RCLogger.d(TAG, "onSetSuccess!");
            if (isCallOut) {
                if (peerConnection.getRemoteDescription() != null) {
                    drainCandidates();
                }
            } else {
                if (peerConnection.getLocalDescription() != null) {
                    drainCandidates();
                }
            }
        }

        private void drainCandidates() {
            for (IceCandidate candidate : remoteIceCandidates) {
                peerConnection.addIceCandidate(candidate);
            }
            remoteIceCandidates.clear();
        }


        @Override
        public void onCreateFailure(String s) {
            RCLogger.d(TAG, "onCreateFailure reason:" + s);
        }

        @Override
        public void onSetFailure(String s) {
            RCLogger.d(TAG, "onSetFailure reason:" + s);
        }
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


    // gets a full SDP and a. populates .iceCandidates with individual candidates, and
    // b. removes the candidates from the SDP string and returns it as .offerSdp
    public SessionDescription extractCandidates(String sdp, SessionDescription.Type type) {

        // first parse the candidates
        // TODO: for video to work properly we need to do some more work to split the full SDP and differentiate candidates
        // based on media type (i.e. audio vs. video)
        //Matcher matcher = Pattern.compile("a=(candidate.*?)\\r\\n").matcher(sdp.description);
        Matcher matcher = Pattern.compile("m=audio|m=video|a=(candidate.*)\\r\\n").matcher(sdp);
        String collectionState = "none";
        while (matcher.find()) {
            if (matcher.group(0).equals("m=audio")) {
                collectionState = "audio";
                continue;
            }
            if (matcher.group(0).equals("m=video")) {
                collectionState = "video";
                continue;
            }

            IceCandidate iceCandidate = new IceCandidate(collectionState, 0, matcher.group(1));
            remoteIceCandidates.add(iceCandidate);
        }

        // remove candidates from SDP
        SessionDescription offerSdp = new SessionDescription(type, sdp.replaceAll("a=candidate.*?\\r\\n", ""));

        // remove candidates from SDP together with any 'end-of-candidates' a-line.
        //params.offerSdp = new SessionDescription(sdp.type, sdp.description.replaceAll("a=candidate.*?\\r\\n", "").replaceAll("a=end-of-candidates.*?\\r\\n", ""));

        return offerSdp;
    }


    @Override
    public void onCallOutgoingPeerRingingEvent(String jobId) {
        //RCLogger.d(TAG, "onCallOutgoingPeerRingingEvent jobId:" + jobId);
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallOutgoingPeerRingingEvent(jobId);
        }
    }

    @Override
    public void onCallOutgoingConnectedEvent(String jobId, String sdpAnswer, HashMap<String, String> customHeaders) {
        //RCLogger.d(TAG, "onCallOutgoingConnectedEvent jobId:" + jobId + " sdpAnswer:" + sdpAnswer + " customHeaders" + customHeaders);
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallOutgoingConnectedEvent(jobId, sdpAnswer, customHeaders);
        }
        SessionDescription sdp = extractCandidates(sdpAnswer, SessionDescription.Type.ANSWER);
        peerConnection.setRemoteDescription(sdpObserver, sdp);
    }

    @Override
    public void onCallIncomingConnectedEvent(String jobId) {
        //RCLogger.d(TAG, "onCallIncomingConnectedEvent jobId:" + jobId);
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallIncomingConnectedEvent(jobId);
        }
    }

    @Override
    public void onCallLocalDisconnectedEvent(String jobId) {
        //RCLogger.d(TAG, "onCallLocalDisconnectedEvent jobId:" + jobId);
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallLocalDisconnectedEvent(jobId);
        }
        callTermited();
    }

    @Override
    public void onCallPeerDisconnectedEvent(String jobId) {
        //RCLogger.d(TAG, "onCallPeerDisconnectedEvent jobId:" + jobId);
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallPeerDisconnectedEvent(jobId);
        }
        callTermited();
    }

    @Override
    public void onCallIncomingCanceledEvent(String jobId) {
        //RCLogger.d(TAG, "onCallIncomingCanceledEvent jobId:" + jobId);
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallIncomingCanceledEvent(jobId);
        }
        callTermited();
    }

    @Override
    public void onCallIgnoredEvent(String jobId) {
        //RCLogger.d(TAG, "onCallIgnoredEvent jobId:" + jobId);
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallIgnoredEvent(jobId);
        }
        callTermited();
    }

    @Override
    public void onCallErrorEvent(String jobId, RCClient.ErrorCodes status, String text) {
        //RCLogger.d(TAG, "onCallErrorEvent jobId:" + jobId + " status:" + status + " text:" + text);
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallErrorEvent(jobId, status, text);
        }
        callTermited();
    }

    @Override
    public void onCallArrivedEvent(String jobId, String peer, String sdpOffer, HashMap<String, String> customHeaders) {
        //RCLogger.d(TAG, "onCallArrivedEvent jobId:" + jobId + " peer:" + peer + " sdpOffer:" + sdpOffer + " customHeaders:" + customHeaders);
        isCallOut = false;
        mJobId = jobId;
        createConnection();
        SessionDescription sdp = extractCandidates(sdpOffer, SessionDescription.Type.OFFER);
        peerConnection.setRemoteDescription(sdpObserver, sdp);
        peerConnection.createAnswer(sdpObserver, new MediaConstraints());
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallArrivedEvent(jobId, peer, sdpOffer, customHeaders);
        }
    }

    @Override
    public void onCallDigitsEvent(String jobId, RCClient.ErrorCodes status, String text) {
        // RCLogger.d(TAG, "onCallDigitsEvent jobId:" + jobId + " status:" + status + " text:" + text);
        for (JainSipCall.JainSipCallListener l : listeners) {
            l.onCallDigitsEvent(jobId, status, text);
        }
    }

    private void callTermited() {
        if (peerConnection != null) {
            peerConnection.dispose();
            peerConnection = null;
        }
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            videoCapturer.dispose();
            videoCapturer = null;
        }
        if (videoSource != null) {
            videoSource.dispose();
            videoSource = null;
        }
        isCallOut = true;
        iceCandidates.clear();
        localAnswerSdp = null;
    }
}
