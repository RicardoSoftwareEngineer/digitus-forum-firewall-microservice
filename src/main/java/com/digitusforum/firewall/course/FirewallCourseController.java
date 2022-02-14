package com.digitusforum.firewall.course;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.LoginMicroserviceDEPRECATED;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.user.UserVO;
import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallCourseController {
	LoginMicroserviceDEPRECATED loginMicroservice = new LoginMicroserviceDEPRECATED(new RequestService());
	CourseService trailAndCourseService = new CourseService(new RequestService());
	RequestService requestService = new RequestService();

	@CrossOrigin
	@GetMapping(value = "/firewall/course/v1/retrieveAll")
	public Object retrieve(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization) {
		TokenVO tokenVO = requestService.validateToken(authorization, locale);
		return trailAndCourseService.retrieveAll(locale);
	}

}
