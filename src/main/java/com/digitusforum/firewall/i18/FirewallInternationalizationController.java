package com.digitusforum.firewall.i18;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.FirewallLoginService;

//todo make this guy in kotlin

@RestController
public class FirewallInternationalizationController {
	I18Microservice i18Microservice = new I18Microservice(new RequestServiceDEPRECATED());

	@Autowired
	FirewallLoginService firewallLoginService;

	@PostMapping(value = "/firewall/internationalization/v1/i18")
	public Object internationalization(@RequestBody Optional<I18VO> i18) {
		return i18Microservice.getMessageByKey(i18.get());
	}

	@PostMapping(value = "/firewall/internationalization/v1/deleteCache")
	public void deleteCache(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		firewallLoginService.validateToken(authorization, locale);
		i18Microservice.deleteCache();
	}
}
