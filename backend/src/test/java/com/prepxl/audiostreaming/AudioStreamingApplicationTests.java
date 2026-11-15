package com.prepxl.audiostreaming;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

import com.prepxl.audiostreaming.ws.AudioWebSocketHandler;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AudioStreamingApplicationTests {
	@Autowired
	private WebTestClient webTestClient;

	@Test
	void contextLoads() {
	}

	@Test
	void healthEndpointReturnsOk() {
		webTestClient.get().uri("/health")
				.exchange()
				.expectStatus().isOk()
				.expectBody().json("{\"status\":\"ok\"}");
	}

	@Test
	void limitsConcurrentAudioSessionsAndReleasesCapacityOnClose() throws Exception {
		AudioWebSocketHandler handler = new AudioWebSocketHandler(1);
		WebSocketSession first = session("first");
		WebSocketSession rejected = session("rejected");

		handler.afterConnectionEstablished(first);
		handler.afterConnectionEstablished(rejected);

		ArgumentCaptor<CloseStatus> closeStatus = ArgumentCaptor.forClass(CloseStatus.class);
		verify(rejected).close(closeStatus.capture());
		assertEquals(1013, closeStatus.getValue().getCode());

		handler.afterConnectionClosed(first, CloseStatus.NORMAL);
		WebSocketSession replacement = session("replacement");
		handler.afterConnectionEstablished(replacement);
		verify(replacement, never()).close(any(CloseStatus.class));
	}

	private WebSocketSession session(String id) {
		WebSocketSession session = mock(WebSocketSession.class);
		when(session.getId()).thenReturn(id);
		return session;
	}

}
