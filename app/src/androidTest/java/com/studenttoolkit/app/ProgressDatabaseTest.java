package com.studenttoolkit.app;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class ProgressDatabaseTest {
 private Store fresh(){Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();ctx.deleteDatabase("student_toolkit.db");return new Store(ctx);}
 @Test public void homeworkCompletionStatisticsUpdate(){Store db=fresh();try{
  ContentValues a=new ContentValues();a.put("title","First");db.add("homework",a);
  ContentValues b=new ContentValues();b.put("title","Second");db.add("homework",b);
  assertEquals(2,db.count("homework"));assertEquals(2,db.pendingHomework());assertEquals(0,db.completedHomework());
  long id;try(Cursor c=db.all("homework")){assertTrue(c.moveToFirst());id=c.getLong(c.getColumnIndexOrThrow("_id"));}
  db.setDone(id,true);assertEquals(1,db.completedHomework());assertEquals(1,db.pendingHomework());
  db.setDone(id,false);assertEquals(0,db.completedHomework());assertEquals(2,db.pendingHomework());
 }finally{db.close();}}
 @Test public void verifiedScoreAndRollingWeeklyAggregates(){Store db=fresh();try{
  db.recordStudy("Math","Pomodoro",1500);db.recordStudy("Math","Stopwatch",615);
  ContentValues old=new ContentValues();old.put("minutes",20);old.put("duration_seconds",1200);old.put("subject","Math");old.put("timer_mode","Countdown");old.put("verified",1);old.put("completed_at",System.currentTimeMillis()-9L*24*60*60*1000);db.add("focus_sessions",old);
  ContentValues unverified=new ContentValues();unverified.put("minutes",30);unverified.put("duration_seconds",1800);unverified.put("subject","Math");unverified.put("timer_mode","Pomodoro");unverified.put("verified",0);unverified.put("completed_at",System.currentTimeMillis());db.add("focus_sessions",unverified);
  assertEquals(4,db.count("focus_sessions"));assertEquals(3315L,db.verifiedStudySeconds());
  assertEquals(3315L/360L,db.verifiedStudySeconds()/360L);
  assertEquals(65,db.focusMinutesLastSevenDays());assertEquals(3,db.focusSessionsLastSevenDays());
  assertEquals(85,db.totalFocusMinutes());
 }finally{db.close();}}
}