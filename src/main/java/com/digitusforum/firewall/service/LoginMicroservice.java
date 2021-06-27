package com.digitusforum.firewall.service;

import io.netty.util.Timeout;
import model.Microservices;
import model.Headers;
import model.Locales;
import model.M;
import model.Timeouts;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import service.RequestService;
import service.ThrowService;
import vo.InternationalizationVO;
import vo.TokenVO;
import vo.UserVO;

import java.util.HashMap;
import java.util.Map;

//todo pllleaase make this in kotlin, we are flying so high, so fast and so sophisticated that i would say simple
public class LoginMicroservice {
    private String LOGIN_BY_EMAIL_AND_PASSWORD_URL = Microservices.LOGIN + "/v1/loginByEmailAndPassword";

    public TokenVO loginWithEmailAndPassword(UserVO userVO, MultiValueMap<String, String> headers){
    	if (!RequestService.isUp(Microservices.LOGIN)){
    		throw ThrowService.doIt(headers.getFirst("locale"), 503, M.LOGIN_MICROSERVICE_OFFLINE);
    	}
    	
    	
    	
        //return null; //(TokenVO) RequestService.hit(LOGIN_BY_EMAIL_AND_PASSWORD_URL, userVO, Timeouts.ideal, new TokenVO());
    	UserVO user = ((ResponseEntity<? extends UserVO>) RequestService.hitThemUp(LOGIN_BY_EMAIL_AND_PASSWORD_URL, Timeouts.ideal, userVO, headers)).getBody();
    	TokenVO token = new TokenVO();
    	//token.setName(resp.getName());
    	return token;
    }
}
