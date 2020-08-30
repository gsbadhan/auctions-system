package com.auctions.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.auctions.pojo.BidRequest;
import com.auctions.startup.Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

@SpringBootTest(classes = { Application.class })
@AutoConfigureMockMvc
class AuctionControllerTestIT {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSubmitBid4xx() throws JsonProcessingException, Exception {
		BidRequest bidRequest = new BidRequest(700F);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/auction/{itemCode}/bid", "SBIBNK")
				.contentType("application/json").content(new Gson().toJson(bidRequest)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	void testSubmitBid201() throws JsonProcessingException, Exception {
		BidRequest bidRequest = new BidRequest(800F);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/auction/{itemCode}/bid", "SBIBNK")
				.contentType("application/json").content(new Gson().toJson(bidRequest)))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

}
