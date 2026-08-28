package com.digitusforum.firewall.training;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallTrainingController {
	// LoginMicroserviceDEPRECATED loginMicroservice = new
	// LoginMicroserviceDEPRECATED(new RequestService());
	Map<String, FirewallTrainingVO> cache = new HashMap<>();
	FirewallTrainingService trainingService = new FirewallTrainingService(new RequestService());
	@Autowired
	FirewallLoginService firewallLoginService = new FirewallLoginService();

	@CrossOrigin
	@PostMapping(value = "/firewall/training/v1/create")
	public FirewallTrainingVO create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody FirewallTrainingVO trainingVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		trainingVO.setUserId(tokenVO.getUserId());
		return trainingService.create(trainingVO, locale);
	}

	@CrossOrigin
	@GetMapping(value = "/firewall/training/v1/retrieveAll")
	public List<FirewallTrainingVO> retrieve(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return trainingService.retrieveAll(locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/training/v1/retrieveById")
	public FirewallTrainingVO retrieve(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody FirewallTrainingVO trainingVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return trainingService.retrieveById(trainingVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/training/v1/retrieveSubjectsByTrainingId")
	public FirewallTrainingVO retrieveSubjectsByTrainingId(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody FirewallTrainingVO trainingVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		trainingVO.setUserId(tokenVO.getUserId());
		String cacheKey = "retrieveSubjectsByTrainingId_trainingId_" + trainingVO.getTrainingId() + "_user_" + tokenVO.getUserId();
		if(!cache.containsKey(cacheKey)){
			cache.put(cacheKey, trainingService.retrieveSubjectsByTrainingId(trainingVO, locale));
		}
		return cache.get(cacheKey);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/training/v1/delete")
	public FirewallTrainingVO delete(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody FirewallTrainingVO trainingVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return trainingService.delete(trainingVO, locale);
	}

}
