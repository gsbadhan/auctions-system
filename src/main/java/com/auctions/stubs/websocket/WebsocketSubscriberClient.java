package com.auctions.stubs.websocket;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class WebsocketSubscriberClient {

	public static void main(String args[]) throws InterruptedException, ExecutionException {
		AuctionsListener();
	}

	private static void AuctionsListener() throws InterruptedException, ExecutionException {
		WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
		List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(simpleWebSocketClient));

		SockJsClient sockJsClient = new SockJsClient(transports);
		WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
		// stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		stompClient.setMessageConverter(new StringMessageConverter());

		String topic = "/bids";
		String url = "ws://localhost:8080/auction";
		final AtomicReference<Throwable> failure = new AtomicReference<>();
		final AtomicReference<Object> result = new AtomicReference<>();
		final CountDownLatch latch = new CountDownLatch(1);

		StompSessionHandler handler = new StompSessionHandlerAdapter() {
			@Override
			public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
				session.subscribe(topic, new StompFrameHandler() {

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						System.out.println("rec-handleFrame:" + payload);
						result.set(payload);
						latch.countDown();
					}

					@Override
					public Type getPayloadType(StompHeaders headers) {
						System.out.println("rec-getPayloadType:" + headers);
						return String.class;
					}
				});
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				latch.countDown();
			}

			@Override
			public void handleException(StompSession session, StompCommand command, StompHeaders headers,
					byte[] payload, Throwable exception) {
				failure.set(exception);
				latch.countDown();
			}

			@Override
			public void handleTransportError(StompSession session, Throwable exception) {
				failure.set(exception);
				latch.countDown();
			}
		};

		StompSession session = stompClient.connect(url, handler).get();
		System.out.println(session.isConnected());
		System.out.println("done");
		while (session.isConnected()) {
			Thread.sleep(30 * 1000);
		}
		System.out.println("stopped....");
	}
}
