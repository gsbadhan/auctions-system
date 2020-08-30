package com.auctions.repository;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AuctionStatus implements Serializable {
	private String code;
	private Float highestBidAmount;

	public AuctionStatus() {
	}

	public AuctionStatus(String code, Float highestBidAmount) {
		super();
		this.code = code;
		this.highestBidAmount = highestBidAmount;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Float getHighestBidAmount() {
		return highestBidAmount;
	}

	public void setHighestBidAmount(Float highestBidAmount) {
		this.highestBidAmount = highestBidAmount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuctionStatus other = (AuctionStatus) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
