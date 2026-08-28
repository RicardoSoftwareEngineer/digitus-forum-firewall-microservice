package com.digitusforum.firewall.billing;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class StripeTestClient {

	public String secretKey() {
		String key = System.getenv("STRIPE_SECRET_KEY");
		if (StringUtils.isBlank(key))
			key = System.getProperty("STRIPE_SECRET_KEY");
		return key == null ? "" : key.trim();
	}

	public boolean isLiveKey() {
		return secretKey().startsWith("sk_live_");
	}

	public boolean isTestKeyPresent() {
		String key = secretKey();
		return StringUtils.isNotBlank(key) && key.startsWith("sk_test_");
	}

	public PurchaseVO findPaidTraining(String userId, String trainingId) {
		if (!isTestKeyPresent() || StringUtils.isBlank(userId) || StringUtils.isBlank(trainingId))
			return null;
		try {
			String query = "metadata[\"trainingId\"]:\"" + trainingId + "\" AND metadata[\"userId\"]:\"" + userId
					+ "\" AND payment_status:\"paid\"";
			String url = "https://api.stripe.com/v1/checkout/sessions/search?query="
					+ URLEncoder.encode(query, StandardCharsets.UTF_8.name()) + "&limit=1";
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(secretKey());
			RestTemplate restTemplate = new RestTemplate();
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(8000);
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(8000);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers),
					String.class);
			if (response.getBody() == null)
				return null;
			JsonObject root = JsonParser.parseString(response.getBody()).getAsJsonObject();
			JsonArray data = root.getAsJsonArray("data");
			if (data == null || data.size() == 0)
				return null;
			JsonObject session = data.get(0).getAsJsonObject();
			PurchaseVO vo = new PurchaseVO();
			vo.setUserId(userId);
			vo.setTrainingId(trainingId);
			vo.setStatus("paid");
			vo.setHasPurchase(true);
			if (session.has("id") && !session.get("id").isJsonNull())
				vo.setStripeCheckoutSessionId(session.get("id").getAsString());
			JsonElement pi = session.get("payment_intent");
			if (pi != null && pi.isJsonPrimitive())
				vo.setStripePaymentIntentId(pi.getAsString());
			return vo;
		} catch (Exception e) {
			return null;
		}
	}

}
