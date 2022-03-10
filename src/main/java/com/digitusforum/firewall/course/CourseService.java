package com.digitusforum.firewall.course;

import java.util.List;

import com.digitusforum.firewall.module.ModuleVO;
import com.digitusforum.firewall.util.RequestService;

public class CourseService {
	private RequestService requestService;

	public CourseService(RequestService requestService) {
		this.requestService = requestService;
	}
	
	
	
	public List<ModuleVO> retrieveModulesWithVideosByCourseId(CourseVO courseVO, String locale) {
		return requestService.retrieveModulesWithVideosByCourseId(courseVO, locale);
	}
	
	public CourseVO retrieveSubjectsByCourseId(CourseVO courseVO, String locale) {
		return requestService.retrieveSubjectsByCourseId(courseVO, locale);
	}
	
	public CourseVO retrieveById(CourseVO courseVO, String locale) {
		return requestService.retrieveById(courseVO, locale);
	}

	public List<CourseVO> retrieveAll(String locale) {
		return requestService.retrieveCourses(locale);
	}

	public CourseVO create(CourseVO courseVO, String locale) {
		return requestService.createCourse(courseVO, locale);
	}
	
	public CourseVO delete(CourseVO courseVO, String locale) {
		return requestService.delete(courseVO, locale);
	}

}
