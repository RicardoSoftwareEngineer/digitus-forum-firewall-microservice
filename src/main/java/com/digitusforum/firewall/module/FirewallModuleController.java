package com.digitusforum.firewall.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.billing.PaidAccessService;
import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;

@RestController
public class FirewallModuleController {
	@Autowired
	ModuleRequestService moduleRequestService;
	@Autowired
	FirewallLoginService firewallLoginService;
	@Autowired
	PaidAccessService paidAccessService;

	Map<String, List<ModuleVO>> cache = new HashMap<String, List<ModuleVO>>();

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/create")
	public ModuleVO create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		moduleVO.setUserId(tokenVO.getUserId());
		return moduleRequestService.create(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/retrieveById")
	public ModuleVO retrieveById(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		moduleVO.setUserId(tokenVO.getUserId());
		return moduleRequestService.retrieveById(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/retrieveByTrainingId")
	public List<ModuleVO> retrieveByTrainingId(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		moduleVO.setUserId(tokenVO.getUserId());
		return moduleRequestService.retrieveByTrainingId(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/retrieveByTrainingIdWithVideos")
	public List<ModuleVO> retrieveModulesWithVideosByTrainingId(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader(required = false) String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = paidAccessService.requireReadableTraining(moduleVO.getTrainingId(), authorization, locale);
		String userPart = "public";
		if (tokenVO != null) {
			moduleVO.setUserId(tokenVO.getUserId());
			userPart = tokenVO.getUserId();
		}
		String cacheKey = "retrieveByTrainingIdWithVideos_trainingId_" + moduleVO.getTrainingId() + "_user_" + userPart;
		if(!cache.containsKey(cacheKey)){
			cache.put(cacheKey, moduleRequestService.retrieveByTrainingIdWithVideos(moduleVO, locale));
		}
		return cache.get(cacheKey);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/update")
	public ModuleVO update(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		moduleVO.setUserId(tokenVO.getUserId());
		return moduleRequestService.update(moduleVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/delete")
	public ModuleVO delete(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		moduleVO.setUserId(tokenVO.getUserId());
		return moduleRequestService.delete(moduleVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/reorder")
	public List<ModuleVO> reorder(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		moduleVO.setUserId(tokenVO.getUserId());
		return moduleRequestService.reorder(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/addVideo")
	public ModuleVideoVO addVideo(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody ModuleVideoVO moduleVideoVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		moduleVideoVO.setUserId(tokenVO.getUserId());
		return moduleRequestService.addVideo(moduleVideoVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/module/v1/removeVideo")
	public ModuleVO removeVideo(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody ModuleVO moduleVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		moduleVO.setUserId(tokenVO.getUserId());
		return moduleRequestService.removeVideo(moduleVO, locale);
	}

	/*
	 * @CrossOrigin
	 * 
	 * @GetMapping(value = "/firewall/training/v1/retrieveAll") public List<TrainingVO>
	 * retrieve(@RequestHeader(defaultValue = "en_us") String locale,
	 * 
	 * @RequestHeader String authorization) { TokenVO tokenVO =
	 * requestService.validateToken(authorization, locale); return
	 * trainingService.retrieveAll(locale); }
	 * 
	 * @CrossOrigin
	 * 
	 * @GetMapping(value = "/firewall/training/v1/retrieveById") public TrainingVO
	 * retrieve(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader
	 * String authorization,
	 * 
	 * @RequestBody TrainingVO trainingVO) { TokenVO tokenVO =
	 * requestService.validateToken(authorization, locale); return
	 * trainingService.retrieveById(trainingVO, locale); }
	 * 
	 * @CrossOrigin
	 * 
	 * @GetMapping(value =
	 * "/firewall/training/v1/retrieveModulesWithVideosByTrainingId") public
	 * List<ModuleVO>
	 * retrieveModulesWithVideosByTrainingId(@RequestHeader(defaultValue = "en_us")
	 * String locale,
	 * 
	 * @RequestHeader String authorization, @RequestBody TrainingVO trainingVO) {
	 * TokenVO tokenVO = requestService.validateToken(authorization, locale); return
	 * trainingService.retrieveModulesWithVideosByTrainingId(trainingVO, locale); }
	 */

}
