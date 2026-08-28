package com.digitusforum.firewall.i18;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.digitusforum.firewall.util.Headers;
import com.digitusforum.firewall.util.TimeService;
import com.digitusforum.firewall.util.Timeouts;

public class RequestServiceDEPRECATED {

	public boolean isUp(String endpoint) {
		String requestTimeId = TimeService.startCounting();
		endpoint += "/healthCheck";
		try {
			ResponseEntity<String> response = (ResponseEntity<String>) hitIt(endpoint);
		} catch (Exception e) {
			TimeService.persistElapsedTimeout(requestTimeId, endpoint);
			return false;
		}
		return true;
	}

	public Object hitIt(String endpoint) {
		return hitIt(endpoint, Timeouts.ideal, "", null);
	}

	public Object hitIt(String endpoint, Object requestEntityBody, String locale) {
		return hitIt(endpoint, Timeouts.ideal, requestEntityBody, Headers.DEFAULT(locale));
	}

	public Object hitIt(String endpoint, Object requestEntityBody, String locale, Class<?> responseType) {
		return hitIt(endpoint, Timeouts.ideal, requestEntityBody, Headers.DEFAULT(locale), responseType);
	}

	public Object hitIt(String endpoint, int timeout, Object requestEntityBody, MultiValueMap<String, String> headers) {
		return hitIt(endpoint, timeout, requestEntityBody, headers, requestEntityBody.getClass());
	}

	public Object hitIt(String endpoint, int timeout, Object requestEntityBody, MultiValueMap<String, String> headers,
			Class<?> responseType) {
		String requestTimeId = TimeService.startCounting();
		RestTemplate restTemplate = new RestTemplate();
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(timeout);
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(timeout);
		final HttpEntity<Object> entity = new HttpEntity<>(requestEntityBody, headers);
		Object response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, responseType);
		TimeService.persistElapsedTime(requestTimeId, endpoint);
		return response;
	}
}
