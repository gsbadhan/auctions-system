package com.auctions.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.auctions.service.AuctionService.Status;

@Repository
public class AuctionStatusRepositoryImpl implements AuctionStatusRepository {
	private AerospikeClient client;
	private String namespace = "auctions";
	private String set = "auction_status";

	@Autowired
	public AuctionStatusRepositoryImpl(@Qualifier("aerospikeClient") AerospikeClient client) {
		this.client = client;
	}

	@Override
	public Optional<AuctionStatus> get(String itemCode) throws AerospikeException {
		Key key = new Key(namespace, set, itemCode);
		Record record = client.get(client.getReadPolicyDefault(), key);
		if (record == null)
			return Optional.of(new AuctionStatus(itemCode, 0.0F));

		AuctionStatus auctionStatus = (AuctionStatus) record.getValue("data");
		return Optional.of(auctionStatus);
	}

	@Override
	public Status save(String itemCode, float amount) throws AerospikeException {
		WritePolicy wPolicy = new WritePolicy(client.getWritePolicyDefault());
		wPolicy.sendKey = true;
		wPolicy.expiration = -1;
		wPolicy.recordExistsAction = RecordExistsAction.REPLACE;
		AuctionStatus auctionStatus = new AuctionStatus(itemCode, amount);
		client.put(wPolicy, new Key(namespace, set, itemCode), new Bin("data", auctionStatus));
		return Status.ACCEPTED;
	}

}
