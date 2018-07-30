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
/*******************************************************************************
 * Product of NIST/ITL Advanced Networking Technologies Division (ANTD)         *
 *******************************************************************************/
package android.gov.nist.javax.sip.message;

import android.gov.nist.javax.sip.header.Accept;
import android.gov.nist.javax.sip.header.AcceptEncoding;
import android.gov.nist.javax.sip.header.AcceptEncodingList;
import android.gov.nist.javax.sip.header.AcceptLanguage;
import android.gov.nist.javax.sip.header.AcceptLanguageList;
import android.gov.nist.javax.sip.header.AcceptList;
import android.gov.nist.javax.sip.header.AlertInfo;
import android.gov.nist.javax.sip.header.AlertInfoList;
import android.gov.nist.javax.sip.header.Allow;
import android.gov.nist.javax.sip.header.AllowEvents;
import android.gov.nist.javax.sip.header.AllowEventsList;
import android.gov.nist.javax.sip.header.AllowList;
import android.gov.nist.javax.sip.header.Authorization;
import android.gov.nist.javax.sip.header.AuthorizationList;
import android.gov.nist.javax.sip.header.CallInfo;
import android.gov.nist.javax.sip.header.CallInfoList;
import android.gov.nist.javax.sip.header.Contact;
import android.gov.nist.javax.sip.header.ContactList;
import android.gov.nist.javax.sip.header.ContentEncoding;
import android.gov.nist.javax.sip.header.ContentEncodingList;
import android.gov.nist.javax.sip.header.ContentLanguage;
import android.gov.nist.javax.sip.header.ContentLanguageList;
import android.gov.nist.javax.sip.header.ErrorInfo;
import android.gov.nist.javax.sip.header.ErrorInfoList;
import android.gov.nist.javax.sip.header.ExtensionHeaderImpl;
import android.gov.nist.javax.sip.header.ExtensionHeaderList;
import android.gov.nist.javax.sip.header.InReplyTo;
import android.gov.nist.javax.sip.header.InReplyToList;
import android.gov.nist.javax.sip.header.ProxyAuthenticate;
import android.gov.nist.javax.sip.header.ProxyAuthenticateList;
import android.gov.nist.javax.sip.header.ProxyAuthorization;
import android.gov.nist.javax.sip.header.ProxyAuthorizationList;
import android.gov.nist.javax.sip.header.ProxyRequire;
import android.gov.nist.javax.sip.header.ProxyRequireList;
import android.gov.nist.javax.sip.header.Reason;
import android.gov.nist.javax.sip.header.ReasonList;
import android.gov.nist.javax.sip.header.RecordRoute;
import android.gov.nist.javax.sip.header.RecordRouteList;
import android.gov.nist.javax.sip.header.Require;
import android.gov.nist.javax.sip.header.RequireList;
import android.gov.nist.javax.sip.header.Route;
import android.gov.nist.javax.sip.header.RouteList;
import android.gov.nist.javax.sip.header.SIPHeader;
import android.gov.nist.javax.sip.header.SIPHeaderList;
import android.gov.nist.javax.sip.header.Supported;
import android.gov.nist.javax.sip.header.SupportedList;
import android.gov.nist.javax.sip.header.Unsupported;
import android.gov.nist.javax.sip.header.UnsupportedList;
import android.gov.nist.javax.sip.header.Via;
import android.gov.nist.javax.sip.header.ViaList;
import android.gov.nist.javax.sip.header.WWWAuthenticate;
import android.gov.nist.javax.sip.header.WWWAuthenticateList;
import android.gov.nist.javax.sip.header.Warning;
import android.gov.nist.javax.sip.header.WarningList;
import android.gov.nist.javax.sip.header.ims.PAssertedIdentity;
import android.gov.nist.javax.sip.header.ims.PAssertedIdentityList;
import android.gov.nist.javax.sip.header.ims.PAssociatedURI;
import android.gov.nist.javax.sip.header.ims.PAssociatedURIList;
import android.gov.nist.javax.sip.header.ims.PMediaAuthorization;
import android.gov.nist.javax.sip.header.ims.PMediaAuthorizationList;
import android.gov.nist.javax.sip.header.ims.PVisitedNetworkID;
import android.gov.nist.javax.sip.header.ims.PVisitedNetworkIDList;
import android.gov.nist.javax.sip.header.ims.Path;
import android.gov.nist.javax.sip.header.ims.PathList;
import android.gov.nist.javax.sip.header.ims.Privacy;
import android.gov.nist.javax.sip.header.ims.PrivacyList;
import android.gov.nist.javax.sip.header.ims.SecurityClient;
import android.gov.nist.javax.sip.header.ims.SecurityClientList;
import android.gov.nist.javax.sip.header.ims.SecurityServer;
import android.gov.nist.javax.sip.header.ims.SecurityServerList;
import android.gov.nist.javax.sip.header.ims.SecurityVerify;
import android.gov.nist.javax.sip.header.ims.SecurityVerifyList;
import android.gov.nist.javax.sip.header.ims.ServiceRoute;
import android.gov.nist.javax.sip.header.ims.ServiceRouteList;

import java.util.HashMap;
import java.util.Map;

/**
 * A map of which of the standard headers may appear as a list
 *
 * @version 1.2 $Revision: 1.16 $ $Date: 2010-05-06 14:08:04 $
 * @since 1.1
 */
