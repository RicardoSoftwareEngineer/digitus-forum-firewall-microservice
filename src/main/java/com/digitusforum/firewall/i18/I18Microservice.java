package com.digitusforum.firewall.i18;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.digitusforum.firewall.util.MicroservicesURLs;

//todo pllleaase make this in kotlin, we are flying so high, so fast and so sophisticated that i would say simple
public class I18Microservice {
	private Map<String, String> messagesCache = new HashMap<>();
	private RequestServiceDEPRECATED requestService;

	public I18Microservice(RequestServiceDEPRECATED requestService) {
		this.requestService = requestService;
	}

	public String getMessageByKey(I18VO i18) {
		String cacheKey = i18.getKey() + "." + i18.getLocale();
		if (messagesCache.get(cacheKey) != null) {
			updateCache(i18);
		} else if (requestService.isUp(MicroservicesURLs.I18)) {
			i18 = makeRequest(i18);
			messagesCache.put(cacheKey, i18.getMessage());
		}
		return messagesCache.get(cacheKey) != null ? messagesCache.get(cacheKey)
				: "message key 2 = " + i18.getKey();
	}

	public String getMessageByKey(String locale, String key) {
		return getMessageByKey(new I18VO(locale, key));
	}

	public void deleteCache() {
		messagesCache = new HashMap<>();
	}

	private I18VO makeRequest(I18VO i18) {
		return ((ResponseEntity<? extends I18VO>) requestService.hitIt(MicroservicesURLs.I18, i18, i18.getLocale()))
				.getBody();
	}

	private void updateCache(I18VO i18) {
		new Thread(() -> {
			if (requestService.isUp(MicroservicesURLs.I18)) {
				I18VO i18_response = ((ResponseEntity<? extends I18VO>) requestService.hitIt(MicroservicesURLs.I18, i18,
						i18.getLocale())).getBody();
				String cacheKey = i18.getKey() + "." + i18.getLocale();
				messagesCache.put(cacheKey, i18_response.getMessage());
			}
		}).start();
	}
}