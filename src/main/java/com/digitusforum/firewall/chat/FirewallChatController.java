package com.digitusforum.firewall.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallChatController {
	FirewallChatService chatService = new FirewallChatService(new RequestService());
	@Autowired
	FirewallLoginService firewallLoginService = new FirewallLoginService();

	@CrossOrigin
	@PostMapping(value = "/firewall/user/v1/chat")
	public FirewallChatMessageVO chat(@RequestHeader(defaultValue = "pt_br") String locale, @RequestHeader String authorization,
			@RequestBody FirewallChatMessageVO chatVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		chatVO.setUserId(tokenVO.getUserId());
		chatVO.setUserName(tokenVO.getUserName());
		chatVO.setUserEmail(tokenVO.getEmail());
		return chatService.chat(chatVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/user/v1/conversations")
	public List<FirewallChatSubjectVO> conversations(@RequestHeader(defaultValue = "pt_br") String locale, @RequestHeader String authorization,
			@RequestBody FirewallChatMessageVO chatVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		chatVO.setUserId(tokenVO.getUserId());
		return chatService.conversations(chatVO, locale);
	}
	
	@CrossOrigin
	@PostMapping(value = "/firewall/user/v1/conversation")
	public List<FirewallChatMessageVO> conversation(@RequestHeader(defaultValue = "pt_br") String locale, @RequestHeader String authorization,
			@RequestBody FirewallChatMessageVO chatVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		chatVO.setUserId(tokenVO.getUserId());
		return chatService.conversation(chatVO, locale);
	}

	
}
