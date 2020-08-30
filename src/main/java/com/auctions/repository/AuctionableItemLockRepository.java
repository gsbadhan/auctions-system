package com.auctions.repository;

public interface AuctionableItemLockRepository {

	boolean acquireLock(AuctionableItemLock auctionableItemLock);

	boolean isLocked(AuctionableItemLock auctionableItemLock);

	boolean releaseLock(AuctionableItemLock auctionableItemLock);
}
