package com.gradlic.fts.erp.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsUtils {
    public static final String FROM_NUMBER = "";
    public static final String SID_KEY = "";
    public static final String TOKEN_KEY = "";

    public static void sendSMS(String to, String messageBody){
        System.out.println(to);
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = com.twilio.rest.api.v2010.account.Message.creator(
                        new PhoneNumber("+91"+to),
                        new PhoneNumber(FROM_NUMBER),
                        messageBody)
                .create();
        System.out.println(message);
    }
}
