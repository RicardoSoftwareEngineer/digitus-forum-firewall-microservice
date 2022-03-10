package com.digitusforum.firewall.subject;

import com.digitusforum.firewall.util.EnvironmentService;

public class SubjectURLs {
	public static String SUBJECT = EnvironmentService.COURSE_SERVER_URL + EnvironmentService.COURSE_SERVER_PORT
			+ "/subject" + EnvironmentService.COURSE_SERVER_VERSION;
	public static String CREATE = SUBJECT + "/create";
	public static String RETRIEVE_BY_PERFIL_ID = SUBJECT + "/retrieveByPerfilId";
	public static String RETRIEVE_BY_ID_WITH_VIDEOS = SUBJECT + "/retrieveByIdWithVideos";
}
