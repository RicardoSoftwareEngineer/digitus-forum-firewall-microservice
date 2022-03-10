package com.digitusforum.firewall.course;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallCourseController {
	// LoginMicroserviceDEPRECATED loginMicroservice = new
	// LoginMicroserviceDEPRECATED(new RequestService());
	CourseService courseService = new CourseService(new RequestService());
	RequestService requestService = new RequestService();

	@CrossOrigin
	@PostMapping(value = "/firewall/course/v1/create")
	public CourseVO create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody CourseVO courseVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return courseService.create(courseVO, locale);
	}

	@CrossOrigin
	@GetMapping(value = "/firewall/course/v1/retrieveAll")
	public List<CourseVO> retrieve(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return courseService.retrieveAll(locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/course/v1/retrieveById")
	public CourseVO retrieve(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody CourseVO courseVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return courseService.retrieveById(courseVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/course/v1/retrieveSubjectsByCourseId")
	public CourseVO retrieveSubjectsByCourseId(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody CourseVO courseVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return courseService.retrieveSubjectsByCourseId(courseVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/course/v1/delete")
	public CourseVO delete(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody CourseVO courseVO) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return courseService.delete(courseVO, locale);
	}

}
