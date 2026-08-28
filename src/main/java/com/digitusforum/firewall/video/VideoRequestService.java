package com.digitusforum.firewall.video;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitusforum.firewall.training.TrainingMicroserviceURLs;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.util.ThrowService;
import com.google.gson.Gson;

@Service
public class VideoRequestService {

	@Autowired
	private RequestService requestService;

	public VideoRequestService(RequestService requestService) {
		this.requestService = requestService;
	}

	private void checkTrainingMS(String locale) {
		if (!requestService.isUp(TrainingMicroserviceURLs.TRAINING))
			throw ThrowService.doIt(locale, 503, M.TRAINING_MICROSERVICE_OFFLINE);
	}

	public VideoVO create(VideoVO videoVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(VideoURLs.CREATE, videoVO, locale);
		VideoVO video = new Gson().fromJson(jsonResponse, VideoVO.class);
		return video;
	}

	public VideoVO retrieveById(VideoVO videoVO, String locale) {
		System.out.println("Firewall microservice will make a request to update cache");
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(VideoURLs.RETRIEVE_BY_ID, videoVO, locale);
		VideoVO video = new Gson().fromJson(jsonResponse, VideoVO.class);
		return video;
	}

	public List<VideoVO> retrieveBySubjectId(VideoVO videoVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(VideoURLs.RETRIEVE_BY_SUBJECT_ID, videoVO, locale);
		List<VideoVO> videos = new Gson().fromJson(jsonResponse, List.class);
		return videos;
	}

	public VideoVO update(VideoVO videoVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(VideoURLs.UPDATE, videoVO, locale);
		VideoVO video = new Gson().fromJson(jsonResponse, VideoVO.class);
		return video;
	}
	
	public VideoVO delete(VideoVO videoVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(VideoURLs.DELETE, videoVO, locale);
		VideoVO video = new Gson().fromJson(jsonResponse, VideoVO.class);
		return video;
	}

}
