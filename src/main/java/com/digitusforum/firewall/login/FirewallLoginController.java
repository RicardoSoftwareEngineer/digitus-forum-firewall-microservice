package com.digitusforum.firewall.login;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import request.RequestService;
import user.UserVO;

@CrossOrigin(origins = "*")
@RestController
public class FirewallLoginController {
	LoginMicroservice loginMicroservice = new LoginMicroservice(new RequestService());

	@PostMapping(value = "/firewall/login/v1/loginByEmailAndPassword")
	public Object loginByEmailAndPassword(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestBody UserVO userVO) {
		return loginMicroservice.loginWithEmailAndPassword(userVO, locale);
	}

	@RequestMapping(value = "/firewall/login/v1/validateToken")
	public Object validateToken(@RequestHeader(defaultValue = "en_us") String locale, @RequestBody UserVO userVO) {
		return loginMicroservice.validateToken(userVO.getToken(), locale);
	}
}
