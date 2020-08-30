package com.auctions.repository;

import org.javatuples.Pair;

import com.aerospike.client.AerospikeException;

public interface AuctionableItemRepository {

	void save(AuctionableItem auctionableItem) throws AerospikeException;

	Pair<Boolean, Float> isValidAuction(String itemCode) throws AerospikeException;

}
