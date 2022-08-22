package com.digitusforum.firewall.chat;

import java.util.ArrayList;
import java.util.List;

public class FirewallChatConversationVO {
	private String subjectId;
	private String subjectName;
	private String subjectStatus;
	private List<FirewallChatMessageVO> conversation = new ArrayList<>();

	public FirewallChatConversationVO() {
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectStatus() {
		return subjectStatus;
	}

	public void setSubjectStatus(String subjectStatus) {
		this.subjectStatus = subjectStatus;
	}

	public List<FirewallChatMessageVO> getConversation() {
		return conversation;
	}

	public void setConversation(List<FirewallChatMessageVO> conversation) {
		this.conversation = conversation;
	}

}
