package org.restcomm.android.sdk;

/**
 * Created by nzery on 18-7-13.
 */

public class RCDevice {
    /**
     * Parameter keys for RCClient.createDevice() and RCDevice.reconfigure()
     */
    public static class ParameterKeys {
        public static final String INTENT_INCOMING_CALL = "incoming-call-intent";
        public static final String INTENT_INCOMING_MESSAGE = "incoming-message-intent";
        public static final String SIGNALING_USERNAME = "pref_sip_user";
        public static final String SIGNALING_DOMAIN = "pref_proxy_domain";
        public static final String SIGNALING_PASSWORD = "pref_sip_password";
        public static final String SIGNALING_SECURE_ENABLED = "signaling-secure";
        public static final String SIGNALING_LOCAL_PORT = "signaling-local-port";
        public static final String DEBUG_JAIN_SIP_LOGGING_ENABLED = "jain-sip-logging-enabled";
        public static final String DEBUG_DISABLE_CERTIFICATE_VERIFICATION = "disable-certificate-verification";
        // WARNING This is NOT for production. It's for Integration Tests, where there is no activity to receive call/message events
        public static final String DEBUG_USE_BROADCASTS_FOR_EVENTS = "debug-use-broadcast-for-events";
        public static final String MEDIA_TURN_ENABLED = "turn-enabled";
        public static final String MEDIA_ICE_SERVERS_DISCOVERY_TYPE = "media-ice-servers-discovery-type";
        public static final String MEDIA_ICE_SERVERS = "media-ice-servers";
        //public static final String MEDIA_ICE_ENDPOINT = "media-ice-endpoint";
        public static final String MEDIA_ICE_URL = "turn-url";
        public static final String MEDIA_ICE_USERNAME = "turn-username";
        public static final String MEDIA_ICE_PASSWORD = "turn-password";
        public static final String MEDIA_ICE_DOMAIN = "ice-domain";
        public static final String RESOURCE_SOUND_CALLING = "sound-calling";
        public static final String RESOURCE_SOUND_RINGING = "sound-ringing";
        public static final String RESOURCE_SOUND_DECLINED = "sound-declined";
        public static final String RESOURCE_SOUND_MESSAGE = "sound-message";
        //push notifications
        public static final String PUSH_NOTIFICATIONS_APPLICATION_NAME = "push-application-name";
        public static final String PUSH_NOTIFICATIONS_ACCOUNT_EMAIL = "push-account-email";
        public static final String PUSH_NOTIFICATIONS_ACCOUNT_PASSWORD = "push-account-password";
        public static final String PUSH_NOTIFICATIONS_ENABLE_PUSH_FOR_ACCOUNT = "push-enable-push-for-account";
        public static final String PUSH_NOTIFICATIONS_PUSH_DOMAIN = "push-domain";
        public static final String PUSH_NOTIFICATIONS_HTTP_DOMAIN  = "push-http-domain";
        public static final String PUSH_NOTIFICATIONS_FCM_SERVER_KEY = "push-fcm-key";
        public static final String PUSH_NOTIFICATION_TIMEOUT_MESSAGING_SERVICE = "push-timeout-message-service";

    }
}
