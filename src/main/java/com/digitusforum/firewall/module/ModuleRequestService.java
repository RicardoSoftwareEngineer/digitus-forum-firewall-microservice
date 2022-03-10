package com.digitusforum.firewall.module;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitusforum.firewall.course.CourseMicroserviceURLs;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.util.ThrowService;
import com.google.gson.Gson;

@Service
public class ModuleRequestService {

	@Autowired
	private RequestService requestService;

	public ModuleRequestService(RequestService requestService) {
		this.requestService = requestService;
	}

	private void checkCourseMS(String locale) {
		if (!requestService.isUp(CourseMicroserviceURLs.COURSE))
			throw ThrowService.doIt(locale, 503, M.COURSE_MICROSERVICE_OFFLINE);
	}

	public ModuleVO create(ModuleVO moduleVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.CREATE, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}
	
	public ModuleVO retrieveById(ModuleVO moduleVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.RETRIEVE_BY_ID, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}

	public List<ModuleVO> retrieveByCourseId(ModuleVO moduleVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.RETRIEVE_BY_COURSE_ID, moduleVO, locale);
		List<ModuleVO> modules = new Gson().fromJson(jsonResponse, List.class);
		return modules;
	}

	public List<ModuleVO> retrieveByCourseIdWithVideos(ModuleVO moduleVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.RETRIEVE_BY_COURSE_ID_WITH_VIDEOS, moduleVO,
				locale);
		List<ModuleVO> modules = new Gson().fromJson(jsonResponse, List.class);
		return modules;
	}

	public ModuleVO update(ModuleVO moduleVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.UPDATE, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}

	public ModuleVO delete(ModuleVO moduleVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.DELETE, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}

	public ModuleVO reorder(ModuleVO moduleVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.REORDER, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}

	public ModuleVideoVO addVideo(ModuleVideoVO moduleVideoVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.ADD_VIDEO, moduleVideoVO, locale);
		ModuleVideoVO module = new Gson().fromJson(jsonResponse, ModuleVideoVO.class);
		return module;
	}

	public ModuleVO removeVideo(ModuleVO moduleVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.REMOVE_VIDEO, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}

}
