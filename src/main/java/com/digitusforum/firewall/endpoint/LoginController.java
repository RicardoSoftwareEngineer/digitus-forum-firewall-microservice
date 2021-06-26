package com.digitusforum.firewall.endpoint;

import com.digitusforum.firewall.service.UserService;
import microservice.LoginMicroservice;
import model.Microservices;
import model.Headers;
import model.Locales;
import model.M;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import service.RequestService;
import service.ThrowService;
import vo.TokenVO;
import vo.UserVO;

import java.util.HashMap;
import java.util.Map;

//@CrossOrigin(origins = "*")
@RestController
public class LoginController {
	@Autowired
    UserService userService;
	//UserService userService = new UserService();
    Map<String, TokenVO> tokenCache = new HashMap<>();

    @RequestMapping(value = "/login/by/emailAndPassword")
    public Object loginByEmailAndPassword(@RequestHeader(defaultValue = "en_us") String locale, @RequestBody UserVO userVO) {
    	if(tokenCache.containsKey(userVO.getEmail()+userVO.getPassword())){
            new Thread(() -> {
                TokenVO token = new LoginMicroservice().loginWithEmailAndPassword(userVO, Headers.DEFAULT(locale.toString()));
                tokenCache.put(userVO.getEmail()+userVO.getPassword(), token);
            }).start();
        }else if (RequestService.isUp(Microservices.LOGIN)){
            TokenVO token = new LoginMicroservice().loginWithEmailAndPassword(userVO, Headers.DEFAULT(locale.toString()));
            tokenCache.put(userVO.getEmail()+userVO.getPassword(), token);
        }else{
            throw ThrowService.doIt(locale, 503, M.LOGIN_MICROSERVICE_OFFLINE);
        }
        return tokenCache.get(userVO.getEmail()+userVO.getPassword());
    }

    private void updateCache(){

    }
}
