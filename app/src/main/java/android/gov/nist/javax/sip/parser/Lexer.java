/*
 * Conditions Of Use
 *
 * This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 Untied States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 *
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS."  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof, including but
 * not limited to the correctness, accuracy, reliability or usefulness of
 * the software.
 *
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement
 *
 * .
 *
 */
package android.gov.nist.javax.sip.parser;

import android.gov.nist.core.LexerCore;
import android.gov.nist.javax.sip.header.extensions.JoinHeader;
import android.gov.nist.javax.sip.header.extensions.MinSEHeader;
import android.gov.nist.javax.sip.header.extensions.ReferencesHeader;
import android.gov.nist.javax.sip.header.extensions.ReferredByHeader;
import android.gov.nist.javax.sip.header.extensions.ReplacesHeader;
import android.gov.nist.javax.sip.header.extensions.SessionExpiresHeader;
import android.gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader;
import android.gov.nist.javax.sip.header.ims.PAssertedIdentityHeader;
import android.gov.nist.javax.sip.header.ims.PAssertedServiceHeader;
import android.gov.nist.javax.sip.header.ims.PAssociatedURIHeader;
import android.gov.nist.javax.sip.header.ims.PCalledPartyIDHeader;
import android.gov.nist.javax.sip.header.ims.PChargingFunctionAddressesHeader;
import android.gov.nist.javax.sip.header.ims.PChargingVectorHeader;
import android.gov.nist.javax.sip.header.ims.PMediaAuthorizationHeader;
import android.gov.nist.javax.sip.header.ims.PPreferredIdentityHeader;
import android.gov.nist.javax.sip.header.ims.PPreferredServiceHeader;
import android.gov.nist.javax.sip.header.ims.PProfileKeyHeader;
import android.gov.nist.javax.sip.header.ims.PServedUserHeader;
import android.gov.nist.javax.sip.header.ims.PUserDatabaseHeader;
import android.gov.nist.javax.sip.header.ims.PVisitedNetworkIDHeader;
import android.gov.nist.javax.sip.header.ims.PathHeader;
import android.gov.nist.javax.sip.header.ims.PrivacyHeader;
import android.gov.nist.javax.sip.header.ims.SecurityClientHeader;
import android.gov.nist.javax.sip.header.ims.SecurityServerHeader;
import android.gov.nist.javax.sip.header.ims.SecurityVerifyHeader;
import android.gov.nist.javax.sip.header.ims.ServiceRouteHeader;

import java.util.concurrent.ConcurrentHashMap;

import android.javax.sip.header.AcceptEncodingHeader;
import android.javax.sip.header.AcceptHeader;
import android.javax.sip.header.AcceptLanguageHeader;
import android.javax.sip.header.AlertInfoHeader;
import android.javax.sip.header.AllowEventsHeader;
import android.javax.sip.header.AllowHeader;
import android.javax.sip.header.AuthenticationInfoHeader;
import android.javax.sip.header.AuthorizationHeader;
import android.javax.sip.header.CSeqHeader;
import android.javax.sip.header.CallIdHeader;
import android.javax.sip.header.CallInfoHeader;
import android.javax.sip.header.ContactHeader;
import android.javax.sip.header.ContentDispositionHeader;
import android.javax.sip.header.ContentEncodingHeader;
import android.javax.sip.header.ContentLanguageHeader;
import android.javax.sip.header.ContentLengthHeader;
import android.javax.sip.header.ContentTypeHeader;
import android.javax.sip.header.DateHeader;
import android.javax.sip.header.ErrorInfoHeader;
import android.javax.sip.header.EventHeader;
import android.javax.sip.header.ExpiresHeader;
import android.javax.sip.header.FromHeader;
import android.javax.sip.header.InReplyToHeader;
import android.javax.sip.header.MaxForwardsHeader;
import android.javax.sip.header.MimeVersionHeader;
import android.javax.sip.header.MinExpiresHeader;
import android.javax.sip.header.OrganizationHeader;
import android.javax.sip.header.PriorityHeader;
import android.javax.sip.header.ProxyAuthenticateHeader;
import android.javax.sip.header.ProxyAuthorizationHeader;
import android.javax.sip.header.ProxyRequireHeader;
import android.javax.sip.header.RAckHeader;
import android.javax.sip.header.RSeqHeader;
import android.javax.sip.header.ReasonHeader;
import android.javax.sip.header.RecordRouteHeader;
import android.javax.sip.header.ReferToHeader;
import android.javax.sip.header.ReplyToHeader;
import android.javax.sip.header.RequireHeader;
import android.javax.sip.header.RetryAfterHeader;
import android.javax.sip.header.RouteHeader;
import android.javax.sip.header.SIPETagHeader;
import android.javax.sip.header.SIPIfMatchHeader;
import android.javax.sip.header.ServerHeader;
import android.javax.sip.header.SubjectHeader;
import android.javax.sip.header.SubscriptionStateHeader;
import android.javax.sip.header.SupportedHeader;
import android.javax.sip.header.TimeStampHeader;
import android.javax.sip.header.ToHeader;
import android.javax.sip.header.UnsupportedHeader;
import android.javax.sip.header.UserAgentHeader;
import android.javax.sip.header.ViaHeader;
import android.javax.sip.header.WWWAuthenticateHeader;
import android.javax.sip.header.WarningHeader;

