package com.digitusforum.firewall.emailVerification;

public class FirewallEmailVerificationVO {
	private String emailVerificationId;
	private String email;
	private boolean success;
	private String recaptchaToken;
	private String password;
	private String retypePassword;
	private String response;
	private Integer readableNumber;
	private Boolean used;
	private String userId;

	public FirewallEmailVerificationVO() {
	}

	public String getRetypePassword() {
		return retypePassword;
	}

	public void setRetypePassword(String retypePassword) {
		this.retypePassword = retypePassword;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getEmailVerificationId() {
		return emailVerificationId;
	}

	public void setEmailVerificationId(String emailVerificationId) {
		this.emailVerificationId = emailVerificationId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getReadableNumber() {
		return readableNumber;
	}

	public void setReadableNumber(Integer readableNumber) {
		this.readableNumber = readableNumber;
	}

	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

	public String getRecaptchaToken() {
		return recaptchaToken;
	}

	public void setRecaptchaToken(String recaptchaToken) {
		this.recaptchaToken = recaptchaToken;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
