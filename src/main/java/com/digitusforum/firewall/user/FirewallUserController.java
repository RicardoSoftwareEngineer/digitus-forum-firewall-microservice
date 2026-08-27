package com.digitusforum.firewall.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.FirewallLoginService;
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
			@RequestHeader String Authorization, @PathVariable Optional<Integer> id) {
		firewallLoginService.validateToken(Authorization, locale);
		return userService.retrieveUserById(locale, id.get());
	}

	@RequestMapping(value = "/firewall/user/v1/{id}/update")
	public Object update(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String Authorization,
			@PathVariable Optional<Integer> id, @RequestBody FirewallUserVO userVO) {
		firewallLoginService.validateToken(Authorization, locale);
		return userService.updateUser(locale, id.get(), userVO);
	}

	@RequestMapping(value = "/firewall/user/v1/{id}/delete")
	public Object delete(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String Authorization,
			@PathVariable Optional<Integer> id) {
		firewallLoginService.validateToken(Authorization, locale);
		return userService.deleteUser(locale, id.get());
	}

}
