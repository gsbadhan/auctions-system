package com.auctions.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import com.auctions.startup.BeanConfigs;
import com.auctions.utils.IdGeneration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BeanConfigs.class, AuctionableItemLockRepositoryImpl.class })
class AuctionableItemLockRepositoryImplTestIT {
	@Autowired
	private AuctionableItemLockRepository auctionableItemLockRepository;
	@Autowired
	private Environment env;
	int lockTTL;

	@BeforeEach
	void setUp() throws Exception {
		lockTTL = Integer.parseInt(env.getProperty("aerospike.itemcode.lock.ttl"));
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testAcquireLock() throws InterruptedException {
		AuctionableItemLock auctionableItemLock = new AuctionableItemLock("SBIBNK", IdGeneration.getLockId());
		boolean status = auctionableItemLockRepository.acquireLock(auctionableItemLock);
		assertTrue(status);
	}

	@Test
	void testAcquireLockWihExpiration() throws InterruptedException {
		AuctionableItemLock auctionableItemLock = new AuctionableItemLock("SBIBNK", IdGeneration.getLockId());
		boolean status = auctionableItemLockRepository.acquireLock(auctionableItemLock);
		assertTrue(status);
		// expiration time
		Thread.sleep(lockTTL * 1000);
		status = auctionableItemLockRepository.isLocked(auctionableItemLock);
		assertFalse(status);
	}

	@Test
	void testReleaseLock() {
		AuctionableItemLock auctionableItemLock = new AuctionableItemLock("SBIBNK", IdGeneration.getLockId());
		boolean status = auctionableItemLockRepository.releaseLock(auctionableItemLock);
		assertTrue(status);
	}

	@Test
	void testIsLocked() {
		AuctionableItemLock auctionableItemLock = new AuctionableItemLock("SBIBNK", IdGeneration.getLockId());
		auctionableItemLockRepository.acquireLock(auctionableItemLock);
		boolean status = auctionableItemLockRepository.isLocked(auctionableItemLock);
		assertTrue(status);
	}

}
