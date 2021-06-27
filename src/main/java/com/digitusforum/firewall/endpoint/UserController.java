package com.digitusforum.firewall.endpoint;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class UserController {
	@RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
	public ResponseEntity handle() {
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/user/create")
	public Object create() {
		return "";
	}

	@RequestMapping(value = "/user/retrieve")
	public Object retrieve(@PathVariable Optional<String> id) {
		return "hi dad";
	}

	@RequestMapping(value = "/user/{id}/retrieve")
	public Object retrieve() {
		return "hi dad";
	}

	@RequestMapping(value = "/user/{id}/update")
	public Object update() {
		return "hi dad";
	}

	@RequestMapping(value = "/user/{id}/delete")
	public Object delete() {
		return "hi dad";
	}

	@RequestMapping(value = "/user/{id}/validate")
	public Object validate() {
		return "hi dad";
	}

}
