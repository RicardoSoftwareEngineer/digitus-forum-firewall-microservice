package com.digitusforum.firewall.endpoint;

import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import microservice.I18Microservice;
import service.RequestService;
import vo.InternationalizationVO;

//todo make this guy in kotlin

@RestController
public class FirewallInternationalizationController {
	I18Microservice i18Microservice = new I18Microservice(new RequestService());

	@PostMapping(value = "/firewall/internationalization/v1/i18")
	public Object internationalization(@RequestBody Optional<InternationalizationVO> i18) {
		return i18Microservice.getMessageByKey(i18.get());
	}
	
	@PostMapping(value = "/firewall/internationalization/v1/deleteCache")
	public void deleteCache() {
		i18Microservice.deleteCache();
	}
}
