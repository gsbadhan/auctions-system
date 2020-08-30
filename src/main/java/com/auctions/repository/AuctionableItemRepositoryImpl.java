package com.auctions.repository;

import java.util.Date;

import org.javatuples.Pair;
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

@Repository
public class AuctionableItemRepositoryImpl implements AuctionableItemRepository {
	private AerospikeClient client;
	private String namespace = "auctions";
	private String set = "auctionable_item";

	@Autowired
	public AuctionableItemRepositoryImpl(@Qualifier("aerospikeClient") AerospikeClient client) {
		this.client = client;
	}

	@Override
	public Pair<Boolean, Float> isValidAuction(String itemCode) throws AerospikeException {
		Key key = new Key(namespace, set, itemCode);
		Record record = client.get(client.getReadPolicyDefault(), key);
		if (record == null)
			return Pair.with(false, 0.0F);

		AuctionableItem auctionableItem = (AuctionableItem) record.getValue("data");
		long today = new Date().getTime();
		if (!(today >= auctionableItem.getStart() && today <= auctionableItem.getEnd())) {
			return Pair.with(false, 0.0F);
		}

		return Pair.with(true, auctionableItem.getStepRate());
	}

	@Override
	public void save(AuctionableItem auctionableItem) throws AerospikeException {
		WritePolicy wPolicy = new WritePolicy(client.getWritePolicyDefault());
		wPolicy.sendKey = true;
		wPolicy.expiration = -1;
		wPolicy.recordExistsAction = RecordExistsAction.REPLACE;
		client.put(wPolicy, new Key(namespace, set, auctionableItem.getCode()), new Bin("data", auctionableItem));
	}

}
