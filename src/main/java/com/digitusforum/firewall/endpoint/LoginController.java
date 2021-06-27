package com.digitusforum.firewall.endpoint;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.service.UserService;

import microservice.LoginMicroservice;
import model.Headers;
import model.M;
import model.Microservices;
import service.RequestService;
import service.ThrowService;
import vo.TokenVO;
import vo.UserVO;

@CrossOrigin(origins = "*")
@RestController
public class LoginController {
	@Autowired
    UserService userService;
	//UserService userService = new UserService();
    //Map<String, TokenVO> tokenCache = new HashMap<>(); //lets keep this to the future

    @PostMapping(value = "/firewall/v1/login/byEmailAndPassword")
    public Object loginByEmailAndPassword(@RequestHeader(defaultValue = "en_us") String locale, @RequestBody UserVO userVO) {
    	 if (!RequestService.isUp(Microservices.LOGIN)){
    		 throw ThrowService.doIt(locale, 503, M.LOGIN_MICROSERVICE_OFFLINE);   
        }
    	 TokenVO token = new LoginMicroservice().loginWithEmailAndPassword(userVO, Headers.DEFAULT(locale.toString()));
        return token;
    }
}
