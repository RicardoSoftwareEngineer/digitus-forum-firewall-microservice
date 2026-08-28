package com.digitusforum.firewall.guru;

import com.digitusforum.firewall.util.EnvironmentService;

public class GuruURLs {
	public static String GURU_PAGE = EnvironmentService.TRAINING_SERVER_URL + EnvironmentService.TRAINING_SERVER_PORT
			+ "/guruPage" + EnvironmentService.TRAINING_SERVER_VERSION;
	public static String RETRIEVE_BY_GURU_ID = GURU_PAGE + "/retrieveByGuruId";
}
