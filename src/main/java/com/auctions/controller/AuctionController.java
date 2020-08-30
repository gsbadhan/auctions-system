package com.auctions.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auctions.pojo.BidRequest;
import com.auctions.service.AuctionService;
import com.auctions.service.AuctionService.Status;
import com.auctions.startup.Application;
import com.google.common.base.Strings;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v1/auction")
@Api(value = "auction API service")
public class AuctionController {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	private AuctionService auctionService;

	@PostMapping("/{itemCode}/bid")
	@ApiOperation(value = "place bid")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Bid is accepted"),
			@ApiResponse(response = String.class, code = 404, message = "Auction not found"),
			@ApiResponse(response = String.class, code = 406, message = "Bid is rejected") })
	public ResponseEntity<?> submitBid(@PathVariable(value = "itemCode") String itemCode, @RequestBody BidRequest body)
			throws IOException, InterruptedException {
		logger.info("http request={},itemCode={}", body, itemCode);
		if (Strings.isNullOrEmpty(itemCode) || itemCode.length() > 8 || body.getAmount() == null
				|| body.getAmount() < 0.0F)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

		ResponseEntity<?> response = apiResponse(auctionService.placeBidPrice(itemCode, body.getAmount()));
		logger.info("http response={}", response);
		return response;
	}

	private ResponseEntity<?> apiResponse(Status status) {
		ResponseEntity<?> response = null;
		switch (status) {
		case ACCEPTED:
			response = ResponseEntity.status(HttpStatus.CREATED).body(null);
			break;
		case NOT_FOUND:
			response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			break;
		case REJECTED:
			response = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
			break;
		default:
			response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			break;
		}
		return response;
	}

}
