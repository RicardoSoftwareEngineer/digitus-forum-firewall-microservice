package com.digitusforum.firewall.subject;

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
public class FirewallSubjectController {
	@Autowired
	SubjectRequestService subjectRequestService;
	@Autowired
	FirewallLoginService firewallLoginService;

	// $$$$$$$$$$$$$$$$$$$$ Public methods $$$$$$$$$$$$$$$$$$$$

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/retrieveByVideo")
	public List<SubjectVO> retrieveByVideo(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody SubjectVO subjectVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return subjectRequestService.retrieveByVideo(subjectVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/retrieveByCourseId")
	public List<SubjectVO> retrieveById(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody SubjectVO subjectVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		subjectVO.setUserId(tokenVO.getUserId());
		return subjectRequestService.retrieveByCourseId(subjectVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/retrieveByIdWithVideos")
	public SubjectVO retrieveByCourseId(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody SubjectVO subjectVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		subjectVO.setUserId(tokenVO.getUserId());
		return subjectRequestService.retrieveByIdWithVideos(subjectVO, locale);
	}

	// $$$$$$$$$$$$$$$$$$$$ Only the owner can execute $$$$$$$$$$$$$$$$$$$$

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/create")
	public SubjectVO create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody SubjectVO subjectVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		subjectVO.setUserId(tokenVO.getUserId());
		return subjectRequestService.create(subjectVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/update")
	public SubjectVO update(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody SubjectVO subjectVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		subjectVO.setUserId(tokenVO.getUserId());
		return subjectRequestService.update(subjectVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/addVideo")
	public SubjectVO addVideo(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody SubjectVO subjectVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		subjectVO.setUserId(tokenVO.getUserId());
		return subjectRequestService.addVideo(subjectVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/removeVideo")
	public SubjectVO removeVideo(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody SubjectVO subjectVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		subjectVO.setUserId(tokenVO.getUserId());
		return subjectRequestService.removeVideo(subjectVO, locale);
	}

}

// TODO transferir o http://localhost:8080/firewall/course/v1/retrieveSubjectsByCourseId pra ca