package com.digitusforum.firewall.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.firewall.training.FirewallTrainingVO;
import com.digitusforum.firewall.i18.I18VO;
import com.digitusforum.firewall.login.TokenVO;
import com.digitusforum.firewall.module.ModuleVO;
import com.google.gson.Gson;

@Service
public class RequestService {
	private Map<String, String> i18MessagesCache = new HashMap<>();

	private void checkUserMS(String locale) {
		if (!isUp(MicroservicesURLs.USER))
			throw ThrowService.doIt(locale, 503, M.USER_MICROSERVICE_OFFLINE);
	}

	private void checkLoginMS(String locale) {
		if (!isUp(MicroservicesURLs.LOGIN))
			throw ThrowService.doIt(locale, 503, M.LOGIN_MICROSERVICE_OFFLINE);
	}

	private void checkTrainingMS(String locale) {
		if (!isUp(MicroservicesURLs.TRAINING))
			throw ThrowService.doIt(locale, 503, M.TRAINING_MICROSERVICE_OFFLINE);
	}

	public FirewallTrainingVO createTraining(FirewallTrainingVO trainingVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = request(MicroservicesURLs.TRAINING_CREATE, trainingVO, locale);
		FirewallTrainingVO training = new Gson().fromJson(jsonResponse, FirewallTrainingVO.class);
		return training;
	}
	
	public FirewallTrainingVO delete(FirewallTrainingVO trainingVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = request(MicroservicesURLs.TRAINING_DELETE, trainingVO, locale);
		FirewallTrainingVO training = new Gson().fromJson(jsonResponse, FirewallTrainingVO.class);
		return training;
	}

	public List<FirewallTrainingVO> retrieveTrainings(String locale) {
		checkTrainingMS(locale);
		String jsonResponse = request(MicroservicesURLs.TRAINING_RETRIEVE_ALL, locale);
		List<FirewallTrainingVO> trainings = new Gson().fromJson(jsonResponse, List.class);
		return trainings;
	}

	public FirewallTrainingVO retrieveById(FirewallTrainingVO trainingVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = request(MicroservicesURLs.TRAINING_RETRIEVE_BY_ID, trainingVO, locale);
		FirewallTrainingVO training = new Gson().fromJson(jsonResponse, FirewallTrainingVO.class);
		return training;
	}

	public FirewallTrainingVO retrieveCatalogById(FirewallTrainingVO trainingVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = request(MicroservicesURLs.TRAINING_RETRIEVE_CATALOG_BY_ID, trainingVO, locale);
		FirewallTrainingVO training = new Gson().fromJson(jsonResponse, FirewallTrainingVO.class);
		return training;
	}
	
	public FirewallTrainingVO retrieveSubjectsByTrainingId(FirewallTrainingVO trainingVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = request(MicroservicesURLs.TRAINING_RETRIEVE_SUBJECTS_BY_TRAINING_ID, trainingVO, locale);
		FirewallTrainingVO training = new Gson().fromJson(jsonResponse, FirewallTrainingVO.class);
		return training;
	}

	public List<ModuleVO> retrieveModulesWithVideosByTrainingId(FirewallTrainingVO trainingVO, String locale) {
		checkTrainingMS(locale);
		String jsonResponse = request(MicroservicesURLs.TRAINING_RETRIEVE_MODULES_WITH_VIDEOS_BY_TRAINING_ID, trainingVO,
				locale);
		List<ModuleVO> modules = new Gson().fromJson(jsonResponse, List.class);
		return modules;
	}

	public TokenVO createToken(TokenVO tokenVO, String locale) {
		checkLoginMS(locale);
		String jsonResponse = request(MicroservicesURLs.LOGIN_CREATE_TOKEN, tokenVO, locale);
		tokenVO = new Gson().fromJson(jsonResponse, TokenVO.class);
		return tokenVO;
	}

	

	

	public boolean isUp(String endpoint) {
		String requestTimeId = TimeService.startCounting();
		try {
			request(endpoint + "/healthCheck");
		} catch (Exception e) {
			TimeService.persistElapsedTimeout(requestTimeId, endpoint);
			return false;
		}
		return true;
	}

