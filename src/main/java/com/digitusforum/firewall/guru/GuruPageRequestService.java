package com.digitusforum.firewall.guru;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitusforum.firewall.training.TrainingMicroserviceURLs;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.util.ThrowService;
import com.google.gson.Gson;

@Service
public class GuruPageRequestService {

	@Autowired
	private RequestService requestService;

	public GuruPageRequestService(RequestService requestService) {
		this.requestService = requestService;
	}

	private void checkTrainingMS(String locale) {
		if (!requestService.isUp(TrainingMicroserviceURLs.TRAINING))
			throw ThrowService.doIt(locale, 503, M.TRAINING_MICROSERVICE_OFFLINE);
	}

	public List<GuruPageVO> retrieveByGuruId(GuruPageVO guruPageVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(GuruURLs.RETRIEVE_BY_GURU_ID, guruPageVO, locale);
		List<GuruPageVO> pages = new Gson().fromJson(jsonResponse, List.class);
		return pages;
	}

}
