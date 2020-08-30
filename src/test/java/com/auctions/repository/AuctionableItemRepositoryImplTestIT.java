package com.auctions.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.javatuples.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.auctions.startup.BeanConfigs;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BeanConfigs.class, AuctionableItemRepositoryImpl.class })
class AuctionableItemRepositoryImplTestIT {

	@Autowired
	private AuctionableItemRepository auctionableItemRepository;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testIsValidAuction() {
		Pair<Boolean, Float> item = auctionableItemRepository.isValidAuction("SBIBNK");
		assertNotNull(item);
	}

	@Test
	void testSave() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long start = calendar.getTimeInMillis();
		calendar.add(Calendar.HOUR_OF_DAY, 4);
		Long end = calendar.getTimeInMillis();
		AuctionableItem item1 = new AuctionableItem("SBIBNK", start, end, 250.0F);
		auctionableItemRepository.save(item1);
		Pair<Boolean, Float> item = auctionableItemRepository.isValidAuction("SBIBNK");
		assertNotNull(item);
		assertTrue(item.getValue0());
		assertTrue(item.getValue1() == 250.0F);
	}

}
