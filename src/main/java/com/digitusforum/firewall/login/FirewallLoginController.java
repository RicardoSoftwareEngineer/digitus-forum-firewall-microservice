package com.digitusforum.firewall.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class FirewallLoginController {
	@Autowired
	FirewallLoginService firewallLoginService;

	@CrossOrigin
	@RequestMapping(value = "/firewall/login/v1/createToken")
	public TokenVO createToken(@RequestHeader(defaultValue = "en_us") String locale, @RequestBody TokenVO tokenVO) throws JsonMappingException, JsonProcessingException {
		return firewallLoginService.createToken(tokenVO, locale);
	}

}