package com.digitusforum.firewall.link;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitusforum.firewall.training.TrainingMicroserviceURLs;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.util.ThrowService;
import com.google.gson.Gson;

@Service
public class LinkRequestService {

	@Autowired
	private RequestService requestService;

	public LinkRequestService(RequestService requestService) {
		this.requestService = requestService;
	}

	private void checkTrainingMS(String locale) {
		if (!requestService.isUp(TrainingMicroserviceURLs.TRAINING))
			throw ThrowService.doIt(locale, 503, M.TRAINING_MICROSERVICE_OFFLINE);
	}

	public LinkVO create(LinkVO linkVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(LinkURLs.CREATE, linkVO, locale);
		LinkVO link = new Gson().fromJson(jsonResponse, LinkVO.class);
		return link;
	}

	public List<LinkVO> retrieveByVideoId(LinkVO linkVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(LinkURLs.RETRIEVE_BY_VIDEO_ID, linkVO, locale);
		List<LinkVO> links = new Gson().fromJson(jsonResponse, List.class);
		return links;
	}

	public LinkVO update(LinkVO linkVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(LinkURLs.UPDATE, linkVO, locale);
		LinkVO link = new Gson().fromJson(jsonResponse, LinkVO.class);
		return link;
	}

	public LinkVO delete(LinkVO linkVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(LinkURLs.DELETE, linkVO, locale);
		LinkVO link = new Gson().fromJson(jsonResponse, LinkVO.class);
		return link;
	}

	public LinkVO reorder(LinkVO linkVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(LinkURLs.REORDER, linkVO, locale);
		LinkVO link = new Gson().fromJson(jsonResponse, LinkVO.class);
		return link;
	}

}
