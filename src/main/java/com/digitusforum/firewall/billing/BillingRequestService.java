package com.digitusforum.firewall.billing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitusforum.firewall.util.M;
import com.digitusforum.firewall.util.MicroservicesURLs;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.util.ThrowService;
import com.google.gson.Gson;

@Service
public class BillingRequestService {

	@Autowired
	private RequestService requestService;

	private void checkUserMS(String locale) {
		if (!requestService.isUp(MicroservicesURLs.USER))
			throw ThrowService.doIt(locale, 503, M.USER_MICROSERVICE_OFFLINE);
	}

	public List<PurchaseVO> retrievePurchasesByUserId(String userId, String locale) {
		checkUserMS(locale);
		PurchaseVO body = new PurchaseVO();
		body.setUserId(userId);
		String jsonResponse = requestService.request(MicroservicesURLs.USER_PURCHASE_BY_USER, body, locale);
		PurchaseVO[] rows = new Gson().fromJson(jsonResponse, PurchaseVO[].class);
		List<PurchaseVO> out = new ArrayList<>();
		if (rows != null) {
			for (int i = 0; i < rows.length; i++)
				out.add(rows[i]);
		}
		return out;
	}

	public boolean hasPurchase(String userId, String trainingId, String locale) {
		checkUserMS(locale);
		PurchaseVO body = new PurchaseVO();
		body.setUserId(userId);
		body.setTrainingId(trainingId);
		String jsonResponse = requestService.request(MicroservicesURLs.USER_PURCHASE_HAS, body, locale);
		PurchaseVO vo = new Gson().fromJson(jsonResponse, PurchaseVO.class);
		return vo != null && vo.isHasPurchase();
	}

	public PurchaseVO upsertPaid(PurchaseVO purchaseVO, String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.request(MicroservicesURLs.USER_PURCHASE_UPSERT, purchaseVO, locale);
		return new Gson().fromJson(jsonResponse, PurchaseVO.class);
	}

	public boolean hasActiveJavaSubscription(String userId, String locale) {
		checkUserMS(locale);
		SubscriptionVO body = new SubscriptionVO();
		body.setUserId(userId);
		body.setGuruId("java");
		String jsonResponse = requestService.request(MicroservicesURLs.USER_SUBSCRIPTION_HAS_ACTIVE, body, locale);
		SubscriptionVO vo = new Gson().fromJson(jsonResponse, SubscriptionVO.class);
		return vo != null && vo.isHasActive();
	}

	public SubscriptionVO upsertSubscription(SubscriptionVO subscriptionVO, String locale) {
		checkUserMS(locale);
		String jsonResponse = requestService.request(MicroservicesURLs.USER_SUBSCRIPTION_UPSERT, subscriptionVO, locale);
		return new Gson().fromJson(jsonResponse, SubscriptionVO.class);
	}

}
