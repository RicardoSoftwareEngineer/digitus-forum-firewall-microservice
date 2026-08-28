package com.digitusforum.firewall.billing;

import java.util.ArrayList;
import java.util.List;

public class BillingMeVO {
	private List<String> purchasedTrainingIds = new ArrayList<>();
	private boolean javaSubscriptionActive;

	public List<String> getPurchasedTrainingIds() {
		return purchasedTrainingIds;
	}

	public void setPurchasedTrainingIds(List<String> purchasedTrainingIds) {
		this.purchasedTrainingIds = purchasedTrainingIds;
	}

	public boolean isJavaSubscriptionActive() {
		return javaSubscriptionActive;
	}

	public void setJavaSubscriptionActive(boolean javaSubscriptionActive) {
		this.javaSubscriptionActive = javaSubscriptionActive;
	}

}
