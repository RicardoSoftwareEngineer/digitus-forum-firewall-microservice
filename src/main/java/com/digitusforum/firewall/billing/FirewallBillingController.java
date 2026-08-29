package com.digitusforum.firewall.billing;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.firewall.login.FirewallLoginService;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.training.FirewallTrainingVO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		return meFor(tokenVO.getUserId(), locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/billing/v1/publishable-key")
	public PublishableKeyVO publishableKey(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization) {
		firewallLoginService.validateToken(authorization, locale);
		if (stripeTestClient.isLivePublishableKey())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Chave live da Stripe é proibida. Use só pk_test_.");
		if (!stripeTestClient.isTestPublishableKey())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"STRIPE_PUBLISHABLE_KEY ainda não está ligada.");
		PublishableKeyVO vo = new PublishableKeyVO();
		vo.setPublishableKey(stripeTestClient.publishableKey());
		return vo;
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/billing/v1/checkout/training")
	public CheckoutTrainingVO checkoutTraining(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody CheckoutTrainingVO body, HttpServletRequest request) {
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
			if (stripeTestClient.priceForTraining(body.getTrainingId()) == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Este treinamento pago ainda não tem preço Stripe mapeado.");
			JsonObject session = stripeTestClient.createTrainingSession(tokenVO.getUserId(), body.getTrainingId(),
					tokenVO.getEmail(), returnUrl(body, request));
			return sessionSecret(body.getTrainingId(), session);
		}

		throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
				"As chaves de teste da Stripe ainda não estão ligadas. Não é possível iniciar o checkout.");
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/billing/v1/checkout/subscription")
	public CheckoutTrainingVO checkoutSubscription(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody(required = false) CheckoutTrainingVO body,
			HttpServletRequest request) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		if (stripeTestClient.isLiveKey())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Chave live da Stripe é proibida. Use só sk_test_.");
		if (!stripeTestClient.isTestKeyPresent())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"As chaves de teste da Stripe ainda não estão ligadas. Não é possível iniciar o checkout.");
		if (billingRequestService.hasActiveJavaSubscription(tokenVO.getUserId(), locale))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já tem a mensalidade java.");
		JsonObject session = stripeTestClient.createJavaSubscriptionSession(tokenVO.getUserId(), tokenVO.getEmail(),
				returnUrl(body, request));
		return sessionSecret(null, session);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/billing/v1/checkout/confirm")
	public BillingMeVO confirm(@RequestHeader(defaultValue = "en_us") String locale,
			@RequestHeader String authorization, @RequestBody CheckoutTrainingVO body) {
		TokenVO tokenVO = firewallLoginService.validateToken(authorization, locale);
		if (stripeTestClient.isLiveKey())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Chave live da Stripe é proibida. Use só sk_test_.");
		if (!stripeTestClient.isTestKeyPresent())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"As chaves de teste da Stripe ainda não estão ligadas. Não é possível iniciar o checkout.");
		if (body == null || StringUtils.isBlank(body.getSessionId()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o sessionId.");
		JsonObject session = stripeTestClient.retrieveSession(body.getSessionId());
		String sessionUser = stripeTestClient.metadata(session, "userId");
		if (StringUtils.isBlank(sessionUser))
			sessionUser = stripeTestClient.text(session, "client_reference_id");
		if (StringUtils.isBlank(sessionUser) || !sessionUser.equals(tokenVO.getUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta sessão não é sua.");
		String mode = stripeTestClient.text(session, "mode");
		String paymentStatus = stripeTestClient.text(session, "payment_status");
		String status = stripeTestClient.text(session, "status");
		boolean paid = "paid".equalsIgnoreCase(paymentStatus);
		boolean complete = "complete".equalsIgnoreCase(status);
		if ("payment".equalsIgnoreCase(mode)) {
			if (!paid)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Pagamento ainda não confirmado pela Stripe.");
			String trainingId = stripeTestClient.metadata(session, "trainingId");
			if (StringUtils.isBlank(trainingId))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sessão sem trainingId.");
			billingRequestService.upsertPaid(
					stripeTestClient.purchaseFromSession(session, tokenVO.getUserId(), trainingId), locale);
		} else if ("subscription".equalsIgnoreCase(mode)) {
			if (!paid && !complete)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Assinatura ainda não confirmada pela Stripe.");
			billingRequestService.upsertSubscription(
					stripeTestClient.subscriptionFromSession(session, tokenVO.getUserId()), locale);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Modo de sessão Stripe não suportado.");
		}
		return meFor(tokenVO.getUserId(), locale);
	}

	@CrossOrigin
	@PostMapping(value = "/firewall/billing/v1/stripe/webhook")
	public ResponseEntity<String> webhook(@RequestHeader(value = "Stripe-Signature", required = false) String signature,
			@RequestBody String payload) {
		if (StringUtils.isBlank(stripeTestClient.webhookSecret()))
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"STRIPE_WEBHOOK_SECRET não está ligado. Não aceitamos evento sem verificação.");
		if (!stripeTestClient.verifyWebhookSignature(payload, signature))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stripe-Signature inválida.");
		if (stripeTestClient.isLiveKey())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Chave live da Stripe é proibida. Use só sk_test_.");
		JsonObject event = JsonParser.parseString(payload).getAsJsonObject();
		String type = event.has("type") && event.get("type").isJsonPrimitive() ? event.get("type").getAsString() : "";
		JsonObject object = event.has("data") && event.get("data").isJsonObject()
				&& event.getAsJsonObject("data").has("object")
						? event.getAsJsonObject("data").getAsJsonObject("object")
						: null;
		if (object == null)
			return ResponseEntity.ok("ignored");
		String locale = "pt_BR";
		if ("checkout.session.completed".equals(type)) {
			applySession(object, locale);
		} else if ("invoice.paid".equals(type)) {
			applyInvoicePaid(object, locale);
		} else if ("customer.subscription.deleted".equals(type)) {
			applySubscriptionDeleted(object, locale);
		}
		return ResponseEntity.ok("ok");
	}

	@CrossOrigin
	@GetMapping(value = "/firewall/billing/v1/embedded-return", produces = MediaType.TEXT_HTML_VALUE)
	public String embeddedReturn() {
		return "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Digitus</title></head>"
				+ "<body><p>Pagamento enviado. Pode voltar ao curso.</p></body></html>";
	}

	private BillingMeVO meFor(String userId, String locale) {
		BillingMeVO me = new BillingMeVO();
		List<PurchaseVO> purchases = billingRequestService.retrievePurchasesByUserId(userId, locale);
		List<String> ids = new ArrayList<>();
		for (PurchaseVO purchase : purchases) {
			if (purchase != null && StringUtils.isNotBlank(purchase.getTrainingId())
					&& ("paid".equalsIgnoreCase(purchase.getStatus()) || purchase.isHasPurchase()))
				ids.add(purchase.getTrainingId());
		}
		me.setPurchasedTrainingIds(ids);
		me.setJavaSubscriptionActive(billingRequestService.hasActiveJavaSubscription(userId, locale));
		return me;
	}

	private String returnUrl(CheckoutTrainingVO body, HttpServletRequest request) {
		String requested = body == null ? null : body.getReturnUrl();
		String host = request == null ? null : request.getHeader("Host");
		String proto = request == null ? null : request.getHeader("X-Forwarded-Proto");
		if (StringUtils.isBlank(proto) && request != null)
			proto = request.getScheme();
		return stripeTestClient.resolveReturnUrl(requested, host, proto);
	}

	private CheckoutTrainingVO sessionSecret(String trainingId, JsonObject session) {
		String secret = stripeTestClient.text(session, "client_secret");
		if (StringUtils.isBlank(secret))
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"A Stripe não devolveu client_secret. Não cobramos nada.");
		if (secret.startsWith("sk_"))
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"A Stripe devolveu um valor inválido. Não cobramos nada.");
		CheckoutTrainingVO vo = new CheckoutTrainingVO();
		vo.setTrainingId(trainingId);
		vo.setClientSecret(secret);
		vo.setSessionId(stripeTestClient.stripeId(session, "id"));
		return vo;
	}

	private void applySession(JsonObject session, String locale) {
		String userId = stripeTestClient.metadata(session, "userId");
		if (StringUtils.isBlank(userId))
			userId = stripeTestClient.text(session, "client_reference_id");
		if (StringUtils.isBlank(userId))
			return;
		String mode = stripeTestClient.text(session, "mode");
		String paymentStatus = stripeTestClient.text(session, "payment_status");
		String status = stripeTestClient.text(session, "status");
		boolean paid = "paid".equalsIgnoreCase(paymentStatus);
		boolean complete = "complete".equalsIgnoreCase(status);
		if ("payment".equalsIgnoreCase(mode) && paid) {
			String trainingId = stripeTestClient.metadata(session, "trainingId");
			if (StringUtils.isNotBlank(trainingId))
				billingRequestService.upsertPaid(stripeTestClient.purchaseFromSession(session, userId, trainingId),
						locale);
		} else if ("subscription".equalsIgnoreCase(mode) && (paid || complete)) {
			billingRequestService.upsertSubscription(stripeTestClient.subscriptionFromSession(session, userId), locale);
		}
	}

	private void applyInvoicePaid(JsonObject invoice, String locale) {
		String userId = stripeTestClient.metadata(invoice, "userId");
		String subId = stripeTestClient.stripeId(invoice, "subscription");
		String customer = stripeTestClient.stripeId(invoice, "customer");
		if (StringUtils.isBlank(userId))
			return;
		SubscriptionVO vo = new SubscriptionVO();
		vo.setUserId(userId);
		vo.setGuruId(StripeTestClient.GURU_JAVA);
		vo.setScope("guru");
		vo.setStatus("active");
		vo.setStripeCustomerId(customer);
		vo.setStripeSubscriptionId(subId);
		billingRequestService.upsertSubscription(vo, locale);
	}

	private void applySubscriptionDeleted(JsonObject subscription, String locale) {
		String userId = stripeTestClient.metadata(subscription, "userId");
		if (StringUtils.isBlank(userId))
			return;
		SubscriptionVO vo = new SubscriptionVO();
		vo.setUserId(userId);
		vo.setGuruId(StringUtils.defaultIfBlank(stripeTestClient.metadata(subscription, "guruId"),
				StripeTestClient.GURU_JAVA));
		vo.setScope("guru");
		vo.setStatus("canceled");
		vo.setStripeCustomerId(stripeTestClient.stripeId(subscription, "customer"));
		vo.setStripeSubscriptionId(stripeTestClient.stripeId(subscription, "id"));
		billingRequestService.upsertSubscription(vo, locale);
	}

}
