package com.digitusforum.firewall.chat;

import java.util.List;

import org.springframework.web.client.HttpClientErrorException;

import com.digitusforum.firewall.module.ModuleVO;
import com.digitusforum.firewall.util.M;
import com.digitusforum.firewall.util.MicroservicesURLs;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.util.ThrowService;
import com.google.gson.Gson;

//todo pllleaase make this in kotlin, we are flying so high, so fast and so sophisticated that i would say simple
public class FirewallChatService {
	private RequestService requestService;

	public FirewallChatService(RequestService requestService) {
		this.requestService = requestService;
	}

	private void checkUserMS(String locale) {
		if (!requestService.isUp(MicroservicesURLs.USER))
			throw ThrowService.doIt(locale, 503, M.USER_MICROSERVICE_OFFLINE);
	}
	
	public FirewallChatConversationVO sup(FirewallChatMessageVO chatVO, String locale) {
		checkUserMS(locale);
		int i = 1;
		while(i < 20002) {
			chatVO.setFrom(i);
			chatVO.setTo(i+3);
			requestService.sup(MicroservicesURLs.SUP, chatVO, locale);
			i = i +3;
		}
			
		
		return new FirewallChatConversationVO();
	}
	

	public FirewallChatConversationVO chat(FirewallChatMessageVO chatVO, String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.chat(MicroservicesURLs.CHAT, chatVO, locale);
		return new Gson().fromJson(jsonResponse, FirewallChatConversationVO.class);
	}
	
	public List<FirewallChatSubjectVO> conversations(FirewallChatMessageVO chatVO, String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.conversations(MicroservicesURLs.CONVERSATIONS, chatVO, locale);
		List<FirewallChatSubjectVO> conversations = new Gson().fromJson(jsonResponse, List.class);
		return conversations;
	}
	
	public FirewallChatConversationVO conversation(FirewallChatMessageVO chatVO, String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.conversation(MicroservicesURLs.CONVERSATION, chatVO, locale);
		FirewallChatConversationVO conversation = new Gson().fromJson(jsonResponse, FirewallChatConversationVO.class);
		return conversation;
	}

	public FirewallChatMessageVO[] retrieveUsers(String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.request(MicroservicesURLs.USER_RETRIEVE_USERS, locale);
		return new Gson().fromJson(jsonResponse, FirewallChatMessageVO[].class);
	}

	public FirewallChatMessageVO retrieveUserById(String locale, int id) {
		checkUserMS(locale);
		String jsonResponse = null;
		try {
			String url = String.format(MicroservicesURLs.USER_RETRIEVE_BY_ID, id);
			jsonResponse = requestService.request(url, locale);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
		}
		return new Gson().fromJson(jsonResponse, FirewallChatMessageVO.class);
	}

	public FirewallChatMessageVO updateUser(String locale, int id, FirewallChatMessageVO userVO) {
		checkUserMS(locale);
		String jsonResponse = null;
		try {
			String url = String.format(MicroservicesURLs.USER_UPDATE, id);
			jsonResponse = requestService.request(url, userVO, locale);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
			if (e.getRawStatusCode() == 403)
				throw ThrowService.doIt(locale, 403, M.USER_EMAIL_ALREADY_IN_USE);
		}
		return new Gson().fromJson(jsonResponse, FirewallChatMessageVO.class);
	}

	public FirewallChatMessageVO deleteUser(String locale, int id) {
		checkUserMS(locale);
		String jsonResponse = null;
		try {
			String url = String.format(MicroservicesURLs.USER_DELETE, id);
			jsonResponse = requestService.request(url, locale);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
		}
		return new Gson().fromJson(jsonResponse, FirewallChatMessageVO.class);
	}

	public FirewallChatMessageVO retrieveByEmailAndPassword(FirewallChatMessageVO userVO, String locale) {
		checkUserMS(locale);
		String jsonResponse = null;
		try {
			String url = MicroservicesURLs.USER_RETRIEVE_BY_EMAIL_AND_PASSWORD;
			jsonResponse = requestService.request(url, userVO, locale);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404)
				throw ThrowService.doIt(locale, 404, M.USER_NOT_FOUND);
		}
		return new Gson().fromJson(jsonResponse, FirewallChatMessageVO.class);
	}
}
