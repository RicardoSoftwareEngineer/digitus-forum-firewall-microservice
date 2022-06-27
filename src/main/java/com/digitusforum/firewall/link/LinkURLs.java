package com.digitusforum.firewall.link;

import com.digitusforum.firewall.util.EnvironmentService;

public class LinkURLs {
	public static String LINK = EnvironmentService.COURSE_SERVER_URL + EnvironmentService.COURSE_SERVER_PORT
			+ "/link" + EnvironmentService.COURSE_SERVER_VERSION;
	public static String CREATE = LINK + "/create";
	public static String RETRIEVE_BY_VIDEO_ID = LINK + "/retrieveByVideoId";
	public static String UPDATE = LINK + "/update";
	public static String DELETE = LINK + "/delete";
	public static String REORDER = LINK + "/reorder";
}
