package com.digitusforum.firewall.user;

public class FirewallUserVO {
	private String id;
	private String name;
	private Integer age;
	private String email;
	private String password;
	private String token;
	private boolean deleted;

	public FirewallUserVO() {
	}

	public FirewallUserVO(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public FirewallUserVO(String userId, String name, String email, String password) {
		this.id = userId;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
