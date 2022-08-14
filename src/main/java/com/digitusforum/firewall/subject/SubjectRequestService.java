package com.digitusforum.firewall.subject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitusforum.firewall.course.CourseMicroserviceURLs;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.util.ThrowService;
import com.google.gson.Gson;

@Service
public class SubjectRequestService {

	@Autowired
	private RequestService requestService;

	public SubjectRequestService(RequestService requestService) {
		this.requestService = requestService;
	}

	private void checkCourseMS(String locale) {
		if (!requestService.isUp(CourseMicroserviceURLs.COURSE))
			throw ThrowService.doIt(locale, 503, M.COURSE_MICROSERVICE_OFFLINE);
	}

	public SubjectVO create(SubjectVO subjectVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(SubjectURLs.CREATE, subjectVO, locale);
		SubjectVO subject = new Gson().fromJson(jsonResponse, SubjectVO.class);
		return subject;
	}

	public List<SubjectVO> retrieveByCourseId(SubjectVO subjectVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(SubjectURLs.RETRIEVE_BY_COURSE_ID, subjectVO, locale);
		List<SubjectVO> subjects = new Gson().fromJson(jsonResponse, List.class);
		return subjects;
	}

	public SubjectVO retrieveByIdWithVideos(SubjectVO subjectVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(SubjectURLs.RETRIEVE_BY_ID_WITH_VIDEOS, subjectVO, locale);
		SubjectVO subject = new Gson().fromJson(jsonResponse, SubjectVO.class);
		return subject;
	}
	
	public SubjectVO update(SubjectVO subjectVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(SubjectURLs.UPDATE, subjectVO, locale);
		SubjectVO subject = new Gson().fromJson(jsonResponse, SubjectVO.class);
		return subject;
	}

	public SubjectVO addVideo(SubjectVO subjectVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(SubjectURLs.ADD_VIDEO, subjectVO, locale);
		SubjectVO subject = new Gson().fromJson(jsonResponse, SubjectVO.class);
		return subject;
	}

	public SubjectVO removeVideo(SubjectVO subjectVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(SubjectURLs.REMOVE_VIDEO, subjectVO, locale);
		SubjectVO subject = new Gson().fromJson(jsonResponse, SubjectVO.class);
		return subject;
	}
	
	public List<SubjectVO> retrieveByVideo(SubjectVO subjectVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(SubjectURLs.RETRIEVE_BY_VIDEO, subjectVO, locale);
		List<SubjectVO> subjects = new Gson().fromJson(jsonResponse, List.class);
		return subjects;
	}

}
