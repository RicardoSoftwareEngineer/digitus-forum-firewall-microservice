package com.digitusforum.firewall.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.util.RequestService;

@RestController
public class FirewallUserController {
	FirewallUserService userService = new FirewallUserService(new RequestService());

	@Autowired
	FirewallLoginService firewallLoginService;

	@PostMapping(value = "/firewall/user/v1/create")
	public Object create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String authorization,
			@RequestBody FirewallUserVO userVO) {
		firewallLoginService.validateToken(authorization, locale);
		return userService.createUser(userVO, locale);
	}

	@GetMapping(value = "/firewall/user/v1/{id}/retrieve")
	public Object retrieveById(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String Authorization, @PathVariable String id) {
		TokenVO tokenVO = firewallLoginService.validateToken(Authorization, locale);
		requireOwnUserId(id, tokenVO);
		return userService.retrieveUserById(locale, id);
	}

	@RequestMapping(value = "/firewall/user/v1/{id}/update")
	public Object update(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String Authorization,
			@PathVariable String id, @RequestBody FirewallUserVO userVO) {
		TokenVO tokenVO = firewallLoginService.validateToken(Authorization, locale);
		requireOwnUserId(id, tokenVO);
		return userService.updateUser(locale, id, userVO);
	}

	@RequestMapping(value = "/firewall/user/v1/{id}/delete")
	public Object delete(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String Authorization,
			@PathVariable String id) {
		TokenVO tokenVO = firewallLoginService.validateToken(Authorization, locale);
		requireOwnUserId(id, tokenVO);
		return userService.deleteUser(locale, id);
	}

	private void requireOwnUserId(String id, TokenVO tokenVO) {
		if (id == null || tokenVO == null || tokenVO.getUserId() == null || !id.equals(tokenVO.getUserId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
		}
	}

}
