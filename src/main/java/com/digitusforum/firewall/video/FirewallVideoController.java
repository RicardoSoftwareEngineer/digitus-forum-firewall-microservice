package com.digitusforum.firewall.video;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.course.FirewallCourseVO;
import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallVideoController {
	@Autowired
	VideoRequestService videoRequestService;
	@Autowired
	FirewallLoginService firewallLoginService;

	
	//TODO continuar criando esses caras no postman
	@CrossOrigin
	@PostMapping(value = "/firewall/video/v1/create")
	public VideoVO create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody VideoVO videoVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		videoVO.setUserId(tokenVO.getUserId());
		return videoRequestService.create(videoVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/video/v1/retrieveById")
	public VideoVO retrieveById(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody VideoVO videoVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return videoRequestService.retrieveById(videoVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/video/v1/retrieveBySubjectId")
	public List<VideoVO> retrieveByCourseId(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody VideoVO videoVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return videoRequestService.retrieveBySubjectId(videoVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/video/v1/update")
	public VideoVO update(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody VideoVO videoVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		videoVO.setUserId(tokenVO.getUserId());
		return videoRequestService.update(videoVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/video/v1/delete")
	public VideoVO delete(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody VideoVO videoVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		videoVO.setUserId(tokenVO.getUserId());
		return videoRequestService.delete(videoVO, locale);
	}

}
