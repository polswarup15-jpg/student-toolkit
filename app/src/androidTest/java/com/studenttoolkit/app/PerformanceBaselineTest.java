package com.studenttoolkit.app;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Emulator baseline only: measures activity creation and synchronous page rendering.
 * These values are NOT cold-start, frame-timing, or physical-device certification.
 */
@RunWith(AndroidJUnit4.class)
public class PerformanceBaselineTest {
    private static final String TAG = "ST_PERF_BASELINE";

    @Test public void recordsActivityAndPageRenderDurations() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Intent intent = new Intent(instrumentation.getTargetContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        long start = SystemClock.elapsedRealtimeNanos();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
        long createdMs = (SystemClock.elapsedRealtimeNanos() - start) / 1_000_000L;
        Log.i(TAG, "activity_start_to_idle_ms=" + createdMs + " (warm/unspecified emulator start, not cold launch)");
        try {
            assertNotNull(activity.body);
            for (int i = 0; i < activity.pages.length; i++) {
                final int page = i;
                long before = SystemClock.elapsedRealtimeNanos();
                instrumentation.runOnMainSync(() -> activity.show(page));
                instrumentation.waitForIdleSync();
                long durationMs = (SystemClock.elapsedRealtimeNanos() - before) / 1_000_000L;
                Log.i(TAG, "page=" + activity.pages[i] + " render_to_idle_ms=" + durationMs);
                assertEquals(page, activity.page);
                assertNotNull(activity.body);
            }
        } finally {
            instrumentation.runOnMainSync(activity::finish);
        }
    }
}
