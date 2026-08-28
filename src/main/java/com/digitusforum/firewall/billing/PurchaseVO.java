package com.digitusforum.firewall.billing;

public class PurchaseVO {
	private String purchaseId;
	private String userId;
	private String trainingId;
	private String stripeCheckoutSessionId;
	private String stripePaymentIntentId;
	private String status;
	private boolean hasPurchase;

	public String getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(String purchaseId) {
		this.purchaseId = purchaseId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}

	public String getStripeCheckoutSessionId() {
		return stripeCheckoutSessionId;
	}

	public void setStripeCheckoutSessionId(String stripeCheckoutSessionId) {
		this.stripeCheckoutSessionId = stripeCheckoutSessionId;
	}

	public String getStripePaymentIntentId() {
		return stripePaymentIntentId;
	}

	public void setStripePaymentIntentId(String stripePaymentIntentId) {
		this.stripePaymentIntentId = stripePaymentIntentId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isHasPurchase() {
		return hasPurchase;
	}

	public void setHasPurchase(boolean hasPurchase) {
		this.hasPurchase = hasPurchase;
	}

}
