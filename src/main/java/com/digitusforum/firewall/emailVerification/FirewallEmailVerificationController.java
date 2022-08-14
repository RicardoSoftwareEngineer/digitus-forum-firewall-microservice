package com.digitusforum.firewall.emailVerification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


@RestController
public class FirewallEmailVerificationController {
	@Autowired
	FirewallEmailVerificationService firewallVerificationService;
	
	@Autowired
	FirewallLoginService firewallLoginService;

	@CrossOrigin
	@RequestMapping(value = "/firewall/emailVerification/v1/sendValidationEmail")
	public FirewallEmailVerificationVO sendValidationEmail(@RequestHeader(defaultValue = "en_us") String locale, 
			@RequestBody FirewallEmailVerificationVO firewallEmailVerificationVO) {
		return firewallVerificationService.sendValidationEmail(firewallEmailVerificationVO, locale);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/firewall/emailVerification/v1/validateEmail")
	public TokenVO validateEmail(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestBody FirewallEmailVerificationVO firewallEmailVerificationVO) throws JsonMappingException, JsonProcessingException {
		firewallVerificationService.validateEmail(firewallEmailVerificationVO, locale);
		return firewallLoginService.createToken(firewallEmailVerificationVO, locale);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/firewall/emailVerification/v1/sendResetPasswordEmail")
	public FirewallEmailVerificationVO sendResetPasswordEmail(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestBody FirewallEmailVerificationVO firewallEmailVerificationVO) {
		return firewallVerificationService.sendResetPasswordEmail(firewallEmailVerificationVO, locale);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/firewall/emailVerification/v1/resetPassword")
	public TokenVO resetPassword(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestBody FirewallEmailVerificationVO firewallEmailVerificationVO) throws JsonMappingException, JsonProcessingException {
			
		firewallVerificationService.resetPassword(firewallEmailVerificationVO, locale);
		return firewallLoginService.createToken(firewallEmailVerificationVO, locale);
	}

}
