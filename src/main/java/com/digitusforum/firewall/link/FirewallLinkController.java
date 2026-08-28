package com.digitusforum.firewall.link;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallLinkController {
	@Autowired
	LinkRequestService linkRequestService;
	@Autowired
	FirewallLoginService firewallLoginService;

	@CrossOrigin
	@PostMapping(value = "/firewall/link/v1/create")
	public LinkVO create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody LinkVO linkVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		linkVO.setUserId(tokenVO.getUserId());
		return linkRequestService.create(linkVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/link/v1/retrieveByVideoId")
	public List<LinkVO> retrieveByvIDEOId(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody LinkVO linkVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return linkRequestService.retrieveByVideoId(linkVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/link/v1/update")
	public LinkVO update(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody LinkVO linkVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return linkRequestService.update(linkVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/link/v1/delete")
	public LinkVO delete(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody LinkVO linkVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return linkRequestService.delete(linkVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/link/v1/reorder")
	public LinkVO reorder(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody LinkVO linkVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return linkRequestService.reorder(linkVO, locale);
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
