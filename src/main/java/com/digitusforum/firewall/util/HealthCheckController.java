package com.digitusforum.firewall.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class HealthCheckController {

	@RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
	public ResponseEntity handle() {
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping("/test")
	public String test() {
		return "firewall";
	}

	@RequestMapping(value = "/firewall/healthCheck")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.status(HttpStatus.OK).body("ok");
	}
	
	@RequestMapping(value = "/healthCheck")
	public ResponseEntity<String> healthCheck2() {
		return ResponseEntity.status(HttpStatus.OK).body("ok");
	}
}
