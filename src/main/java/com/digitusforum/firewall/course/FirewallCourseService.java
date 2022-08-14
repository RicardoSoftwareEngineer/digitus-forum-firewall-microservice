package com.digitusforum.firewall.course;

import java.util.List;

import org.springframework.stereotype.Service;

import com.digitusforum.firewall.module.ModuleVO;
import com.digitusforum.firewall.util.RequestService;


public class FirewallCourseService {
	private RequestService requestService;

	public FirewallCourseService(RequestService requestService) {
		this.requestService = requestService;
	}
	
	
	
	public List<ModuleVO> retrieveModulesWithVideosByCourseId(FirewallCourseVO courseVO, String locale) {
		return requestService.retrieveModulesWithVideosByCourseId(courseVO, locale);
	}
	
	public FirewallCourseVO retrieveSubjectsByCourseId(FirewallCourseVO courseVO, String locale) {
		return requestService.retrieveSubjectsByCourseId(courseVO, locale);
	}
	
	public FirewallCourseVO retrieveById(FirewallCourseVO courseVO, String locale) {
		return requestService.retrieveById(courseVO, locale);
	}

	public List<FirewallCourseVO> retrieveAll(String locale) {
		return requestService.retrieveCourses(locale);
	}

	public FirewallCourseVO create(FirewallCourseVO courseVO, String locale) {
		return requestService.createCourse(courseVO, locale);
	}
	
	public FirewallCourseVO delete(FirewallCourseVO courseVO, String locale) {
		return requestService.delete(courseVO, locale);
	}

}
