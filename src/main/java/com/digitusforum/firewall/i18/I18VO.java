package com.digitusforum.firewall.i18;

public class I18VO {
	private String locale;
	private String key;
	private String message;

	public I18VO() {
	}

	public I18VO(String locale, String key) {
		this.locale = locale;
		this.key = key;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
