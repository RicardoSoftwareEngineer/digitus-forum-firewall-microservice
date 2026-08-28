package com.digitusforum.firewall.guru;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class FirewallGuruController {
	@Autowired
	GuruPageRequestService guruPageRequestService;

	@CrossOrigin
	@PostMapping(value = "/firewall/guru/v1/{guruId}/pages")
	public List<GuruPageVO> retrievePages(@RequestHeader(defaultValue = "en_us") String locale,
			@PathVariable String guruId) {
		if (StringUtils.isBlank(guruId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.GURU_PAGE_MISSING_GURU_ID);
		GuruPageVO guruPageVO = new GuruPageVO();
		guruPageVO.setGuruId(guruId);
		return guruPageRequestService.retrieveByGuruId(guruPageVO, locale);
	}

}
