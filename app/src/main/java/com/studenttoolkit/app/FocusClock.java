package com.studenttoolkit.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

/** One local clock shared by the activity and floating service. No database migration. */
final class FocusClock {
    private static final String PREFS = "shared_focus_clock_v1";
    static final String POMODORO = "Pomodoro", STOPWATCH = "Stopwatch", COUNTDOWN = "Countdown";
    static final class State {
        final String mode, subject;
        final boolean running, breakTime;
        final long elapsedMs, remainingMs;
        State(String m, String s, boolean r, boolean b, long e, long left) {
            mode=m; subject=s; running=r; breakTime=b; elapsedMs=e; remainingMs=left;
        }
    }
    private FocusClock() {}
    static synchronized State read(Context context) {
        SharedPreferences p=context.getSharedPreferences(PREFS,0);
        String mode=p.getString("mode",POMODORO), subject=p.getString("subject","");
        boolean running=p.getBoolean("running",false), breakTime=p.getBoolean("break",false);
        long elapsed=Math.max(0,p.getLong("elapsed",0)), remaining=Math.max(0,p.getLong("remaining",25*60000L));
        if(running) {
            long delta=Math.max(0,SystemClock.elapsedRealtime()-p.getLong("started",SystemClock.elapsedRealtime()));
            if(breakTime) remaining=Math.max(0,remaining-delta);
            else if(STOPWATCH.equals(mode)) elapsed+=delta;
            else {long credited=Math.min(delta,remaining);elapsed+=credited;remaining-=credited;}
            if (!STOPWATCH.equals(mode) && remaining==0) {
                running=false;
                p.edit().putBoolean("running",false).putLong("elapsed",elapsed)
                    .putLong("remaining",0).commit();
            }
        }
        return new State(mode,subject,running,breakTime,elapsed,remaining);
    }
    static synchronized void configure(Context context,String mode,String subject,long remaining,boolean breakTime) {
        State s=read(context);
        if(s.running) return;
        context.getSharedPreferences(PREFS,0).edit().putString("mode",mode).putString("subject",subject)
            .putLong("remaining",Math.max(0,remaining)).putBoolean("break",breakTime).commit();
    }
    static synchronized void play(Context context) {
        State s=read(context);
        if(s.running || s.subject.isEmpty() || (!STOPWATCH.equals(s.mode)&&s.remainingMs==0)) return;
        context.getSharedPreferences(PREFS,0).edit().putBoolean("running",true)
            .putLong("started",SystemClock.elapsedRealtime()).commit();
    }
    static synchronized void pause(Context context) {
        State s=read(context);
        context.getSharedPreferences(PREFS,0).edit().putBoolean("running",false)
            .putLong("elapsed",s.elapsedMs).putLong("remaining",s.remainingMs).commit();
    }
    static synchronized void reset(Context context,String mode,long initialMs) {
        context.getSharedPreferences(PREFS,0).edit().putBoolean("running",false).putBoolean("break",false)
            .putString("mode",mode).putString("subject","").putLong("elapsed",0)
            .putLong("remaining",initialMs).commit();
    }
}
