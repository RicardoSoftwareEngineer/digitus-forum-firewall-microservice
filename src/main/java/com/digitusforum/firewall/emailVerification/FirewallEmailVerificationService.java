package com.digitusforum.firewall.emailVerification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digitusforum.firewall.i18.I18Microservice;
import com.digitusforum.firewall.i18.RequestServiceDEPRECATED;
import com.digitusforum.firewall.util.MicroservicesURLs;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.util.ThrowService;
import com.google.gson.Gson;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FirewallEmailVerificationService {

	@Autowired
	private RequestService requestService;
	private static I18Microservice i18City = new I18Microservice(new RequestServiceDEPRECATED());

	// TODO test without this
	public FirewallEmailVerificationService(RequestService requestService) {
		this.requestService = requestService;
	}

	private void checkUserMS(String locale) {
		if (!requestService.isUp(MicroservicesURLs.USER))
			throw ThrowService.doIt(locale, 503, M.USER_MICROSERVICE_OFFLINE);
	}

	public FirewallEmailVerificationVO sendValidationEmail(FirewallEmailVerificationVO firewallEmailVerificationVO, String locale) {
		if (StringUtils.isBlank(firewallEmailVerificationVO.getRecaptchaToken()))
			throw ThrowService.doIt(locale, 403, M.MISSING_CAPTCHA);

		if(!requestService.captchaIsValid(firewallEmailVerificationVO.getRecaptchaToken()))
			throw ThrowService.doIt(locale, 403, M.INVALID_CAPTCHA);

		System.out.println("valid captcha");

		checkUserMS(locale);
		String jsonResponse = requestService.request(FirewallEmailVerificationURLs.SEND_VALIDATION_EMAIL, firewallEmailVerificationVO, locale);
		firewallEmailVerificationVO = new Gson().fromJson(jsonResponse, FirewallEmailVerificationVO.class);
		String message = i18City.getMessageByKey(locale, firewallEmailVerificationVO.getResponse());
		firewallEmailVerificationVO.setResponse(message);

		return firewallEmailVerificationVO;
	}

	public FirewallEmailVerificationVO validateEmail(FirewallEmailVerificationVO firewallEmailVerificationVO,
			String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.request(FirewallEmailVerificationURLs.VALIDATE_EMAIL, firewallEmailVerificationVO, locale);
		firewallEmailVerificationVO = new Gson().fromJson(jsonResponse, FirewallEmailVerificationVO.class);
		String message = i18City.getMessageByKey(locale, firewallEmailVerificationVO.getResponse());
		firewallEmailVerificationVO.setResponse(message);
		return firewallEmailVerificationVO;
	}

	public FirewallEmailVerificationVO sendResetPasswordEmail(FirewallEmailVerificationVO firewallEmailVerificationVO,
			String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.request(FirewallEmailVerificationURLs.SEND_RESET_PASSWORD_EMAIL, firewallEmailVerificationVO, locale);
		firewallEmailVerificationVO = new Gson().fromJson(jsonResponse, FirewallEmailVerificationVO.class);
		String message = i18City.getMessageByKey(locale, firewallEmailVerificationVO.getResponse());
		firewallEmailVerificationVO.setResponse(message);
		return firewallEmailVerificationVO;
	}

	public FirewallEmailVerificationVO resetPassword(FirewallEmailVerificationVO firewallEmailVerificationVO,
			String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.request(FirewallEmailVerificationURLs.RESET_PASSWORD, firewallEmailVerificationVO, locale);
		firewallEmailVerificationVO = new Gson().fromJson(jsonResponse, FirewallEmailVerificationVO.class);
		String message = i18City.getMessageByKey(locale, firewallEmailVerificationVO.getResponse());
		firewallEmailVerificationVO.setResponse(message);
		return firewallEmailVerificationVO;
	}

}
