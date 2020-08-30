package com.auctions.pojo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BidsFeedResponse implements Serializable {
	private String itemCode;
	private Float HighestBidAmount;
	private Float stepRate;

	public BidsFeedResponse() {
	}

	public BidsFeedResponse(String itemCode, Float highestBidAmount, Float stepRate) {
		super();
		this.itemCode = itemCode;
		HighestBidAmount = highestBidAmount;
		this.stepRate = stepRate;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public Float getHighestBidAmount() {
		return HighestBidAmount;
	}

	public void setHighestBidAmount(Float highestBidAmount) {
		HighestBidAmount = highestBidAmount;
	}

	public Float getStepRate() {
		return stepRate;
	}

	public void setStepRate(Float stepRate) {
		this.stepRate = stepRate;
	}

	@Override
	public String toString() {
		return "BidsFeedResponse [itemCode=" + itemCode + ", HighestBidAmount=" + HighestBidAmount + ", stepRate="
				+ stepRate + "]";
	}

}
