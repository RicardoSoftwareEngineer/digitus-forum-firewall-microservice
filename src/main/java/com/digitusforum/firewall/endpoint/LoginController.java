package com.digitusforum.firewall.endpoint;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.service.LoginMicroservice;

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

	@PostMapping(value = "/firewall/v1/login/byEmailAndPassword")
	public Object loginByEmailAndPassword(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestBody UserVO userVO) {
		if (!RequestService.isUp(Microservices.LOGIN)) {
			throw ThrowService.doIt(locale, 503, M.LOGIN_MICROSERVICE_OFFLINE);
		}
		TokenVO token = new LoginMicroservice().loginWithEmailAndPassword(userVO, Headers.DEFAULT(locale.toString()));
		return token;
	}
}
