package com.auctions.repository;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AuctionableItemLock implements Serializable {
	private String code;
	private String lockId;
	private long savedTime;

	public AuctionableItemLock() {
	}

	public AuctionableItemLock(String code, String lockId) {
		super();
		this.code = code;
		this.lockId = lockId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLockId() {
		return lockId;
	}

	public void setLockId(String lockId) {
		this.lockId = lockId;
	}

	public long getSavedTime() {
		return savedTime;
	}

	public void setSavedTime(long savedTime) {
		this.savedTime = savedTime;
	}

}
