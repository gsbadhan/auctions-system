package com.auctions.pojo;

public class BidRequest {
	private Float amount;

	public BidRequest() {
	}

	public BidRequest(Float amount) {
		super();
		this.amount = amount;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "BidRequest [amount=" + amount + "]";
	}

}
