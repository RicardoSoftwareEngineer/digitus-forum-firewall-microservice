package com.digitusforum.firewall.model.vo;

public class TokenVO {
	private String token;
	private String name;

	public TokenVO() {
	}

	public TokenVO(String token, String name) {
		this.token = token;
		this.name = name;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
