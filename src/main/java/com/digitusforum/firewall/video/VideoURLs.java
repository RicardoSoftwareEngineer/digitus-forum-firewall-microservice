package com.digitusforum.firewall.video;

import com.digitusforum.firewall.util.EnvironmentService;

public class VideoURLs {
	public static String VIDEO = EnvironmentService.COURSE_SERVER_URL + EnvironmentService.COURSE_SERVER_PORT
			+ "/video" + EnvironmentService.COURSE_SERVER_VERSION;
	public static String CREATE = VIDEO + "/create";
	public static String RETRIEVE_BY_ID = VIDEO + "/retrieveById";
	public static String RETRIEVE_BY_SUBJECT_ID = VIDEO + "/retrieveBySubjectId";
	public static String UPDATE = VIDEO + "/update";
	public static String DELETE = VIDEO + "/delete";
}
