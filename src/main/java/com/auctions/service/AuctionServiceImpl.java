package com.auctions.service;

import java.util.concurrent.CompletableFuture;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.auctions.pojo.BidsFeedResponse;
import com.auctions.repository.AuctionStatus;
import com.auctions.repository.AuctionStatusRepository;
import com.auctions.repository.AuctionableItemLock;
import com.auctions.repository.AuctionableItemLockRepository;
import com.auctions.repository.AuctionableItemRepository;
import com.auctions.utils.IdGeneration;
import com.google.gson.Gson;

@Service
public class AuctionServiceImpl implements AuctionService {
	private static final Logger logger = LoggerFactory.getLogger(AuctionServiceImpl.class);

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private AuctionableItemRepository auctionableItemRepository;
	@Autowired
	private AuctionStatusRepository auctionStatusRepository;
	@Autowired
	private AuctionableItemLockRepository auctionableItemLockRepository;

	@Override
	public Status placeBidPrice(String itemCode, Float amount) {
		Status status = Status.REJECTED;
		Pair<Boolean, Float> auctionItem = null;
		AuctionStatus currentAuctionStatus = null;
		try {
			auctionItem = auctionableItemRepository.isValidAuction(itemCode);
			if (!auctionItem.getValue0()) {
				return Status.NOT_FOUND;
			}

			// acquire lock
			AuctionableItemLock auctionableItemLock = new AuctionableItemLock(itemCode, IdGeneration.getLockId());
			if (auctionableItemLockRepository.acquireLock(auctionableItemLock)) {
				currentAuctionStatus = auctionStatusRepository.get(itemCode).get();
				Float minimumBidAmtDiff = amount - currentAuctionStatus.getHighestBidAmount();
				if (minimumBidAmtDiff < auctionItem.getValue1()) {
					logger.info(
							"minimum bid amount difference not matching minimumBidAmountDiff={},stepRate={},highestBidAmount={}",
							minimumBidAmtDiff, auctionItem.getValue1(), currentAuctionStatus.getHighestBidAmount());
					auctionableItemLockRepository.releaseLock(auctionableItemLock);
					return status;
				}

				// last check before amount execution
				if (!auctionableItemLockRepository.isLocked(auctionableItemLock)) {
					return status;
				}

				// execute amount
				status = auctionStatusRepository.save(itemCode, amount);
				auctionableItemLockRepository.releaseLock(auctionableItemLock);
				BidsFeedResponse feedResponse = new BidsFeedResponse(itemCode, amount, auctionItem.getValue1());
				if (status == Status.ACCEPTED) {
					CompletableFuture.runAsync(() -> auctionFeed(feedResponse));
				}
				return status;
			}
		} catch (Exception e) {
			logger.error("error occured in placeBidPrice for itemCode={},amount={}", itemCode, amount, e);
		} finally {
			logger.info("TRX status={},itemCode={},amount={},stepPrice={},highestAmount={}", status, itemCode, amount,
					auctionItem.getValue1(),
					(currentAuctionStatus != null ? currentAuctionStatus.getHighestBidAmount() : null));
		}
		return status;
	}

	@Override
	public void auctionFeed(BidsFeedResponse feedResponse) {
		try {
			Gson gson = new Gson();
			String payload = gson.toJson(feedResponse);
			logger.debug("broadcasting payload={}", payload);
			this.messagingTemplate.convertAndSend("/bids", payload);
		} catch (MessagingException e) {
			logger.error("error occured in auctionFeed for feedResponse={}", feedResponse, e);
		}
	}

}
