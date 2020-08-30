package com.auctions.utils;

import java.util.UUID;

public class IdGeneration {
	private IdGeneration() {
	}

	public static String getLockId() {
		return UUID.randomUUID().toString();
	}
}
