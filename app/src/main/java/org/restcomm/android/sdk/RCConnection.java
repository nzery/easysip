package org.restcomm.android.sdk;

/**
 * Created by nzery on 18-7-13.
 */

public class RCConnection {
    /**
     * Parameter keys for RCCDevice.connect() and RCConnection.accept()
     */
    public static class ParameterKeys {
        public static final String CONNECTION_PEER = "username";
        public static final String CONNECTION_VIDEO_ENABLED = "video-enabled";
        public static final String CONNECTION_LOCAL_VIDEO = "local-video";
        public static final String CONNECTION_REMOTE_VIDEO = "remote-video";
        public static final String CONNECTION_PREFERRED_AUDIO_CODEC = "preferred-audio-codec";
        // Preferred local video codec
        public static final String CONNECTION_PREFERRED_VIDEO_CODEC = "preferred-video-codec";
        // Preferred local video resolution (needs to be supported by local camera)
        public static final String CONNECTION_PREFERRED_VIDEO_RESOLUTION = "preferred-video-resolution";
        public static final String CONNECTION_PREFERRED_VIDEO_FRAME_RATE = "preferred-video-frame-rate";
        public static final String CONNECTION_CUSTOM_SIP_HEADERS = "sip-headers";
        // Incoming headers from Restcomm both for incoming and outgoing calls
        public static final String CONNECTION_CUSTOM_INCOMING_SIP_HEADERS = "sip-headers-incoming";
        public static final String CONNECTION_SIP_HEADER_KEY_CALL_SID = "X-RestComm-CallSid";

        // Until we have trickle, as a way to timeout sooner than 40 seconds (webrtc default timeout)
        public static final String DEBUG_CONNECTION_CANDIDATE_TIMEOUT = "debug-connection-candidate-timeout";
    }
}
