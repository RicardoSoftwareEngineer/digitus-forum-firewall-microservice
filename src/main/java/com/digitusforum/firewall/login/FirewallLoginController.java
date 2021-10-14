package com.digitusforum.firewall.login;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallLoginController {
	RequestService requestService = new RequestService();

	@CrossOrigin
	@RequestMapping(value = "/firewall/login/v1/createToken")
	public TokenVO createToken(@RequestHeader(defaultValue = "en_us") String locale, @RequestBody TokenVO tokenVO) {
		return requestService.createToken(tokenVO, locale);
	}

	@CrossOrigin
	@RequestMapping(value = "/firewall/login/v1/validateToken")
	public TokenVO validateToken(@RequestHeader(defaultValue = "en_us") String locale, @RequestBody TokenVO tokenVO) {
		return requestService.validateToken(tokenVO, locale);
	}
}