package com.digitusforum.firewall.emailVerification;

import com.digitusforum.firewall.util.EnvironmentService;

//TODO deixar de usar esse cara e usar uma classe dessa pra cada pacote
public class FirewallEmailVerificationURLs {
	public static String USER = EnvironmentService.USER_SERVER_URL + EnvironmentService.USER_SERVER_PORT + "/emailVerification" + EnvironmentService.USER_SERVER_VERSION;
	public static String SEND_VALIDATION_EMAIL = USER + "/sendValidationEmail"; 
	public static String VALIDATE_EMAIL = USER + "/validateEmail"; 
	public static String SEND_RESET_PASSWORD_EMAIL = USER + "/sendResetPasswordEmail"; 
	public static String RESET_PASSWORD = USER + "/resetPassword"; 
}
