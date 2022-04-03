package com.digitusforum.firewall.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.digitusforum.firewall.course.CourseVO;
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

	private void checkCourseMS(String locale) {
		if (!isUp(MicroservicesURLs.COURSE))
			throw ThrowService.doIt(locale, 503, M.COURSE_MICROSERVICE_OFFLINE);
	}

	public CourseVO createCourse(CourseVO courseVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = request(MicroservicesURLs.COURSE_CREATE, courseVO, locale);
		CourseVO course = new Gson().fromJson(jsonResponse, CourseVO.class);
		return course;
	}
	
	public CourseVO delete(CourseVO courseVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = request(MicroservicesURLs.COURSE_DELETE, courseVO, locale);
		CourseVO course = new Gson().fromJson(jsonResponse, CourseVO.class);
		return course;
	}

	public List<CourseVO> retrieveCourses(String locale) {
		checkCourseMS(locale);
		String jsonResponse = request(MicroservicesURLs.COURSE_RETRIEVE_ALL, locale);
		List<CourseVO> courses = new Gson().fromJson(jsonResponse, List.class);
		return courses;
	}

	public CourseVO retrieveById(CourseVO courseVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = request(MicroservicesURLs.COURSE_RETRIEVE_BY_ID, courseVO, locale);
		CourseVO course = new Gson().fromJson(jsonResponse, CourseVO.class);
		return course;
	}
	
	public CourseVO retrieveSubjectsByCourseId(CourseVO courseVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = request(MicroservicesURLs.COURSE_RETRIEVE_SUBJECTS_BY_COURSE_ID, courseVO, locale);
		CourseVO course = new Gson().fromJson(jsonResponse, CourseVO.class);
		return course;
	}

	public List<ModuleVO> retrieveModulesWithVideosByCourseId(CourseVO courseVO, String locale) {
		checkCourseMS(locale);
		String jsonResponse = request(MicroservicesURLs.COURSE_RETRIEVE_MODULES_WITH_VIDEOS_BY_COURSE_ID, courseVO,
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

	public TokenVO validateToken(String authorization, String locale) {
		return null;
		/*String[] tokenData = authorization.split(" ");
		if (tokenData.length != 2)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, M.LOGIN_INVALID_TOKEN);
		TokenVO tokenVO = new TokenVO();
		tokenVO.setToken(tokenData[1]);
		tokenVO.setTokenType(tokenData[0]);
		return validateToken(tokenVO, locale);*/
	}

	public TokenVO validateToken(TokenVO tokenVO, String locale) {
		checkLoginMS(locale);

		String jsonResponse = request(MicroservicesURLs.LOGIN_VALIDATE_TOKEN, tokenVO, locale);
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

	public String createUser(String endpoint, Object userVO, String locale) {
		checkUserMS(locale);
		return request(MicroservicesURLs.USER_CREATE, userVO, locale);
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

	public String getMessageByKey(String locale, String key) {
		I18VO i18 = new I18VO(locale, key);
		String cacheKey = i18.getKey() + "." + i18.getLocale();
		updateCache(i18, cacheKey);
		return i18MessagesCache.get(cacheKey) != null ? i18MessagesCache.get(cacheKey)
				: "Internationalization service is down, sorry for the inconvenience - message key = " + i18.getKey();
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
