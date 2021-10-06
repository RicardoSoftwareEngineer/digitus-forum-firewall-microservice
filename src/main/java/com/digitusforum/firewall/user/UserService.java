package com.digitusforum.firewall.user;

import org.springframework.web.client.HttpClientErrorException;

import com.digitusforum.firewall.util.RequestService;
import com.google.gson.Gson;

import i18.M;
import request.MicroservicesURLs;
import user.UserVO;
import util.ThrowService;

//todo pllleaase make this in kotlin, we are flying so high, so fast and so sophisticated that i would say simple
public class UserService {
	private RequestService requestService;

	public UserService(RequestService requestService) {
		this.requestService = requestService;
	}

	private void checkUserMS(String locale) {
		if (!requestService.isUp(MicroservicesURLs.USER))
			throw ThrowService.doIt(locale, 503, M.USER_MICROSERVICE_OFFLINE);
	}

	public UserVO createUser(UserVO userVO, String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.createUser(MicroservicesURLs.USER_CREATE, userVO, locale);
		return new Gson().fromJson(jsonResponse, UserVO.class);
	}

	public UserVO[] retrieveUsers(String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.request(MicroservicesURLs.USER_RETRIEVE_USERS, locale);
		return new Gson().fromJson(jsonResponse, UserVO[].class);
	}

	public UserVO retrieveUserById(String locale, int id) {
		checkUserMS(locale);
		String jsonResponse = null;
		try {
			String url = String.format(MicroservicesURLs.USER_RETRIEVE_BY_ID, id);
			jsonResponse = requestService.request(url, locale);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
		}
		return new Gson().fromJson(jsonResponse, UserVO.class);
	}

	public UserVO updateUser(String locale, int id, UserVO userVO) {
		checkUserMS(locale);
		String jsonResponse = null;
		try {
			String url = String.format(MicroservicesURLs.USER_UPDATE, id);
			jsonResponse = requestService.request(url, userVO, locale);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
			if (e.getRawStatusCode() == 403)
				throw ThrowService.doIt(locale, 403, M.USER_EMAIL_ALREADY_IN_USE);
		}
		return new Gson().fromJson(jsonResponse, UserVO.class);
	}

	public UserVO deleteUser(String locale, int id) {
		checkUserMS(locale);
		String jsonResponse = null;
		try {
			String url = String.format(MicroservicesURLs.USER_DELETE, id);
			jsonResponse = requestService.request(url, locale);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
		}
		return new Gson().fromJson(jsonResponse, UserVO.class);
	}

	public UserVO retrieveByEmailAndPassword(UserVO userVO, String locale) {
		checkUserMS(locale);
		String jsonResponse = null;
		try {
			String url = MicroservicesURLs.USER_RETRIEVE_BY_EMAIL_AND_PASSWORD;
			jsonResponse = requestService.request(url, userVO, locale);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
		}
		return new Gson().fromJson(jsonResponse, UserVO.class);
	}
}
