package com.digitusforum.firewall.course;

import java.util.List;

import com.digitusforum.firewall.util.RequestService;

public class CourseService {
	private RequestService requestService;

	public CourseService(RequestService requestService) {
		this.requestService = requestService;
	}

	public List<CourseVO> retrieveAll(String locale) {
		return requestService.retrieveCourses(locale);
	}

}
