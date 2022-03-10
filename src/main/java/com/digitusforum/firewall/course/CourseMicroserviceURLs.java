package com.digitusforum.firewall.course;

import com.digitusforum.firewall.util.EnvironmentService;

public class CourseMicroserviceURLs {
	public static String COURSE = EnvironmentService.COURSE_SERVER_URL + EnvironmentService.COURSE_SERVER_PORT + "/course" + EnvironmentService.COURSE_SERVER_VERSION;
	public static String COURSE_RETRIEVE_ALL = COURSE + "/retrieveAll"; 
	public static String COURSE_CREATE = COURSE + "/create"; 
	public static String COURSE_RETRIEVE_BY_ID = COURSE + "/retrieveById"; 
	public static String COURSE_RETRIEVE_MODULES_WITH_VIDEOS_BY_COURSE_ID = COURSE + "/retrieveModulesWithVideosByCourseId"; 
}
