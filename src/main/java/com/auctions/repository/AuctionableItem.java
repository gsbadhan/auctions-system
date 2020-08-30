package com.auctions.repository;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AuctionableItem implements Serializable {
	private String code;
	private Long start;
	private Long end;
	private Float stepRate;

	public AuctionableItem() {
	}

	public AuctionableItem(String code, Long start, Long end, Float stepRate) {
		super();
		this.code = code;
		this.start = start;
		this.end = end;
		this.stepRate = stepRate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}

	public Float getStepRate() {
		return stepRate;
	}

	public void setStepRate(Float stepRate) {
		this.stepRate = stepRate;
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
		AuctionableItem other = (AuctionableItem) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
