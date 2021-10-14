package com.digitusforum.firewall.i18;

import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import i18.I18Microservice;
import i18.I18VO;
import request.RequestServiceDEPRECATED;

//todo make this guy in kotlin

@RestController
public class FirewallInternationalizationController {
	I18Microservice i18Microservice = new I18Microservice(new RequestServiceDEPRECATED());

	@PostMapping(value = "/firewall/internationalization/v1/i18")
	public Object internationalization(@RequestBody Optional<I18VO> i18) {
		return i18Microservice.getMessageByKey(i18.get());
	}
	
	@PostMapping(value = "/firewall/internationalization/v1/deleteCache")
	public void deleteCache() {
		i18Microservice.deleteCache();
	}
}
