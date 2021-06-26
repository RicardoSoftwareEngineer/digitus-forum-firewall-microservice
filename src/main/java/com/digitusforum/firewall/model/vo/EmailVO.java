package com.digitusforum.firewall.model.vo;

public class EmailVO {
	private String email;
	private Integer readableNumber;
	private Integer verificationNumber;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getReadableNumber() {
		return readableNumber;
	}

	public void setReadableNumber(Integer readableNumber) {
		this.readableNumber = readableNumber;
	}

	public Integer getVerificationNumber() {
		return verificationNumber;
	}

	public void setVerificationNumber(Integer verificationNumber) {
		this.verificationNumber = verificationNumber;
	}
}
