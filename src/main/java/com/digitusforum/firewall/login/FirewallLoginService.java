package com.digitusforum.firewall.login;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitusforum.firewall.emailVerification.FirewallEmailVerificationVO;
import com.digitusforum.firewall.util.M;
import com.digitusforum.firewall.util.MicroservicesURLs;
import com.digitusforum.firewall.util.RequestService;
import com.digitusforum.firewall.util.ThrowService;
import com.digitusforum.firewall.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FirewallLoginService {
	@Autowired
	private RequestService requestService;
	private Map<String, TokenVO> creationCache = new HashMap<>();
	private Map<String, TokenVO> uuidCache = new HashMap<>();
	private int expirationInSeconds = 369000;
	
	public TokenVO createToken(FirewallEmailVerificationVO firewallEmailVerificationVO, String locale) throws JsonMappingException, JsonProcessingException {
		TokenVO tokenVO = new TokenVO();
		tokenVO.setEmail(firewallEmailVerificationVO.getEmail());
		tokenVO.setPassword(firewallEmailVerificationVO.getPassword());
		return createToken(tokenVO, locale);
	}

	/** REGRA-AUTH-4 / NÃO-PASSWORD: UUID no cache da borda após EV-OK. Sem senha. Sem login MS. */
	public TokenVO issueUuidAfterEmailCode(String email, String userId, String locale) {
		if (StringUtils.isBlank(email))
			throw ThrowService.doIt(locale, 503, M.LOGIN_MISSING_EMAIL);

		TokenVO tokenVO = new TokenVO();
		tokenVO.setEmail(email);
		tokenVO.setUserId(userId);
		tokenVO.setToken(java.util.UUID.randomUUID().toString());
		tokenVO.setTokenType("uuid");
		tokenVO.setCreatedIn(ZonedDateTime.now());
		tokenVO.setPassword(null);
		uuidCache.put(tokenVO.getToken(), tokenVO);
		return tokenVO;
	}
	
	public TokenVO createToken(TokenVO tokenVO, String locale) throws JsonMappingException, JsonProcessingException {
		if (StringUtils.isBlank(tokenVO.getEmail()))
			throw ThrowService.doIt(locale, 503, M.LOGIN_MISSING_EMAIL);

		if (StringUtils.isBlank(tokenVO.getPassword()))
			throw ThrowService.doIt(locale, 503, M.LOGIN_MISSING_PASSWORD);

		if (!cacheContains(tokenVO) || !stillValid(tokenVO)) {
			checkLoginMS(locale);
			String jsonResponse = requestService.request(MicroservicesURLs.LOGIN_CREATE_TOKEN, tokenVO, locale);
			tokenVO = Util.getMapper().map(new ObjectMapper().readTree(jsonResponse), TokenVO.class);
			tokenVO.setCreatedIn(ZonedDateTime.now());
			updateCache(tokenVO);
		}
		return getFromCache(tokenVO);
	}

	public TokenVO validateToken(String authorization, String locale) {
		String[] tokenData = authorization.split(" ");
		if (tokenData.length != 2)
			throw ThrowService.doIt(locale, 503, M.LOGIN_INVALID_TOKEN);

		String token = tokenData[1];

		if (cacheContains(token) && stillValid(token))
			return getFromCache(token);

		if (cacheContains(token) && !stillValid(token)) {
			removeFromCache(token);
			throw ThrowService.doIt(locale, 503, M.LOGIN_EXPIRED_TOKEN);
		}
		throw ThrowService.doIt(locale, 503, M.LOGIN_INVALID_TOKEN);
	}

	private boolean cacheContains(TokenVO tokenVO) {
		return creationCache.containsKey(tokenVO.getEmail()+tokenVO.getPassword());
	}

	private boolean cacheContains(String token) {
		return uuidCache.containsKey(token);
	}

	private boolean stillValid(TokenVO tokenVO) {
		if (uuidCache.containsKey(tokenVO.getEmail()+tokenVO.getPassword())) {
			tokenVO = creationCache.get(tokenVO.getEmail()+tokenVO.getPassword());
			long tokenAgeInSeconds = Duration.between(tokenVO.getCreatedIn(), ZonedDateTime.now()).getSeconds();
			return tokenAgeInSeconds < expirationInSeconds;
		}
		return false;
	}

	private boolean stillValid(String token) {
		if (uuidCache.containsKey(token)) {
			long tokenAgeInSeconds = Duration.between(uuidCache.get(token).getCreatedIn(), ZonedDateTime.now()).getSeconds();
			return tokenAgeInSeconds < expirationInSeconds;
		}
		return false;
	}

	private void removeFromCache(String token) {
		if (uuidCache.containsKey(token))
			uuidCache.remove(token);
	}

	private void updateCache(TokenVO tokenVO) {
		creationCache.put(tokenVO.getEmail()+tokenVO.getPassword(), tokenVO);
		uuidCache.put(tokenVO.getToken(), tokenVO);
	}

	private TokenVO getFromCache(TokenVO tokenVO) {
		tokenVO = creationCache.get(tokenVO.getEmail()+tokenVO.getPassword());
		long tokenAgeInSeconds = Duration.between(tokenVO.getCreatedIn(), ZonedDateTime.now()).getSeconds();
		tokenVO.setStillValidForSeconds(expirationInSeconds - tokenAgeInSeconds);
		tokenVO.setPassword(null);
		return tokenVO;
	}

	private TokenVO getFromCache(String token) {
		TokenVO tokenVO = uuidCache.get(token);
		long tokenAgeInSeconds = Duration.between(tokenVO.getCreatedIn(), ZonedDateTime.now()).getSeconds();
		tokenVO.setStillValidForSeconds(expirationInSeconds - tokenAgeInSeconds);
		return tokenVO;
	}

	private void checkLoginMS(String locale) {
		if (!requestService.isUp(MicroservicesURLs.LOGIN))
			throw ThrowService.doIt(locale, 503, M.LOGIN_MICROSERVICE_OFFLINE);
	}

}
