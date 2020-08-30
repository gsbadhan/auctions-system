package com.auctions.repository;

import java.util.Optional;

import com.aerospike.client.AerospikeException;
import com.auctions.service.AuctionService.Status;

public interface AuctionStatusRepository {

	Optional<AuctionStatus> get(String itemCode) throws AerospikeException;

	Status save(String itemCode, float amount) throws AerospikeException;

}
