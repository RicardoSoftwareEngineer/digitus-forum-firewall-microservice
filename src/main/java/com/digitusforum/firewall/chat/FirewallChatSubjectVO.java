package com.digitusforum.firewall.chat;

import java.time.ZonedDateTime;

public class FirewallChatSubjectVO {
	private String chatSubjectId;
	private String userId;
	private String name;
	private String status;
	private ZonedDateTime lastUpdated;
	private boolean deleted;

	public FirewallChatSubjectVO() {
	}

	public String getChatSubjectId() {
		return chatSubjectId;
	}

	public void setChatSubjectId(String chatSubjectId) {
		this.chatSubjectId = chatSubjectId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ZonedDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(ZonedDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
