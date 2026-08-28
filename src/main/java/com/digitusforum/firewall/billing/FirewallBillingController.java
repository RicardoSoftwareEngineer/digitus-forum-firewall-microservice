package com.digitusforum.firewall.billing;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.training.FirewallTrainingVO;

@RestController
public class FirewallBillingController {
	@Autowired
	FirewallLoginService firewallLoginService;
	@Autowired
	BillingRequestService billingRequestService;
	@Autowired
	PaidAccessService paidAccessService;
	@Autowired
	StripeTestClient stripeTestClient;

	@CrossOrigin
	@PostMapping(value = "/firewall/billing/v1/me")
	public BillingMeVO me(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		BillingMeVO me = new BillingMeVO();
		List<PurchaseVO> purchases = billingRequestService.retrievePurchasesByUserId(tokenVO.getUserId(), locale);
		List<String> ids = new ArrayList<>();
		for (PurchaseVO purchase : purchases) {
			if (purchase != null && StringUtils.isNotBlank(purchase.getTrainingId())
					&& ("paid".equalsIgnoreCase(purchase.getStatus()) || purchase.isHasPurchase()))
				ids.add(purchase.getTrainingId());
		}
		me.setPurchasedTrainingIds(ids);
		me.setJavaSubscriptionActive(
				billingRequestService.hasActiveJavaSubscription(tokenVO.getUserId(), locale));
		return me;
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/billing/v1/checkout/training")
	public CheckoutTrainingVO checkoutTraining(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody CheckoutTrainingVO body) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		if (body == null || StringUtils.isBlank(body.getTrainingId()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o trainingId.");
		FirewallTrainingVO training = paidAccessService.retrieveTraining(body.getTrainingId(), locale);
		if (training == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Treinamento não encontrado.");
		if (!training.isPaid())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este treinamento é gratuito.");
		if (paidAccessService.hasAccess(tokenVO.getUserId(), training, locale))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já tem este treinamento.");

		if (stripeTestClient.isLiveKey())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Chave live da Stripe é proibida. Use só sk_test_.");

		if (stripeTestClient.isTestKeyPresent()) {
			PurchaseVO alreadyPaid = stripeTestClient.findPaidTraining(tokenVO.getUserId(), body.getTrainingId());
			if (alreadyPaid != null) {
				billingRequestService.upsertPaid(alreadyPaid, locale);
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já tem este treinamento.");
			}
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"O checkout Stripe embedded ainda não está ligado neste ambiente. Não cobramos nada.");
		}

		throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
				"As chaves de teste da Stripe ainda não estão ligadas. Não é possível iniciar o checkout.");
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/billing/v1/checkout/subscription")
	public CheckoutTrainingVO checkoutSubscription(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		firewallLoginService.validateToken(authorization, locale);
		if (stripeTestClient.isLiveKey())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Chave live da Stripe é proibida. Use só sk_test_.");
		throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
				"As chaves de teste da Stripe ainda não estão ligadas. Não é possível iniciar o checkout.");
	}

}
