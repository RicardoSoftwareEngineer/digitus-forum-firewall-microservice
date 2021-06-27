package com.digitusforum.firewall.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import model.Headers;
import model.M;
import model.Microservices;
import model.Timeouts;
import service.RequestService;
import service.ThrowService;
import vo.UserVO;

//todo pllleaase make this in kotlin, we are flying so high, so fast and so sophisticated that i would say simple
public class LoginMicroservice {
	private String LOGIN_BY_EMAIL_AND_PASSWORD_URL = Microservices.LOGIN + "/v1/loginByEmailAndPassword";
	private String VALIDATE_TOKEN = Microservices.LOGIN + "/v1/validateToken";

	public UserVO loginWithEmailAndPassword(UserVO userVO, String locale) {
		if (StringUtils.isBlank(userVO.getEmail()))
			throw ThrowService.doIt(locale, 403, M.LOGIN_MISSING_EMAIL);
		if (StringUtils.isBlank(userVO.getPassword()))
			throw ThrowService.doIt(locale, 403, M.LOGIN_MISSING_PASSWORD);
		if (!RequestService.isUp(Microservices.LOGIN))
			throw ThrowService.doIt(locale, 503, M.LOGIN_MICROSERVICE_OFFLINE);

		ResponseEntity<? extends UserVO> response = null;
		try {
			response = (ResponseEntity<? extends UserVO>) RequestService.hitThemUp(LOGIN_BY_EMAIL_AND_PASSWORD_URL,
					Timeouts.ideal, userVO, Headers.DEFAULT(locale));
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
		}
		return response.getBody();
	}

	public UserVO validateToken(String token, String locale) {
		if (StringUtils.isBlank(token))
			throw ThrowService.doIt(locale, 403, M.LOGIN_MISSING_TOKEN);
		if (!RequestService.isUp(Microservices.LOGIN))
			throw ThrowService.doIt(locale, 503, M.LOGIN_MICROSERVICE_OFFLINE);

		UserVO userVO = new UserVO();
		userVO.setToken(token);
		ResponseEntity<? extends UserVO> response = null;
		try {
			response = (ResponseEntity<? extends UserVO>) RequestService.hitThemUp(VALIDATE_TOKEN, Timeouts.ideal,
					userVO, Headers.DEFAULT(locale));
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 400)
				throw ThrowService.doIt(locale, 400, M.LOGIN_INVALID_TOKEN);
			if (e.getRawStatusCode() == 403)
				throw ThrowService.doIt(locale, 403, M.LOGIN_EXPIRED_TOKEN);
		}
		return response.getBody();
	}
}
