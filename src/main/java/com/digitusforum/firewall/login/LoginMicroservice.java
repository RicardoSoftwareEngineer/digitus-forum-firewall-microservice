package com.digitusforum.firewall.login;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import i18.M;
import request.Headers;
import request.MicroservicesURLs;
import request.RequestService;
import request.Timeouts;
import user.UserVO;
import util.ThrowService;

//todo pllleaase make this in kotlin, we are flying so high, so fast and so sophisticated that i would say simple
public class LoginMicroservice {
	private RequestService requestService;

	public LoginMicroservice(RequestService requestService) {
		this.requestService = requestService;
	}

	public UserVO loginWithEmailAndPassword(UserVO userVO, String locale) {
		if (StringUtils.isBlank(userVO.getEmail()))
			throw ThrowService.doIt(locale, 403, M.LOGIN_MISSING_EMAIL);
		if (StringUtils.isBlank(userVO.getPassword()))
			throw ThrowService.doIt(locale, 403, M.LOGIN_MISSING_PASSWORD);
		if (!requestService.isUp(MicroservicesURLs.LOGIN))
			throw ThrowService.doIt(locale, 503, M.LOGIN_MICROSERVICE_OFFLINE);

		ResponseEntity<? extends UserVO> response = null;
		try {
			response = (ResponseEntity<? extends UserVO>) requestService.hitIt(
					MicroservicesURLs.LOGIN_BY_EMAIL_AND_PASSWORD, Timeouts.ideal, userVO, Headers.DEFAULT(locale));
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
		}
		return response.getBody();
	}

	public UserVO validateToken(String token, String locale) {
		if (StringUtils.isBlank(token))
			throw ThrowService.doIt(locale, 403, M.LOGIN_MISSING_TOKEN);
		if (!requestService.isUp(MicroservicesURLs.LOGIN))
			throw ThrowService.doIt(locale, 503, M.LOGIN_MICROSERVICE_OFFLINE);

		UserVO userVO = new UserVO();
		userVO.setToken(token);
		ResponseEntity<? extends UserVO> response = null;
		try {
			response = (ResponseEntity<? extends UserVO>) requestService.hitIt(MicroservicesURLs.LOGIN_VALIDATE_TOKEN,
					Timeouts.ideal, userVO, Headers.DEFAULT(locale));
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 400)
				throw ThrowService.doIt(locale, 400, M.LOGIN_INVALID_TOKEN);
			if (e.getRawStatusCode() == 403)
				throw ThrowService.doIt(locale, 403, M.LOGIN_EXPIRED_TOKEN);
		}
		return response.getBody();
	}
}
