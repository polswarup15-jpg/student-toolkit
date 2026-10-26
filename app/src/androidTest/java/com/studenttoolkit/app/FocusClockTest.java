package com.studenttoolkit.app;

import android.content.Context;
import android.os.SystemClock;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FocusClockTest {
    @Test public void sharedClockPlayPauseAndResetAreConsistentAcrossReads() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        FocusClock.reset(context,FocusClock.STOPWATCH,0);
        FocusClock.configure(context,FocusClock.STOPWATCH,"Test subject",0,false);
        FocusClock.play(context);
        SystemClock.sleep(80);
        FocusClock.State active=FocusClock.read(context);
        assertTrue(active.running);
        assertTrue(active.elapsedMs>=50);
        FocusClock.pause(context);
        FocusClock.State paused=FocusClock.read(context);
        assertFalse(paused.running);
        long elapsed=paused.elapsedMs;
        SystemClock.sleep(60);
        assertEquals(elapsed,FocusClock.read(context).elapsedMs);
        FocusClock.reset(context,FocusClock.POMODORO,25*60000L);
        FocusClock.State reset=FocusClock.read(context);
        assertFalse(reset.running);
        assertEquals(0,reset.elapsedMs);
        assertEquals(25*60000L,reset.remainingMs);
    }
    @Test public void countdownNeverGoesNegative() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        FocusClock.reset(context,FocusClock.COUNTDOWN,30);
        FocusClock.configure(context,FocusClock.COUNTDOWN,"Test subject",30,false);
        FocusClock.play(context);
        SystemClock.sleep(80);
        FocusClock.State state=FocusClock.read(context);
        assertEquals(0,state.remainingMs);
        assertTrue(state.elapsedMs<=30);
        FocusClock.pause(context);
        FocusClock.reset(context,FocusClock.POMODORO,25*60000L);
    }
}