	public String request(String endpoint) {
		return request(endpoint, Timeouts.debug, "", null, "");
	}

	public String request(String endpoint, String locale) {
		return request(endpoint, Timeouts.debug, "", Headers.DEFAULT(locale), locale);
	}

	public String request(String endpoint, Object requestEntityBody, String locale) {
		return request(endpoint, Timeouts.debug, requestEntityBody, Headers.DEFAULT(locale), locale);
	}

	//TODO separate the firewall.requestService
	public String createUser(String endpoint, Object userVO, String locale) {
		checkUserMS(locale);
		return request(MicroservicesURLs.USER_CREATE, userVO, locale);
	}
	
	//TODO this file looks horrible wrong, look at this ASAP
	//TODO why not use the endpoint?
	public String chat(String endpoint, Object chatVO, String locale) {
		checkUserMS(locale);
		return request(MicroservicesURLs.CHAT, chatVO, locale);
	}
	
	public String sup(String endpoint, Object chatVO, String locale) {
		checkUserMS(locale);
		return request(MicroservicesURLs.SUP, chatVO, locale);
	}
	
	public String conversations(String endpoint, Object chatVO, String locale) {
		checkUserMS(locale);
		return request(MicroservicesURLs.CONVERSATIONS, chatVO, locale);
	}
	
	public String conversation(String endpoint, Object chatVO, String locale) {
		checkUserMS(locale);
		return request(MicroservicesURLs.CONVERSATION, chatVO, locale);
	}

	public String request(String endpoint, int timeout, Object requestEntityBody, MultiValueMap<String, String> headers,
			String locale) {
		try {
			String requestTimeId = TimeService.startCounting();
			RestTemplate restTemplate = new RestTemplate();
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(timeout);
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(timeout);
			final HttpEntity<Object> entity = new HttpEntity<>(requestEntityBody, headers);
			ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
			TimeService.persistElapsedTime(requestTimeId, endpoint);
			return response.getBody();
		} catch (HttpClientErrorException e) {
			String errorMessage = e.getMessage().replace("[", "").replace("]", "").substring(6);
			ErrorMessageVO errorMessageVO = new Gson().fromJson(errorMessage, ErrorMessageVO.class);
			System.out.println("loopinfinito");
			String i18Message = getMessageByKey(locale, errorMessageVO.getMessage());
			throw new ResponseStatusException(e.getStatusCode(), i18Message);
		}
	}

	public boolean captchaIsValid(String captchaToken){
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();
		String recaptchaSecret = System.getenv("RECAPTCHA_SECRET");
		if (recaptchaSecret == null || recaptchaSecret.isEmpty()) {
			return false;
		}
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("secret", recaptchaSecret)
				.addFormDataPart("response", captchaToken)
				.build();
		Request request = new Request.Builder()
				.url("https://www.google.com/recaptcha/api/siteverify")
				.method("POST", body)
				.build();
		try {
			Response response = client.newCall(request).execute();
			String resp = response.body().string();
			if(resp.contains("true")){
				return true;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public String getMessageByKey(String locale, String key) {
		I18VO i18 = new I18VO(locale, key);
		String cacheKey = i18.getKey() + "." + i18.getLocale();
		updateCache(i18, cacheKey);
		return i18MessagesCache.get(cacheKey) != null ? i18MessagesCache.get(cacheKey)
				: "message key 3 = " + i18.getKey();
	}

	private void updateCache(I18VO i18, String cacheKey) {
		if (i18MessagesCache.get(cacheKey) != null) {
			final I18VO i18_final = i18;
			new Thread(() -> {
				if (isUp(MicroservicesURLs.I18)) {
					I18VO i18_response = makeRequest(i18_final);
					i18MessagesCache.put(cacheKey, i18_response.getMessage());
				}
			}).start();
		} else if (isUp(MicroservicesURLs.I18)) {
			i18 = makeRequest(i18);
			i18MessagesCache.put(cacheKey, i18.getMessage());
		}
	}

	private I18VO makeRequest(I18VO i18) {
		String url = MicroservicesURLs.I18;
		String jsonResponse = request(url, i18, i18.getLocale());
		return new Gson().fromJson(jsonResponse, I18VO.class);
	}
}
