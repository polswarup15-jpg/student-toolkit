package com.studenttoolkit.app;
import android.content.Context;
import android.database.Cursor;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FocusDatabaseTest {
 private Store fresh(){Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();ctx.deleteDatabase("student_toolkit.db");return new Store(ctx);}
 @Test public void rejectsShortAndMissingSubjectSessions(){
  Store db=fresh();try{
   db.recordStudy("Accounting","Pomodoro",599);
   db.recordStudy(" ","Countdown",900);
   db.recordStudy(null,"Stopwatch",900);
   assertEquals(0,db.count("focus_sessions"));
   assertEquals(0L,db.verifiedStudySeconds());
  }finally{db.close();}
 }
 @Test public void savesVerifiedSecondsModeAndSubjectAcrossReopen(){
  Store db=fresh();try{
   db.recordStudy("Accounting","Pomodoro",1500);
   db.recordStudy("Economics","Stopwatch",615);
   db.recordStudy("Commerce","Countdown",600);
   assertEquals(3,db.count("focus_sessions"));
   assertEquals(2715L,db.verifiedStudySeconds());
   assertEquals(45,db.totalFocusMinutes());
   assertEquals(45,db.focusMinutesLastSevenDays());
   assertEquals(3,db.focusSessionsLastSevenDays());
   try(Cursor c=db.all("focus_sessions")){
    assertTrue(c.moveToFirst());
    assertEquals("Commerce",c.getString(c.getColumnIndexOrThrow("subject")));
    assertEquals("Countdown",c.getString(c.getColumnIndexOrThrow("timer_mode")));
    assertEquals(600,c.getInt(c.getColumnIndexOrThrow("duration_seconds")));
    assertEquals(1,c.getInt(c.getColumnIndexOrThrow("verified")));
   }
  }finally{db.close();}
  db=new Store(InstrumentationRegistry.getInstrumentation().getTargetContext());
  try{assertEquals(3,db.count("focus_sessions"));assertEquals(2715L,db.verifiedStudySeconds());}
  finally{db.close();}
 }
}