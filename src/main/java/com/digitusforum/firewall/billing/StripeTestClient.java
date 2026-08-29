package com.digitusforum.firewall.billing;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class StripeTestClient {
	public static final String JAVA_PAGO_TRAINING_ID = "c0ffee00-0000-4000-8000-000000000001";
	public static final String JAVA_PAGO_PRODUCT = "prod_V9tJkcp307vgoZ";
	public static final String JAVA_PAGO_PRICE = "price_1U9ZXuRS56hFPP66CyqsBUi7";
	public static final String JAVA_SUB_PRODUCT = "prod_V9tJWoC67ZYO3Q";
	public static final String JAVA_SUB_PRICE = "price_1U9ZXvRS56hFPP66Qn70qI7o";
	public static final String GURU_JAVA = "java";
	public static final String DEFAULT_RETURN_URL = "http://localhost:8080/firewall/billing/v1/embedded-return?session_id={CHECKOUT_SESSION_ID}";

	private static final String API = "https://api.stripe.com/v1";

	public String secretKey() {
		return envOrProperty("STRIPE_SECRET_KEY");
	}

	public String publishableKey() {
		return envOrProperty("STRIPE_PUBLISHABLE_KEY");
	}

	public String webhookSecret() {
		return envOrProperty("STRIPE_WEBHOOK_SECRET");
	}

	private String envOrProperty(String name) {
		String key = System.getenv(name);
		if (StringUtils.isBlank(key))
			key = System.getProperty(name);
		return key == null ? "" : key.trim();
	}

	public boolean isLiveKey() {
		return secretKey().startsWith("sk_live_");
	}

	public boolean isTestKeyPresent() {
		String key = secretKey();
		return StringUtils.isNotBlank(key) && key.startsWith("sk_test_");
	}

	public boolean isLivePublishableKey() {
		return publishableKey().startsWith("pk_live_");
	}

	public boolean isTestPublishableKey() {
		String key = publishableKey();
		return StringUtils.isNotBlank(key) && key.startsWith("pk_test_");
	}

	public String priceForTraining(String trainingId) {
		if (JAVA_PAGO_TRAINING_ID.equals(trainingId))
			return JAVA_PAGO_PRICE;
		return null;
	}

	public PurchaseVO findPaidTraining(String userId, String trainingId) {
		if (!isTestKeyPresent() || StringUtils.isBlank(userId) || StringUtils.isBlank(trainingId))
			return null;
		try {
			String query = "metadata[\"trainingId\"]:\"" + trainingId + "\" AND metadata[\"userId\"]:\"" + userId
					+ "\" AND payment_status:\"paid\"";
			String url = API + "/checkout/sessions/search?query="
					+ URLEncoder.encode(query, StandardCharsets.UTF_8.name()) + "&limit=1";
			JsonObject root = getJson(url);
			if (root == null)
				return null;
			JsonArray data = root.getAsJsonArray("data");
			if (data == null || data.size() == 0)
				return null;
			return purchaseFromSession(data.get(0).getAsJsonObject(), userId, trainingId);
		} catch (Exception e) {
			return null;
		}
	}

	public JsonObject createTrainingSession(String userId, String trainingId, String email, String returnUrl) {
		String price = priceForTraining(trainingId);
		if (StringUtils.isBlank(price))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Este treinamento pago ainda não tem preço Stripe mapeado.");
		MultiValueMap<String, String> form = baseEmbeddedForm("payment", price, userId, email, returnUrl);
		form.add("metadata[trainingId]", trainingId);
		form.add("metadata[userId]", userId);
		return postForm("/checkout/sessions", form);
	}

	public JsonObject createJavaSubscriptionSession(String userId, String email, String returnUrl) {
		MultiValueMap<String, String> form = baseEmbeddedForm("subscription", JAVA_SUB_PRICE, userId, email, returnUrl);
		form.add("metadata[userId]", userId);
		form.add("metadata[guruId]", GURU_JAVA);
		form.add("subscription_data[metadata][userId]", userId);
		form.add("subscription_data[metadata][guruId]", GURU_JAVA);
		return postForm("/checkout/sessions", form);
	}

	public JsonObject retrieveSession(String sessionId) {
		if (StringUtils.isBlank(sessionId) || !sessionId.startsWith("cs_"))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o sessionId da Stripe.");
		return getJson(API + "/checkout/sessions/" + urlSeg(sessionId));
	}

	public boolean verifyWebhookSignature(String payload, String sigHeader) {
		String secret = webhookSecret();
		if (StringUtils.isBlank(secret) || StringUtils.isBlank(payload) || StringUtils.isBlank(sigHeader))
			return false;
		String timestamp = null;
		java.util.List<String> v1s = new java.util.ArrayList<>();
		for (String part : sigHeader.split(",")) {
			String[] kv = part.split("=", 2);
			if (kv.length != 2)
				continue;
			if ("t".equals(kv[0].trim()))
				timestamp = kv[1].trim();
			else if ("v1".equals(kv[0].trim()))
				v1s.add(kv[1].trim());
		}
		if (StringUtils.isBlank(timestamp) || v1s.isEmpty())
			return false;
		try {
			long t = Long.parseLong(timestamp);
			if (Math.abs(System.currentTimeMillis() / 1000L - t) > 300)
				return false;
			String signed = timestamp + "." + payload;
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			String expected = hex(mac.doFinal(signed.getBytes(StandardCharsets.UTF_8)));
			byte[] expectedBytes = expected.getBytes(StandardCharsets.US_ASCII);
			for (String found : v1s) {
				byte[] foundBytes = found.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII);
				if (expectedBytes.length == foundBytes.length && MessageDigest.isEqual(expectedBytes, foundBytes))
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public String resolveReturnUrl(String requested, String hostHeader, String proto) {
		if (isAllowedReturnUrl(requested))
			return requested.trim();
		String host = hostHeader == null ? "" : hostHeader.trim();
		String scheme = StringUtils.isBlank(proto) ? "http" : proto.trim();
		if (isAllowedHost(host)) {
			if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))
				scheme = "http";
			return scheme + "://" + host + "/firewall/billing/v1/embedded-return?session_id={CHECKOUT_SESSION_ID}";
		}
		return DEFAULT_RETURN_URL;
	}

	public boolean isAllowedReturnUrl(String url) {
		if (StringUtils.isBlank(url))
			return false;
		String trimmed = url.trim();
		if (trimmed.regionMatches(true, 0, "file:", 0, 5))
			return false;
		try {
			URI uri = URI.create(trimmed);
			String scheme = uri.getScheme();
			if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))
				return false;
			return isAllowedHost(uri.getHost());
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isAllowedHost(String host) {
		if (StringUtils.isBlank(host))
			return false;
		String h = host.trim().toLowerCase(Locale.ROOT);
		int colon = h.lastIndexOf(':');
		if (colon > 0 && h.indexOf(']') < 0 && h.substring(colon + 1).chars().allMatch(Character::isDigit))
			h = h.substring(0, colon);
		if (h.startsWith("[") && h.endsWith("]"))
			h = h.substring(1, h.length() - 1);
		if ("localhost".equals(h) || "127.0.0.1".equals(h) || "::1".equals(h) || "0:0:0:0:0:0:0:1".equals(h))
			return true;
		if ("eusouprogramadorjunior.com".equals(h) || h.endsWith(".eusouprogramadorjunior.com"))
			return true;
		if ("digitusforum.com".equals(h) || h.endsWith(".digitusforum.com"))
			return true;
		return false;
	}

	public PurchaseVO purchaseFromSession(JsonObject session, String userId, String trainingId) {
		PurchaseVO vo = new PurchaseVO();
		vo.setUserId(userId);
		vo.setTrainingId(trainingId);
		vo.setStatus("paid");
		vo.setHasPurchase(true);
		String id = stripeId(session, "id");
		if (id != null)
			vo.setStripeCheckoutSessionId(id);
		String pi = stripeId(session, "payment_intent");
		if (pi != null)
			vo.setStripePaymentIntentId(pi);
		return vo;
	}

	public SubscriptionVO subscriptionFromSession(JsonObject session, String userId) {
		SubscriptionVO vo = new SubscriptionVO();
		vo.setUserId(userId);
		vo.setGuruId(GURU_JAVA);
		vo.setScope("guru");
		vo.setStatus("active");
		String customer = stripeId(session, "customer");
		if (customer != null)
			vo.setStripeCustomerId(customer);
		String sub = stripeId(session, "subscription");
		if (sub != null)
			vo.setStripeSubscriptionId(sub);
		return vo;
	}

	public String metadata(JsonObject obj, String key) {
		if (obj == null || !obj.has("metadata") || !obj.get("metadata").isJsonObject())
			return "";
		JsonObject meta = obj.getAsJsonObject("metadata");
		if (!meta.has(key) || meta.get(key).isJsonNull())
			return "";
		return meta.get(key).getAsString();
	}

	public String stripeId(JsonObject obj, String field) {
		if (obj == null || !obj.has(field) || obj.get(field).isJsonNull())
			return null;
		JsonElement el = obj.get(field);
		if (el.isJsonPrimitive())
			return el.getAsString();
		if (el.isJsonObject() && el.getAsJsonObject().has("id") && !el.getAsJsonObject().get("id").isJsonNull())
			return el.getAsJsonObject().get("id").getAsString();
		return null;
	}

	public String text(JsonObject obj, String field) {
		if (obj == null || !obj.has(field) || obj.get(field).isJsonNull())
			return "";
		JsonElement el = obj.get(field);
		return el.isJsonPrimitive() ? el.getAsString() : "";
	}

	private MultiValueMap<String, String> baseEmbeddedForm(String mode, String price, String userId, String email,
			String returnUrl) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("ui_mode", "embedded");
		form.add("mode", mode);
		form.add("line_items[0][price]", price);
		form.add("line_items[0][quantity]", "1");
		form.add("payment_method_types[0]", "card");
		form.add("client_reference_id", userId);
		form.add("return_url", returnUrl);
		if (StringUtils.isNotBlank(email))
			form.add("customer_email", email);
		return form;
	}

	private JsonObject postForm(String path, MultiValueMap<String, String> form) {
		if (isLiveKey())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Chave live da Stripe é proibida. Use só sk_test_.");
		if (!isTestKeyPresent())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"As chaves de teste da Stripe ainda não estão ligadas. Não é possível iniciar o checkout.");
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(secretKey());
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			ResponseEntity<String> response = rest().exchange(API + path, HttpMethod.POST,
					new HttpEntity<>(form, headers), String.class);
			if (response.getBody() == null)
				throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
						"A Stripe não devolveu a sessão. Não cobramos nada.");
			return JsonParser.parseString(response.getBody()).getAsJsonObject();
		} catch (ResponseStatusException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"A Stripe recusou a sessão de checkout. Não cobramos nada.");
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Não foi possível falar com a Stripe. Não cobramos nada.");
		}
	}

	private JsonObject getJson(String url) {
		if (isLiveKey())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Chave live da Stripe é proibida. Use só sk_test_.");
		if (!isTestKeyPresent())
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"As chaves de teste da Stripe ainda não estão ligadas. Não é possível iniciar o checkout.");
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(secretKey());
			ResponseEntity<String> response = rest().exchange(url, HttpMethod.GET, new HttpEntity<>(headers),
					String.class);
			if (response.getBody() == null)
				return null;
			return JsonParser.parseString(response.getBody()).getAsJsonObject();
		} catch (ResponseStatusException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Não foi possível ler a sessão na Stripe.");
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Não foi possível falar com a Stripe. Não cobramos nada.");
		}
	}

	private RestTemplate rest() {
		RestTemplate restTemplate = new RestTemplate();
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(15000);
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(15000);
		return restTemplate;
	}

	private static String urlSeg(String raw) {
		return URLEncoder.encode(raw, StandardCharsets.UTF_8).replace("+", "%20");
	}

	private static String hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}

}
