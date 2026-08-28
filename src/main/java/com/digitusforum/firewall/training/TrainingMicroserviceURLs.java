package com.digitusforum.firewall.training;

import com.digitusforum.firewall.util.EnvironmentService;

public class TrainingMicroserviceURLs {
	public static String TRAINING = EnvironmentService.TRAINING_SERVER_URL + EnvironmentService.TRAINING_SERVER_PORT + "/training" + EnvironmentService.TRAINING_SERVER_VERSION;
	public static String TRAINING_RETRIEVE_ALL = TRAINING + "/retrieveAll"; 
	public static String TRAINING_CREATE = TRAINING + "/create"; 
	public static String TRAINING_RETRIEVE_BY_ID = TRAINING + "/retrieveById"; 
	public static String TRAINING_RETRIEVE_MODULES_WITH_VIDEOS_BY_TRAINING_ID = TRAINING + "/retrieveModulesWithVideosByTrainingId"; 
}
