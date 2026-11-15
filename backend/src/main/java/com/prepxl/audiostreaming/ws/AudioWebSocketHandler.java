package com.prepxl.audiostreaming.ws;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class AudioWebSocketHandler extends BinaryWebSocketHandler {
    private static final int MAX_AUDIO_MESSAGE_BYTES = 32 * 1024;
    private static final int MAX_FRAMES_PER_SECOND = 30;
    private static final int MAX_BYTES_PER_SECOND = 256 * 1024;
    private static final CloseStatus SERVER_AT_CAPACITY = new CloseStatus(1013, "Server capacity reached");
    private final Map<String, FrameWindow> frameWindows = new ConcurrentHashMap<>();
    private final Semaphore connectionSlots;

    public AudioWebSocketHandler(int maxConcurrentConnections) {
        if (maxConcurrentConnections < 1) {
            throw new IllegalArgumentException("maxConcurrentConnections must be positive");
        }
        connectionSlots = new Semaphore(maxConcurrentConnections);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        if (!connectionSlots.tryAcquire()) {
            session.close(SERVER_AT_CAPACITY);
            return;
        }
        frameWindows.put(session.getId(), new FrameWindow());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message)
            throws IOException {
        if (message.getPayloadLength() > MAX_AUDIO_MESSAGE_BYTES) {
            session.close(CloseStatus.TOO_BIG_TO_PROCESS);
            return;
        }

        FrameWindow window = frameWindows.get(session.getId());
        if (window == null || !window.allow(System.nanoTime(), message.getPayloadLength())) {
            session.close(new CloseStatus(1008, "Audio rate limit exceeded"));
            return;
        }

        // Echo audio back (for testing)
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (frameWindows.remove(session.getId()) != null) {
            connectionSlots.release();
        }
    }

    static final class FrameWindow {
        private static final long WINDOW_NANOS = 1_000_000_000L;
        private final Queue<Frame> frames = new ArrayDeque<>(MAX_FRAMES_PER_SECOND);
        private long bytesInWindow;

        synchronized boolean allow(long now, int frameBytes) {
            while (!frames.isEmpty() && now - frames.peek().timestampNanos() >= WINDOW_NANOS) {
                bytesInWindow -= frames.remove().bytes();
            }
            if (frames.size() >= MAX_FRAMES_PER_SECOND || bytesInWindow + frameBytes > MAX_BYTES_PER_SECOND) {
                return false;
            }
            frames.add(new Frame(now, frameBytes));
            bytesInWindow += frameBytes;
            return true;
        }

        private record Frame(long timestampNanos, int bytes) {}
    }
}
