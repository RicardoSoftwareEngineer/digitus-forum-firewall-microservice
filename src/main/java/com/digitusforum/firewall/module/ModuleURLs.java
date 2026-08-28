package com.digitusforum.firewall.module;

import com.digitusforum.firewall.util.EnvironmentService;

public class ModuleURLs {
	public static String MODULE = EnvironmentService.TRAINING_SERVER_URL + EnvironmentService.TRAINING_SERVER_PORT
			+ "/module" + EnvironmentService.TRAINING_SERVER_VERSION;
	public static String CREATE = MODULE + "/create";
	public static String RETRIEVE_BY_ID = MODULE + "/retrieveById";
	public static String RETRIEVE_BY_TRAINING_ID = MODULE + "/retrieveByTrainingId";
	public static String RETRIEVE_BY_TRAINING_ID_WITH_VIDEOS = MODULE + "/retrieveByTrainingIdWithVideos";
	public static String UPDATE = MODULE + "/update";
	public static String DELETE = MODULE + "/delete";
	public static String REORDER = MODULE + "/reorder";
	public static String ADD_VIDEO = MODULE + "/addVideo";
	public static String REMOVE_VIDEO = MODULE + "/removeVideo";
}
