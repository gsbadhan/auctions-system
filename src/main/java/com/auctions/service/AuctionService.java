package com.auctions.service;

import com.auctions.pojo.BidsFeedResponse;

public interface AuctionService {
	public enum Status {
		ACCEPTED, REJECTED, NOT_FOUND;
	}

	Status placeBidPrice(String itemCode, Float amount);

	void auctionFeed(BidsFeedResponse feedResponse);
}