/**
 * Lexer class for the parser.
 *
 * @version 1.2
 *
 * @author M. Ranganathan <br/>
 *
 *
 */
public class Lexer extends LexerCore {
    /**
     * get the header name of the line
     *
     * @return the header name (stuff before the :) bug fix submitted by
     *         zvali@dev.java.net
     */
    public static String getHeaderName(String line) {
        if (line == null)
            return null;
        String headerName = null;
        try {
            int begin = line.indexOf(":");
            headerName = null;
            if (begin >= 1)
                headerName = line.substring(0, begin).trim();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return headerName;
    }

    public Lexer(String lexerName, String buffer) {
        super(lexerName, buffer);
        this.selectLexer(lexerName);
    }

    /**
     * get the header value of the line
     *
     * @return String
     */
    public static String getHeaderValue(String line) {
        if (line == null)
            return null;
        String headerValue = null;
        try {
            int begin = line.indexOf(":");
            headerValue = line.substring(begin + 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return headerValue;
    }

    public void selectLexer(String lexerName) {
        // Synchronization Bug fix by Robert Rosen.
    	ConcurrentHashMap<String, Integer> lexer = lexerTables.get(lexerName);
        this.currentLexerName = lexerName;
        if (lexer == null) {
        	ConcurrentHashMap<String, Integer> newLexer  = new ConcurrentHashMap<String, Integer>();
            // Temporarily set newLexer as current, so addKeyword populate it
            currentLexer = newLexer;
//          addLexer(lexerName);
            if (lexerName.equals("method_keywordLexer")) {
                addKeyword(TokenNames.REGISTER, TokenTypes.REGISTER);
                addKeyword(TokenNames.ACK, TokenTypes.ACK);
                addKeyword(TokenNames.OPTIONS, TokenTypes.OPTIONS);
                addKeyword(TokenNames.BYE, TokenTypes.BYE);
                addKeyword(TokenNames.INVITE, TokenTypes.INVITE);
                addKeyword(TokenNames.SIP, TokenTypes.SIP);
                addKeyword(TokenNames.SIPS, TokenTypes.SIPS);
                addKeyword(TokenNames.SUBSCRIBE, TokenTypes.SUBSCRIBE);
                addKeyword(TokenNames.NOTIFY, TokenTypes.NOTIFY);
                addKeyword(TokenNames.MESSAGE, TokenTypes.MESSAGE);

                // JvB: added to support RFC3903
                addKeyword(TokenNames.PUBLISH, TokenTypes.PUBLISH);

            } else if (lexerName.equals("command_keywordLexer")) {
                addKeyword(ErrorInfoHeader.NAME,
                        TokenTypes.ERROR_INFO);
                addKeyword(AllowEventsHeader.NAME,
                        TokenTypes.ALLOW_EVENTS);
                addKeyword(AuthenticationInfoHeader.NAME,
                        TokenTypes.AUTHENTICATION_INFO);
                addKeyword(EventHeader.NAME, TokenTypes.EVENT);
                addKeyword(MinExpiresHeader.NAME,
                        TokenTypes.MIN_EXPIRES);
                addKeyword(RSeqHeader.NAME, TokenTypes.RSEQ);
                addKeyword(RAckHeader.NAME, TokenTypes.RACK);
                addKeyword(ReasonHeader.NAME,
                        TokenTypes.REASON);
                addKeyword(ReplyToHeader.NAME,
                        TokenTypes.REPLY_TO);
                addKeyword(SubscriptionStateHeader.NAME,
                        TokenTypes.SUBSCRIPTION_STATE);
                addKeyword(TimeStampHeader.NAME,
                        TokenTypes.TIMESTAMP);
                addKeyword(InReplyToHeader.NAME,
                        TokenTypes.IN_REPLY_TO);
                addKeyword(MimeVersionHeader.NAME,
                        TokenTypes.MIME_VERSION);
                addKeyword(AlertInfoHeader.NAME,
                        TokenTypes.ALERT_INFO);
                addKeyword(FromHeader.NAME, TokenTypes.FROM);
                addKeyword(ToHeader.NAME, TokenTypes.TO);
                addKeyword(ReferToHeader.NAME,
                        TokenTypes.REFER_TO);
                addKeyword(ViaHeader.NAME, TokenTypes.VIA);
                addKeyword(UserAgentHeader.NAME,
                        TokenTypes.USER_AGENT);
                addKeyword(ServerHeader.NAME,
                        TokenTypes.SERVER);
                addKeyword(AcceptEncodingHeader.NAME,
                        TokenTypes.ACCEPT_ENCODING);
                addKeyword(AcceptHeader.NAME,
                        TokenTypes.ACCEPT);
                addKeyword(AllowHeader.NAME, TokenTypes.ALLOW);
                addKeyword(RouteHeader.NAME, TokenTypes.ROUTE);
                addKeyword(AuthorizationHeader.NAME,
                        TokenTypes.AUTHORIZATION);
                addKeyword(ProxyAuthorizationHeader.NAME,
                        TokenTypes.PROXY_AUTHORIZATION);
                addKeyword(RetryAfterHeader.NAME,
                        TokenTypes.RETRY_AFTER);
                addKeyword(ProxyRequireHeader.NAME,
                        TokenTypes.PROXY_REQUIRE);
                addKeyword(ContentLanguageHeader.NAME,
                        TokenTypes.CONTENT_LANGUAGE);
                addKeyword(UnsupportedHeader.NAME,
                        TokenTypes.UNSUPPORTED);
                addKeyword(SupportedHeader.NAME,
                        TokenTypes.SUPPORTED);
                addKeyword(WarningHeader.NAME,
                        TokenTypes.WARNING);
                addKeyword(MaxForwardsHeader.NAME,
                        TokenTypes.MAX_FORWARDS);
                addKeyword(DateHeader.NAME, TokenTypes.DATE);
                addKeyword(PriorityHeader.NAME,
                        TokenTypes.PRIORITY);
                addKeyword(ProxyAuthenticateHeader.NAME,
                        TokenTypes.PROXY_AUTHENTICATE);
                addKeyword(ContentEncodingHeader.NAME,
                        TokenTypes.CONTENT_ENCODING);
                addKeyword(ContentLengthHeader.NAME,
                        TokenTypes.CONTENT_LENGTH);
                addKeyword(SubjectHeader.NAME,
                        TokenTypes.SUBJECT);
                addKeyword(ContentTypeHeader.NAME,
                        TokenTypes.CONTENT_TYPE);
                addKeyword(ContactHeader.NAME,
                        TokenTypes.CONTACT);
                addKeyword(CallIdHeader.NAME,
                        TokenTypes.CALL_ID);
                addKeyword(RequireHeader.NAME,
                        TokenTypes.REQUIRE);
                addKeyword(ExpiresHeader.NAME,
                        TokenTypes.EXPIRES);
                addKeyword(RecordRouteHeader.NAME,
                        TokenTypes.RECORD_ROUTE);
                addKeyword(OrganizationHeader.NAME,
                        TokenTypes.ORGANIZATION);
                addKeyword(CSeqHeader.NAME, TokenTypes.CSEQ);
                addKeyword(AcceptLanguageHeader.NAME,
                        TokenTypes.ACCEPT_LANGUAGE);
                addKeyword(WWWAuthenticateHeader.NAME,
                        TokenTypes.WWW_AUTHENTICATE);
                addKeyword(CallInfoHeader.NAME,
                        TokenTypes.CALL_INFO);
                addKeyword(ContentDispositionHeader.NAME,
                        TokenTypes.CONTENT_DISPOSITION);
                // And now the dreaded short forms....
                addKeyword(TokenNames.K, TokenTypes.SUPPORTED);
                addKeyword(TokenNames.C,
                        TokenTypes.CONTENT_TYPE);
                addKeyword(TokenNames.E,
                        TokenTypes.CONTENT_ENCODING);
                addKeyword(TokenNames.F, TokenTypes.FROM);
                addKeyword(TokenNames.I, TokenTypes.CALL_ID);
                addKeyword(TokenNames.M, TokenTypes.CONTACT);
                addKeyword(TokenNames.L,
                        TokenTypes.CONTENT_LENGTH);
                addKeyword(TokenNames.S, TokenTypes.SUBJECT);
                addKeyword(TokenNames.T, TokenTypes.TO);
                addKeyword(TokenNames.U,
                        TokenTypes.ALLOW_EVENTS); // JvB: added
                addKeyword(TokenNames.V, TokenTypes.VIA);
                addKeyword(TokenNames.R, TokenTypes.REFER_TO);
                addKeyword(TokenNames.O, TokenTypes.EVENT); // Bug
                                                                            // fix
                                                                            // by
                                                                            // Mario
                                                                            // Mantak
                addKeyword(TokenNames.X, TokenTypes.SESSIONEXPIRES_TO); // Bug fix by Jozef Saniga
                
                // JvB: added to support RFC3903
                addKeyword(SIPETagHeader.NAME,
                        TokenTypes.SIP_ETAG);
                addKeyword(SIPIfMatchHeader.NAME,
                        TokenTypes.SIP_IF_MATCH);

                // pmusgrave: Add RFC4028 and ReferredBy
                addKeyword(SessionExpiresHeader.NAME,
                        TokenTypes.SESSIONEXPIRES_TO);
                addKeyword(MinSEHeader.NAME,
                        TokenTypes.MINSE_TO);
                addKeyword(ReferredByHeader.NAME,
                        TokenTypes.REFERREDBY_TO);

                // pmusgrave RFC3891
                addKeyword(ReplacesHeader.NAME,
                        TokenTypes.REPLACES_TO);
                //jean deruelle RFC3911
                addKeyword(JoinHeader.NAME,
                        TokenTypes.JOIN_TO);

                // IMS Headers
                addKeyword(PathHeader.NAME, TokenTypes.PATH);
                addKeyword(ServiceRouteHeader.NAME,
                        TokenTypes.SERVICE_ROUTE);
                addKeyword(PAssertedIdentityHeader.NAME,
                        TokenTypes.P_ASSERTED_IDENTITY);
                addKeyword(PPreferredIdentityHeader.NAME,
                        TokenTypes.P_PREFERRED_IDENTITY);
                addKeyword(PrivacyHeader.NAME,
                        TokenTypes.PRIVACY);

                // issued by Miguel Freitas
                addKeyword(PCalledPartyIDHeader.NAME,
                        TokenTypes.P_CALLED_PARTY_ID);
                addKeyword(PAssociatedURIHeader.NAME,
                        TokenTypes.P_ASSOCIATED_URI);
                addKeyword(PVisitedNetworkIDHeader.NAME,
                        TokenTypes.P_VISITED_NETWORK_ID);
                addKeyword(PChargingFunctionAddressesHeader.NAME,
                        TokenTypes.P_CHARGING_FUNCTION_ADDRESSES);
                addKeyword(PChargingVectorHeader.NAME,
                        TokenTypes.P_VECTOR_CHARGING);
                addKeyword(PAccessNetworkInfoHeader.NAME,
                        TokenTypes.P_ACCESS_NETWORK_INFO);
                addKeyword(PMediaAuthorizationHeader.NAME,
                        TokenTypes.P_MEDIA_AUTHORIZATION);

                addKeyword(SecurityServerHeader.NAME,
                        TokenTypes.SECURITY_SERVER);
                addKeyword(SecurityVerifyHeader.NAME,
                        TokenTypes.SECURITY_VERIFY);
                addKeyword(SecurityClientHeader.NAME,
                        TokenTypes.SECURITY_CLIENT);

                // added by aayush@rancore
                addKeyword(PUserDatabaseHeader.NAME,
                        TokenTypes.P_USER_DATABASE);

                // added by aayush@rancore
                addKeyword(PProfileKeyHeader.NAME,
                        TokenTypes.P_PROFILE_KEY);

                // added by aayush@rancore
                addKeyword(PServedUserHeader.NAME,
                        TokenTypes.P_SERVED_USER);

                // added by aayush@rancore
                addKeyword(PPreferredServiceHeader.NAME,
                        TokenTypes.P_PREFERRED_SERVICE);

                // added by aayush@rancore
                addKeyword(PAssertedServiceHeader.NAME,
                        TokenTypes.P_ASSERTED_SERVICE);
                
                // added References header
                addKeyword(ReferencesHeader.NAME,TokenTypes.REFERENCES);

                // end //


            } else if (lexerName.equals("status_lineLexer")) {
                addKeyword(TokenNames.SIP, TokenTypes.SIP);
            } else if (lexerName.equals("request_lineLexer")) {
                addKeyword(TokenNames.SIP, TokenTypes.SIP);
            } else if (lexerName.equals("sip_urlLexer")) {
                addKeyword(TokenNames.TEL, TokenTypes.TEL);
                addKeyword(TokenNames.SIP, TokenTypes.SIP);
                addKeyword(TokenNames.SIPS, TokenTypes.SIPS);
            }

            // Now newLexer is completely initialized, let's check if somebody
            // have put lexer in table
            lexer = lexerTables.putIfAbsent(lexerName, newLexer);
            if (lexer == null) {
                // put succeeded, use new value
                lexer = newLexer;                    
            }
            currentLexer = lexer;
        } else {
        	currentLexer = lexer;
        }
    }
}
