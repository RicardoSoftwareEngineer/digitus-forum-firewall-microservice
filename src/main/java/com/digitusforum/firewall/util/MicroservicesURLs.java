package com.digitusforum.firewall.util;

//TODO deixar de usar esse cara e usar uma classe dessa pra cada pacote
public class MicroservicesURLs {
	public static String I18 = EnvironmentService.I18_SERVER_URL + EnvironmentService.I18_SERVER_PORT + "/i18" + EnvironmentService.I18_SERVER_VERSION;
	
	public static String LOGIN = EnvironmentService.LOGIN_SERVER_URL + EnvironmentService.LOGIN_SERVER_PORT + "/login" + EnvironmentService.LOGIN_SERVER_VERSION;
	public static String LOGIN_BY_EMAIL_AND_PASSWORD = LOGIN + "/loginByEmailAndPassword"; 
	public static String LOGIN_CREATE_TOKEN = LOGIN + "/createToken"; 
	public static String LOGIN_VALIDATE_TOKEN = LOGIN + "/validateToken";
	
	public static String USER = EnvironmentService.USER_SERVER_URL + EnvironmentService.USER_SERVER_PORT + "/user" + EnvironmentService.USER_SERVER_VERSION;
	public static String USER_RETRIEVE_BY_EMAIL_AND_PASSWORD = USER + "/retrieve/byEmailAndPassword"; 
	public static String USER_CREATE = USER + "/create"; 
	public static String USER_RETRIEVE_USERS = USER + "/retrieve"; 
	public static String USER_RETRIEVE_BY_ID = USER + "/%s/retrieve"; 
	public static String USER_UPDATE = USER + "/%s/update"; 
	public static String USER_DELETE = USER + "/%s/delete"; 
	public static String CHAT = USER + "/chat"; 
	public static String SUP = USER + "/sup"; 
	public static String CONVERSATIONS = USER + "/conversations"; 
	public static String CONVERSATION = USER + "/conversation"; 
	public static String USER_PURCHASE_BY_USER = USER + "/purchase/retrieveByUserId";
	public static String USER_PURCHASE_HAS = USER + "/purchase/hasPurchase";
	public static String USER_PURCHASE_UPSERT = USER + "/purchase/upsertPaid";
	public static String USER_SUBSCRIPTION_BY_USER = USER + "/subscription/retrieveByUserId";
	public static String USER_SUBSCRIPTION_HAS_ACTIVE = USER + "/subscription/hasActive";
	public static String USER_SUBSCRIPTION_UPSERT = USER + "/subscription/upsert";
	public static String USER_BACKGROUND_SAVE = USER + "/background/save";
	public static String USER_BACKGROUND_BY_USER = USER + "/background/retrieveByUserId";
	public static String USER_BACKGROUND_SELECT = USER + "/background/select";
	public static String USER_BACKGROUND_SET_AUTO = USER + "/background/setAuto";
	public static String USER_BACKGROUND_PREFS = USER + "/background/prefs";
	
	public static String PERFIL = EnvironmentService.PERFIL_SERVER_URL + EnvironmentService.PERFIL_SERVER_PORT + "/perfil" + EnvironmentService.PERFIL_SERVER_VERSION;
	public static String PERFIL_RETRIEVE_LAST_USED = PERFIL + "/retrieve/lastUsed"; 
	
	public static String TRAINING = EnvironmentService.TRAINING_SERVER_URL + EnvironmentService.TRAINING_SERVER_PORT + "/training" + EnvironmentService.TRAINING_SERVER_VERSION;
	public static String TRAINING_RETRIEVE_ALL = TRAINING + "/retrieveAll"; 
	public static String TRAINING_CREATE = TRAINING + "/create"; 
	public static String TRAINING_DELETE = TRAINING + "/delete"; 
	public static String TRAINING_RETRIEVE_BY_ID = TRAINING + "/retrieveById"; 
	public static String TRAINING_RETRIEVE_CATALOG_BY_ID = TRAINING + "/retrieveCatalogById"; 
	public static String TRAINING_RETRIEVE_SUBJECTS_BY_TRAINING_ID = TRAINING + "/retrieveSubjectsByTrainingId"; 
	public static String TRAINING_RETRIEVE_MODULES_WITH_VIDEOS_BY_TRAINING_ID = TRAINING + "/retrieveModulesWithVideosByTrainingId"; 
}
