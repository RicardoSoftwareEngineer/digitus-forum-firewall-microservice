package com.digitusforum.firewall.background;

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
public class BackgroundRequestService {

	@Autowired
	private RequestService requestService;

	private void checkUserMS(String locale) {
		if (!requestService.isUp(MicroservicesURLs.USER))
			throw ThrowService.doIt(locale, 503, M.USER_MICROSERVICE_OFFLINE);
	}

	public BackgroundSaveVO save(BackgroundSaveVO body, String locale) {
		checkUserMS(locale);
		String json = requestService.request(MicroservicesURLs.USER_BACKGROUND_SAVE, body, locale);
		return new Gson().fromJson(json, BackgroundSaveVO.class);
	}

	public List<BackgroundSaveVO> list(String userId, String locale) {
		checkUserMS(locale);
		BackgroundSaveVO body = new BackgroundSaveVO();
		body.setUserId(userId);
		String json = requestService.request(MicroservicesURLs.USER_BACKGROUND_BY_USER, body, locale);
		BackgroundSaveVO[] rows = new Gson().fromJson(json, BackgroundSaveVO[].class);
		List<BackgroundSaveVO> out = new ArrayList<>();
		if (rows != null) {
			for (int i = 0; i < rows.length; i++)
				out.add(rows[i]);
		}
		return out;
	}

	public BackgroundSaveVO select(BackgroundSaveVO body, String locale) {
		checkUserMS(locale);
		String json = requestService.request(MicroservicesURLs.USER_BACKGROUND_SELECT, body, locale);
		return new Gson().fromJson(json, BackgroundSaveVO.class);
	}

	public BackgroundSaveVO setAuto(String userId, String locale) {
		checkUserMS(locale);
		BackgroundSaveVO body = new BackgroundSaveVO();
		body.setUserId(userId);
		String json = requestService.request(MicroservicesURLs.USER_BACKGROUND_SET_AUTO, body, locale);
		return new Gson().fromJson(json, BackgroundSaveVO.class);
	}

	public BackgroundSaveVO prefs(String userId, String locale) {
		checkUserMS(locale);
		BackgroundSaveVO body = new BackgroundSaveVO();
		body.setUserId(userId);
		String json = requestService.request(MicroservicesURLs.USER_BACKGROUND_PREFS, body, locale);
		return new Gson().fromJson(json, BackgroundSaveVO.class);
	}

}
