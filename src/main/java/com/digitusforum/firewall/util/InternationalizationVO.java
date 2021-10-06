package com.digitusforum.firewall.util;

public class InternationalizationVO {
    private String locale;
    private String key;
    private String message;

    public InternationalizationVO() {
    }

    public InternationalizationVO(String locale, String key) {
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
