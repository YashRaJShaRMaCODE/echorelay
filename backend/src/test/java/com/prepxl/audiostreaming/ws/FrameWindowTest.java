package com.prepxl.audiostreaming.ws;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrameWindowTest {
    @Test
    void limitsFramesAcrossTheWholeRollingSecond() {
        AudioWebSocketHandler.FrameWindow window = new AudioWebSocketHandler.FrameWindow();
        for (int frame = 0; frame < 30; frame++) {
            assertTrue(window.allow(frame * 1_000_000L, 0));
        }

        assertFalse(window.allow(999_999_999L, 0));
        assertTrue(window.allow(1_000_000_000L, 0));
        assertFalse(window.allow(1_000_000_001L, 0));
    }

    @Test
    void limitsAudioBytesAcrossTheWholeRollingSecond() {
        AudioWebSocketHandler.FrameWindow window = new AudioWebSocketHandler.FrameWindow();
        int halfBudget = 128 * 1024;

        assertTrue(window.allow(0, halfBudget));
        assertTrue(window.allow(1_000_000L, halfBudget));
        assertFalse(window.allow(2_000_000L, 1));
        assertTrue(window.allow(1_000_000_000L, halfBudget));
    }
}
