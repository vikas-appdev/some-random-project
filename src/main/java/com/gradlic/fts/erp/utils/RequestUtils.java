package com.gradlic.fts.erp.utils;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

public class RequestUtils {

    public static final String USER_AGENT_HEADER = "user-agent";
    public static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

    public static String getIpAddress(HttpServletRequest request){
        String ipAddress = "UNKNOWN IP";
        if (request !=null ){
            ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
            if (ipAddress == null || "".equals(ipAddress)){
                ipAddress = request.getRemoteAddr();
            }
        }
        return ipAddress;
    }

    public static String getDevice(HttpServletRequest request){
        UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer.newBuilder()
                .hideMatcherLoadStats()
                .withCache(1000)
                .build();

        UserAgent userAgent = userAgentAnalyzer.parse(request.getHeader(USER_AGENT_HEADER));

        return userAgent.getValue(UserAgent.OPERATING_SYSTEM_NAME)+" - "+ userAgent.getValue(UserAgent.AGENT_NAME) + " - " +userAgent.getValue(UserAgent.DEVICE_NAME);
    }
}
