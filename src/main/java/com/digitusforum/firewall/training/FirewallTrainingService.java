package com.digitusforum.firewall.training;

import java.util.List;

import org.springframework.stereotype.Service;

import com.digitusforum.firewall.module.ModuleVO;
import com.digitusforum.firewall.util.RequestService;


public class FirewallTrainingService {
	private RequestService requestService;

	public FirewallTrainingService(RequestService requestService) {
		this.requestService = requestService;
	}
	
	
	
	public List<ModuleVO> retrieveModulesWithVideosByTrainingId(FirewallTrainingVO trainingVO, String locale) {
		return requestService.retrieveModulesWithVideosByTrainingId(trainingVO, locale);
	}
	
	public FirewallTrainingVO retrieveSubjectsByTrainingId(FirewallTrainingVO trainingVO, String locale) {
		System.out.println("firewall fez a requisição pra atualizar o cache");
		return requestService.retrieveSubjectsByTrainingId(trainingVO, locale);
	}
	
	public FirewallTrainingVO retrieveById(FirewallTrainingVO trainingVO, String locale) {
		return requestService.retrieveById(trainingVO, locale);
	}

	public List<FirewallTrainingVO> retrieveAll(String locale) {
		return requestService.retrieveTrainings(locale);
	}

	public FirewallTrainingVO create(FirewallTrainingVO trainingVO, String locale) {
		return requestService.createTraining(trainingVO, locale);
	}
	
	public FirewallTrainingVO delete(FirewallTrainingVO trainingVO, String locale) {
		return requestService.delete(trainingVO, locale);
	}

}
