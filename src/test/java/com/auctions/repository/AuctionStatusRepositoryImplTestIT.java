package com.auctions.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.auctions.service.AuctionService.Status;
import com.auctions.startup.BeanConfigs;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BeanConfigs.class, AuctionStatusRepositoryImpl.class })
class AuctionStatusRepositoryImplTestIT {
	@Autowired
	private AuctionStatusRepository auctionStatusRepository;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSave() {
		Status status = auctionStatusRepository.save("SBIBNK", 250F);
		assertNotNull(status);
		assertTrue(status == Status.ACCEPTED);
	}

	@Test
	void testGet() {
		Optional<AuctionStatus> auctionStatus = auctionStatusRepository.get("SBIBNK");
		assertNotNull(auctionStatus);
		assertTrue(auctionStatus.isPresent());
		assertNotNull(auctionStatus.get().getCode());
		assertNotNull(auctionStatus.get().getHighestBidAmount());
	}

}
