package com.gradlic.fts.erp.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsUtils {
    public static final String FROM_NUMBER = "+17178424458";
    public static final String SID_KEY = "ACaed09f77140052e615b22a0b75d07067";
    public static final String TOKEN_KEY = "4f939ac17acfe1f3244676c93ff204a5";

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
