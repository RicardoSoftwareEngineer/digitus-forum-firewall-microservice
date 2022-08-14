package com.digitusforum.firewall.chat;

public class FirewallChatMessageVO {
	private String chatSubjectId;
	private String chatSubjectName;
	private String userId;
	private String userName;
	private String userEmail;
	private String chatMessageId;
	private String name;
	private String message;
	private String status;
	private boolean deleted;

	public FirewallChatMessageVO() {
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getChatSubjectId() {
		return chatSubjectId;
	}

	public void setChatSubjectId(String chatSubjectId) {
		this.chatSubjectId = chatSubjectId;
	}

	public String getChatSubjectName() {
		return chatSubjectName;
	}

	public void setChatSubjectName(String chatSubjectName) {
		this.chatSubjectName = chatSubjectName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getChatMessageId() {
		return chatMessageId;
	}

	public void setChatMessageId(String chatMessageId) {
		this.chatMessageId = chatMessageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
