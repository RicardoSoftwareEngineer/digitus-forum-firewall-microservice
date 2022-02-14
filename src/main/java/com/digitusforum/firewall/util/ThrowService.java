package com.digitusforum.firewall.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.firewall.i18.I18Microservice;
import com.digitusforum.firewall.i18.RequestServiceDEPRECATED;


public class ThrowService {
    private static I18Microservice i18City = new I18Microservice(new RequestServiceDEPRECATED());

    public static ResponseStatusException doIt(String locale, int httpStatus, String key){
        String message = i18City.getMessageByKey(locale, key);
        return new ResponseStatusException(HttpStatus.valueOf(httpStatus), message);
    }

    public static ResponseStatusException doIt(int httpStatus, String key){
        return doIt(Locales.DEFAULT, httpStatus, key);
    }
}
