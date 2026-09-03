package com.digitusforum.firewall.background;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;

@RestController
public class FirewallBackgroundController {
	@Autowired
	FirewallLoginService firewallLoginService;
	@Autowired
	BackgroundRequestService backgroundRequestService;

	@CrossOrigin
	@PostMapping(value = "/firewall/background/v1/save")
	public BackgroundSaveVO save(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody BackgroundSaveVO body) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		if (body == null)
			body = new BackgroundSaveVO();
		if (StringUtils.isBlank(body.getName()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o name.");
		if (StringUtils.isBlank(body.getWallpaperData()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o wallpaperData.");
		body.setUserId(tokenVO.getUserId());
		return backgroundRequestService.save(body, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/background/v1/list")
	public List<BackgroundSaveVO> list(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return backgroundRequestService.list(tokenVO.getUserId(), locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/background/v1/select")
	public BackgroundSaveVO select(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody BackgroundSaveVO body) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		if (body == null)
			body = new BackgroundSaveVO();
		String backgroundId = StringUtils.isNotBlank(body.getBackgroundId()) ? body.getBackgroundId() : body.getId();
		if (StringUtils.isBlank(backgroundId))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o backgroundId.");
		body.setUserId(tokenVO.getUserId());
		body.setBackgroundId(backgroundId);
		return backgroundRequestService.select(body, locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/background/v1/setAuto")
	public BackgroundSaveVO setAuto(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return backgroundRequestService.setAuto(tokenVO.getUserId(), locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/background/v1/prefs")
	public BackgroundSaveVO prefs(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		return backgroundRequestService.prefs(tokenVO.getUserId(), locale);
	}

}
