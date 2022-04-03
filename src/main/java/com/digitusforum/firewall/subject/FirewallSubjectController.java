package com.digitusforum.firewall.subject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallSubjectController {
	@Autowired
	SubjectRequestService subjectRequestService;
	@Autowired
	RequestService requestService;

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/create")
	public SubjectVO create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody SubjectVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return subjectRequestService.create(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/retrieveByPerfilId")
	public List<SubjectVO> retrieveById(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody SubjectVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return subjectRequestService.retrieveByPerfilId(moduleVO, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/retrieveByIdWithVideos")
	public SubjectVO retrieveByCourseId(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody SubjectVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return subjectRequestService.retrieveByIdWithVideos(moduleVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/subject/v1/update")
	public SubjectVO update(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody SubjectVO moduleVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return subjectRequestService.update(moduleVO, locale);
	}

}


// TODO transferir o http://localhost:8080/firewall/course/v1/retrieveSubjectsByCourseId pra ca