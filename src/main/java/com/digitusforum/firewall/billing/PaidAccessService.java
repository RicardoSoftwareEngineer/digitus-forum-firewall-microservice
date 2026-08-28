package com.digitusforum.firewall.billing;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.module.ModuleRequestService;
import com.digitusforum.firewall.module.ModuleVO;
import com.digitusforum.firewall.training.FirewallTrainingVO;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.video.VideoVO;

@Service
public class PaidAccessService {
	public static final String GURU_JAVA = "java";

	@Autowired
	private FirewallLoginService firewallLoginService;
	@Autowired
	private RequestService requestService;
	@Autowired
	private ModuleRequestService moduleRequestService;
	@Autowired
	private BillingRequestService billingRequestService;

	public boolean hasAccess(String userId, FirewallTrainingVO training, String locale) {
		if (training == null || !training.isPaid())
			return true;
		if (StringUtils.isBlank(userId))
			return false;
		if (billingRequestService.hasPurchase(userId, training.getTrainingId(), locale))
			return true;
		if (GURU_JAVA.equalsIgnoreCase(training.getGuruId())
				&& billingRequestService.hasActiveJavaSubscription(userId, locale))
			return true;
		return false;
	}

	public TokenVO requireReadableTraining(String trainingId, String authorization, String locale) {
		if (StringUtils.isBlank(trainingId))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o trainingId.");
		FirewallTrainingVO query = new FirewallTrainingVO();
		query.setTrainingId(trainingId);
		FirewallTrainingVO training = requestService.retrieveById(query, locale);
		if (training == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Treinamento não encontrado.");
		if (!training.isPaid())
			return null;
		TokenVO tokenVO = firewallLoginService.validateToken(authorization == null ? "" : authorization, locale);
		if (!hasAccess(tokenVO.getUserId(), training, locale))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Este treinamento é pago. Compre ou assine o guru Java.");
		return tokenVO;
	}

	public TokenVO requireReadableVideo(VideoVO videoVO, String authorization, String locale) {
		if (videoVO == null || StringUtils.isBlank(videoVO.getModuleId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Aula paga exige moduleId para conferir o treinamento.");
		ModuleVO moduleQuery = new ModuleVO();
		moduleQuery.setModuleId(videoVO.getModuleId());
		ModuleVO module = moduleRequestService.retrieveById(moduleQuery, locale);
		if (module == null || StringUtils.isBlank(module.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não foi possível localizar o treinamento da aula.");
		return requireReadableTraining(module.getTrainingId(), authorization, locale);
	}

	public FirewallTrainingVO retrieveTraining(String trainingId, String locale) {
		FirewallTrainingVO query = new FirewallTrainingVO();
		query.setTrainingId(trainingId);
		return requestService.retrieveById(query, locale);
	}

}
