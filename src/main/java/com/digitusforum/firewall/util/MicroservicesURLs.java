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
	
	public static String PERFIL = EnvironmentService.PERFIL_SERVER_URL + EnvironmentService.PERFIL_SERVER_PORT + "/perfil" + EnvironmentService.PERFIL_SERVER_VERSION;
	public static String PERFIL_RETRIEVE_LAST_USED = PERFIL + "/retrieve/lastUsed"; 
	
	public static String COURSE = EnvironmentService.COURSE_SERVER_URL + EnvironmentService.COURSE_SERVER_PORT + "/course" + EnvironmentService.COURSE_SERVER_VERSION;
	public static String COURSE_RETRIEVE_ALL = COURSE + "/retrieveAll"; 
	public static String COURSE_CREATE = COURSE + "/create"; 
	public static String COURSE_DELETE = COURSE + "/delete"; 
	public static String COURSE_RETRIEVE_BY_ID = COURSE + "/retrieveById"; 
	public static String COURSE_RETRIEVE_SUBJECTS_BY_COURSE_ID = COURSE + "/retrieveSubjectsByCourseId"; 
	public static String COURSE_RETRIEVE_MODULES_WITH_VIDEOS_BY_COURSE_ID = COURSE + "/retrieveModulesWithVideosByCourseId"; 
}
