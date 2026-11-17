package com.prepxl.audiostreaming.ws;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AudioWebSocketHandlerTest {
    @Test
    void rejectsAtCapacityAndReleasesSlotOnlyForAcceptedSessions() throws Exception {
        AudioWebSocketHandler handler = new AudioWebSocketHandler(1);
        AtomicReference<CloseStatus> firstClosed = new AtomicReference<>();
        AtomicReference<CloseStatus> rejectedClosed = new AtomicReference<>();
        AtomicReference<CloseStatus> nextClosed = new AtomicReference<>();
        WebSocketSession first = session("first", firstClosed);
        WebSocketSession rejected = session("rejected", rejectedClosed);
        WebSocketSession next = session("next", nextClosed);

        handler.afterConnectionEstablished(first);
        handler.afterConnectionEstablished(rejected);
        assertEquals(1013, rejectedClosed.get().getCode());

        handler.afterConnectionClosed(rejected, CloseStatus.NORMAL);
        handler.afterConnectionClosed(first, CloseStatus.NORMAL);
        handler.afterConnectionClosed(first, CloseStatus.NORMAL);
        handler.afterConnectionEstablished(next);

        assertNull(nextClosed.get());
        handler.afterConnectionClosed(next, CloseStatus.NORMAL);
    }

    private static WebSocketSession session(String id, AtomicReference<CloseStatus> closed) {
        return (WebSocketSession) Proxy.newProxyInstance(
                WebSocketSession.class.getClassLoader(),
                new Class<?>[]{WebSocketSession.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getId" -> id;
                    case "close" -> {
                        closed.set((CloseStatus) args[0]);
                        yield null;
                    }
                    case "isOpen" -> true;
                    case "toString" -> "WebSocketSession[" + id + "]";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }
}
