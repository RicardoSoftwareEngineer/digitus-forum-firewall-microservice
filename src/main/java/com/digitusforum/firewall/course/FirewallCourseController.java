package com.digitusforum.firewall.course;

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
public class FirewallCourseController {
	// LoginMicroserviceDEPRECATED loginMicroservice = new
	// LoginMicroserviceDEPRECATED(new RequestService());
	Map<String, FirewallCourseVO> cache = new HashMap<>();
	FirewallCourseService courseService = new FirewallCourseService(new RequestService());
	@Autowired
	FirewallLoginService firewallLoginService = new FirewallLoginService();

	@CrossOrigin
	@PostMapping(value = "/firewall/course/v1/create")
	public FirewallCourseVO create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody FirewallCourseVO courseVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		courseVO.setUserId(tokenVO.getUserId());
		return courseService.create(courseVO, locale);
	}

	@CrossOrigin
	@GetMapping(value = "/firewall/course/v1/retrieveAll")
	public List<FirewallCourseVO> retrieve(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return courseService.retrieveAll(locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/course/v1/retrieveById")
	public FirewallCourseVO retrieve(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody FirewallCourseVO courseVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return courseService.retrieveById(courseVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/course/v1/retrieveSubjectsByCourseId")
	public FirewallCourseVO retrieveSubjectsByCourseId(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody FirewallCourseVO courseVO) {
		String cacheKey = "retrieveSubjectsByCourseId_courseId_" + courseVO.getCourseId();
		if(!cache.containsKey(cacheKey)){
			cache.put(cacheKey, courseService.retrieveSubjectsByCourseId(courseVO, locale));
		}
		//TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return cache.get(cacheKey);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/course/v1/delete")
	public FirewallCourseVO delete(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody FirewallCourseVO courseVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return courseService.delete(courseVO, locale);
	}

}
