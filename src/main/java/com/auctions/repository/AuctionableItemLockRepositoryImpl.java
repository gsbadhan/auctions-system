package com.auctions.repository;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

@Repository
public class AuctionableItemLockRepositoryImpl implements AuctionableItemLockRepository {
	private static final Logger logger = LoggerFactory.getLogger(AuctionableItemLockRepositoryImpl.class);
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
		Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder().retryIfException()
				.retryIfExceptionOfType(AerospikeException.class)
				.withWaitStrategy(WaitStrategies.fixedWait(lockRetriesTimeGap, TimeUnit.MILLISECONDS))
				.withStopStrategy(StopStrategies.stopAfterAttempt(lockRetries)).build();
		try {
			return retryer.call(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					try {
						return lockEntry(auctionableItemLock);
					} catch (AerospikeException ae) {
						if (ae.getMessage().contains("Key already exists")) {
							logger.debug("retyring for code={}...", auctionableItemLock.getCode());
							throw ae;
						}
					}
					return false;
				}
			});
		} catch (ExecutionException | RetryException e) {
			logger.debug("error occured in acquireLock", e);
		}
		return false;
	}

	private boolean lockEntry(AuctionableItemLock auctionableItemLock) {
		WritePolicy wr = new WritePolicy(client.getWritePolicyDefault());
		wr.sendKey = true;
		wr.expiration = lockTTL;
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
