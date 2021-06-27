package com.digitusforum.firewall.endpoint;

import microservice.I18Microservice;

import java.util.Optional;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.TimeService;
import vo.InternationalizationVO;


//todo make this guy in kotlin

@RestController
public class InternationalizationController {
    I18Microservice i18Microservice = new I18Microservice();

    @RequestMapping(value = "/i18")
    public Object internationalization(@RequestBody Optional<InternationalizationVO> i18) {
        return i18Microservice.getMessageByKey(i18.get());
    }
}