public class ListMap {
    // A table that indicates whether a header has a list representation or
    // not (to catch adding of the non-list form when a list exists.)
    // Entries in this table allow you to look up the list form of a header
    // (provided it has a list form). Note that under JAVA-5 we have
    // typed collections which would render such a list obsolete. However,
    // we are not using java 5.
    private static Map<Class<?>,Class<?>> headerListTable;

    private static boolean initialized;
    static {
        initializeListMap();
    }

    static private void initializeListMap() {
        /*
         * Build a table mapping between objects that have a list form and the
         * class of such objects.
         */
    	// jeand : using concurrent data structure to avoid excessive blocking
        headerListTable = new HashMap<Class<?>, Class<?>>(34);
        headerListTable.put(ExtensionHeaderImpl.class, ExtensionHeaderList.class);

        headerListTable.put(Contact.class, ContactList.class);

        headerListTable.put(ContentEncoding.class, ContentEncodingList.class);

        headerListTable.put(Via.class, ViaList.class);

        headerListTable.put(WWWAuthenticate.class, WWWAuthenticateList.class);

        headerListTable.put(Accept.class, AcceptList.class);

        headerListTable.put(AcceptEncoding.class, AcceptEncodingList.class);

        headerListTable.put(AcceptLanguage.class, AcceptLanguageList.class);

        headerListTable.put(ProxyRequire.class, ProxyRequireList.class);

        headerListTable.put(Route.class, RouteList.class);

        headerListTable.put(Require.class, RequireList.class);

        headerListTable.put(Warning.class, WarningList.class);

        headerListTable.put(Unsupported.class, UnsupportedList.class);

        headerListTable.put(AlertInfo.class, AlertInfoList.class);

        headerListTable.put(CallInfo.class, CallInfoList.class);

        headerListTable.put(ProxyAuthenticate.class,ProxyAuthenticateList.class);

        headerListTable.put(ProxyAuthorization.class, ProxyAuthorizationList.class);

        headerListTable.put(Authorization.class, AuthorizationList.class);

        headerListTable.put(Allow.class, AllowList.class);
        
        // http://java.net/jira/browse/JSIP-410
        headerListTable.put(AllowEvents.class, AllowEventsList.class);

        headerListTable.put(RecordRoute.class, RecordRouteList.class);

        headerListTable.put(ContentLanguage.class, ContentLanguageList.class);

        headerListTable.put(ErrorInfo.class, ErrorInfoList.class);

        headerListTable.put(Supported.class, SupportedList.class);

        headerListTable.put(InReplyTo.class,InReplyToList.class);

        // IMS headers.

        headerListTable.put(PAssociatedURI.class, PAssociatedURIList.class);

        headerListTable.put(PMediaAuthorization.class, PMediaAuthorizationList.class);

        headerListTable.put(Path.class, PathList.class);

        headerListTable.put(Privacy.class,PrivacyList.class);

        headerListTable.put(ServiceRoute.class, ServiceRouteList.class);

        headerListTable.put(PVisitedNetworkID.class, PVisitedNetworkIDList.class);

        headerListTable.put(SecurityClient.class, SecurityClientList.class);

        headerListTable.put(SecurityServer.class, SecurityServerList.class);

        headerListTable.put(SecurityVerify.class, SecurityVerifyList.class);

        headerListTable.put(PAssertedIdentity.class, PAssertedIdentityList.class);
        
        // https://java.net/jira/browse/JSIP-460
        headerListTable.put(Reason.class, ReasonList.class);

        initialized = true;

    }

    /**
     * return true if this has an associated list object.
     */
    static public boolean hasList(SIPHeader sipHeader) {
        if (sipHeader instanceof SIPHeaderList)
            return false;
        else {
            Class<?> headerClass = sipHeader.getClass();
            return headerListTable.get(headerClass) != null;
        }
    }

    /**
     * Return true if this has an associated list object.
     */
    static public boolean hasList(Class<?> sipHdrClass) {
        if (!initialized)
            initializeListMap();
        return headerListTable.get(sipHdrClass) != null;
    }
    
    /**
     * Mark a listable Header 
     */
    static public void addListHeader(Class<? extends SIPHeader> sipHeaderClass,
                                     Class<? extends SIPHeaderList<? extends SIPHeader>> sipHeaderListClass)
    {
      headerListTable.put(sipHeaderClass, sipHeaderListClass);
    }

    /**
     * Get the associated list class.
     */
    static public Class<?> getListClass(Class<?> sipHdrClass) {
        if (!initialized)
            initializeListMap();
        return (Class<?>) headerListTable.get(sipHdrClass);
    }

    /**
     * Return a list object for this header if it has an associated list object.
     */
    @SuppressWarnings("unchecked")
    static public SIPHeaderList<SIPHeader> getList(SIPHeader sipHeader) {
        if (!initialized)
            initializeListMap();
        try {
            Class<?> headerClass = sipHeader.getClass();
            Class<?> listClass =  headerListTable.get(headerClass);
            SIPHeaderList<SIPHeader> shl = (SIPHeaderList<SIPHeader>) listClass.newInstance();
            shl.setHeaderName(sipHeader.getName());
            return shl;
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}

