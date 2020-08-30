package com.auctions.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.auctions.pojo.BidsFeedResponse;
import com.auctions.repository.AuctionStatusRepositoryImpl;
import com.auctions.repository.AuctionableItemLockRepositoryImpl;
import com.auctions.repository.AuctionableItemRepositoryImpl;
import com.auctions.service.AuctionService.Status;
import com.auctions.startup.BeanConfigs;
import com.auctions.startup.WebSocketConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BeanConfigs.class, WebSocketConfig.class, AuctionServiceImpl.class,
		AuctionableItemRepositoryImpl.class, AuctionStatusRepositoryImpl.class,
		AuctionableItemLockRepositoryImpl.class })
class AuctionServiceImplTestIT {

	@Autowired
	private AuctionService auctionService;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testPlaceBidPrice() {
		String itemCode = "SBIBNK";
		Float amount = 502F;
		Status status = auctionService.placeBidPrice(itemCode, amount);
		assertNotNull(status);
		assertTrue(status == Status.ACCEPTED);
	}

	@Test
	void testAuctionFeed() {
		BidsFeedResponse feedResponse = new BidsFeedResponse("SBIBNK", 245F, 250F);
		auctionService.auctionFeed(feedResponse);
	}

}
