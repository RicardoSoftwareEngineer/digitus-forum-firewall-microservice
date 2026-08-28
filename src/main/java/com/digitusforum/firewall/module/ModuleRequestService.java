package com.digitusforum.firewall.module;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitusforum.firewall.training.TrainingMicroserviceURLs;
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

	private void checkTrainingMS(String locale) {
		if (!requestService.isUp(TrainingMicroserviceURLs.TRAINING))
			throw ThrowService.doIt(locale, 503, M.TRAINING_MICROSERVICE_OFFLINE);
	}

	public ModuleVO create(ModuleVO moduleVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.CREATE, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}
	
	public ModuleVO retrieveById(ModuleVO moduleVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.RETRIEVE_BY_ID, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}

	public List<ModuleVO> retrieveByTrainingId(ModuleVO moduleVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.RETRIEVE_BY_TRAINING_ID, moduleVO, locale);
		List<ModuleVO> modules = new Gson().fromJson(jsonResponse, List.class);
		return modules;
	}

	public List<ModuleVO> retrieveByTrainingIdWithVideos(ModuleVO moduleVO, String locale) {
		System.out.println("Firewall microservice will make a request to update cache");
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.RETRIEVE_BY_TRAINING_ID_WITH_VIDEOS, moduleVO,
				locale);
		List<ModuleVO> modules = new Gson().fromJson(jsonResponse, List.class);
		return modules;
	}

	public ModuleVO update(ModuleVO moduleVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.UPDATE, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}

	public ModuleVO delete(ModuleVO moduleVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.DELETE, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}

	public List<ModuleVO> reorder(ModuleVO moduleVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.REORDER, moduleVO, locale);
		List<ModuleVO> modules = new Gson().fromJson(jsonResponse, List.class);
		return modules;
	}

	public ModuleVideoVO addVideo(ModuleVideoVO moduleVideoVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.ADD_VIDEO, moduleVideoVO, locale);
		ModuleVideoVO module = new Gson().fromJson(jsonResponse, ModuleVideoVO.class);
		return module;
	}

	public ModuleVO removeVideo(ModuleVO moduleVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = requestService.request(ModuleURLs.REMOVE_VIDEO, moduleVO, locale);
		ModuleVO module = new Gson().fromJson(jsonResponse, ModuleVO.class);
		return module;
	}

}
