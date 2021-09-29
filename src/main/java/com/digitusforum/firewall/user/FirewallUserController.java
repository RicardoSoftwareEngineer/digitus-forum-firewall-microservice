package com.digitusforum.firewall.user;

import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitusforum.firewall.login.LoginMicroservice;

import request.RequestService;
import user.UserMicroservice;
import user.UserVO;

@CrossOrigin(origins = "*")
@RestController
public class FirewallUserController {
	LoginMicroservice loginMicroservice = new LoginMicroservice(new RequestService());
	UserMicroservice userMicroservice = new UserMicroservice(new RequestService());

	@PostMapping(value = "/firewall/user/v1/create")
	public Object create(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String Authorization,
			@RequestBody UserVO userVO) {
		UserVO loggedUser = loginMicroservice.validateToken(Authorization, locale);
		return userMicroservice.createUser(userVO, locale);
	}

	@GetMapping(value = "/firewall/user/v1/retrieve")
	public Object retrieve(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String Authorization) {
		UserVO loggedUser = loginMicroservice.validateToken(Authorization, locale);
		return userMicroservice.retrieveUsers(locale);
	}

	@GetMapping(value = "/firewall/user/v1/{id}/retrieve")
	public Object retrieveById(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String Authorization, @PathVariable Optional<Integer> id) {
		UserVO loggedUser = loginMicroservice.validateToken(Authorization, locale);
		return userMicroservice.retrieveUserById(locale, id.get());
	}

	@RequestMapping(value = "/firewall/user/v1/{id}/update")
	public Object update(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String Authorization,
			@PathVariable Optional<Integer> id, @RequestBody UserVO userVO) {
		UserVO loggedUser = loginMicroservice.validateToken(Authorization, locale);
		return userMicroservice.updateUser(locale, id.get(), userVO);
	}

	@RequestMapping(value = "/firewall/user/v1/{id}/delete")
	public Object delete(@RequestHeader(defaultValue = "en_us") String locale, @RequestHeader String Authorization,
			@PathVariable Optional<Integer> id) {
		UserVO loggedUser = loginMicroservice.validateToken(Authorization, locale);
		return userMicroservice.deleteUser(locale, id.get());
	}

	@RequestMapping(value = "/user/{id}/validate")
	public Object validate() {
		return "hi dad";
	}

}
