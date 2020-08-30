package com.auctions.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;

@Repository
public class AuctionableItemLockRepositoryImpl implements AuctionableItemLockRepository {
	private AerospikeClient client;
	private String namespace = "auctions";
	private String set = "auctionable_item_lock";
	private int lockTTL;
	private int lockRetries;
	private int lockRetriesTimeGap;

	@Autowired
	public AuctionableItemLockRepositoryImpl(@Qualifier("aerospikeClient") AerospikeClient client, Environment env) {
		this.client = client;
		lockTTL = Integer.parseInt(env.getProperty("aerospike.itemcode.lock.ttl"));
		lockRetries = Integer.parseInt(env.getProperty("aerospike.itemcode.lock.retries"));
		lockRetriesTimeGap = Integer.parseInt(env.getProperty("aerospike.itemcode.lock.interval"));
	}

	@Override
	public boolean acquireLock(AuctionableItemLock auctionableItemLock) {
		WritePolicy wr = new WritePolicy(client.getWritePolicyDefault());
		wr.sendKey = true;
		wr.expiration = lockTTL;
		wr.maxRetries = lockRetries;
		wr.sleepBetweenRetries = lockRetriesTimeGap;
		wr.recordExistsAction = RecordExistsAction.CREATE_ONLY;
		auctionableItemLock.setSavedTime(System.currentTimeMillis());
		client.put(wr, new Key(namespace, set, auctionableItemLock.getCode()), new Bin("data", auctionableItemLock));
		return true;
	}

	@Override
	public boolean releaseLock(AuctionableItemLock auctionableItemLock) {
		Key key = new Key(namespace, set, auctionableItemLock.getCode());
		return client.delete(client.getWritePolicyDefault(), key);
	}

	@Override
	public boolean isLocked(AuctionableItemLock auctionableItemLock) {
		Key key = new Key(namespace, set, auctionableItemLock.getCode());
		Record record = client.get(client.getReadPolicyDefault(), key);
		if (record == null)
			return false;
		AuctionableItemLock lock = (AuctionableItemLock) record.getValue("data");
		if (lock.getLockId().equals(auctionableItemLock.getLockId())) {
			return true;
		}
		return false;
	}

}
