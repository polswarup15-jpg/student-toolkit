package com.studenttoolkit.app;
import android.content.*;import android.database.*;import android.database.sqlite.*;
public class Store extends SQLiteOpenHelper {
 public Store(Context c){super(c,"student_toolkit.db",null,4);}
 @Override public void onCreate(SQLiteDatabase d){
  d.execSQL("CREATE TABLE subjects (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL)");
  d.execSQL("CREATE TABLE homework (_id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,subject TEXT,due TEXT,done INTEGER DEFAULT 0)");
  d.execSQL("CREATE TABLE notes (_id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,body TEXT)");
  d.execSQL("CREATE TABLE exams (_id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,subject TEXT,date TEXT NOT NULL)");
  d.execSQL("CREATE TABLE focus_sessions (_id INTEGER PRIMARY KEY AUTOINCREMENT,minutes INTEGER NOT NULL,completed_at INTEGER NOT NULL,subject TEXT NOT NULL DEFAULT 'General',duration_seconds INTEGER NOT NULL DEFAULT 0,timer_mode TEXT NOT NULL DEFAULT 'Pomodoro',verified INTEGER NOT NULL DEFAULT 1)");
  d.execSQL("CREATE TABLE timetable (_id INTEGER PRIMARY KEY AUTOINCREMENT,day TEXT NOT NULL,time TEXT NOT NULL,subject TEXT NOT NULL)");
 }
 @Override public void onUpgrade(SQLiteDatabase d,int oldVersion,int newVersion){if(oldVersion<2)d.execSQL("CREATE TABLE IF NOT EXISTS exams (_id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,subject TEXT,date TEXT NOT NULL)");if(oldVersion<3)d.execSQL("CREATE TABLE IF NOT EXISTS focus_sessions (_id INTEGER PRIMARY KEY AUTOINCREMENT,minutes INTEGER NOT NULL,completed_at INTEGER NOT NULL)");if(oldVersion<4){d.execSQL("ALTER TABLE focus_sessions ADD COLUMN subject TEXT NOT NULL DEFAULT 'General'");d.execSQL("ALTER TABLE focus_sessions ADD COLUMN duration_seconds INTEGER NOT NULL DEFAULT 0");d.execSQL("ALTER TABLE focus_sessions ADD COLUMN timer_mode TEXT NOT NULL DEFAULT 'Pomodoro'");d.execSQL("ALTER TABLE focus_sessions ADD COLUMN verified INTEGER NOT NULL DEFAULT 1");d.execSQL("UPDATE focus_sessions SET duration_seconds=minutes*60");}}
 public void recordFocus(int minutes){ContentValues v=new ContentValues();v.put("minutes",minutes);v.put("completed_at",System.currentTimeMillis());getWritableDatabase().insertOrThrow("focus_sessions",null,v);}
 public void recordStudy(String subject,String mode,int seconds){if(subject==null||subject.trim().isEmpty()||seconds<600)return;ContentValues v=new ContentValues();v.put("minutes",seconds/60);v.put("duration_seconds",seconds);v.put("subject",subject.trim());v.put("timer_mode",mode);v.put("verified",1);v.put("completed_at",System.currentTimeMillis());getWritableDatabase().insertOrThrow("focus_sessions",null,v);}
 public Cursor subjectsForTimer(){return getReadableDatabase().rawQuery("SELECT name FROM subjects WHERE TRIM(name)<>'' ORDER BY name COLLATE NOCASE",null);}
 public void add(String table,ContentValues values){getWritableDatabase().insertOrThrow(table,null,values);}
 public Cursor all(String table){String order=table.equals("exams")?"date ASC,_id ASC":table.equals("timetable")?"CASE day WHEN 'Monday' THEN 1 WHEN 'Tuesday' THEN 2 WHEN 'Wednesday' THEN 3 WHEN 'Thursday' THEN 4 WHEN 'Friday' THEN 5 WHEN 'Saturday' THEN 6 WHEN 'Sunday' THEN 7 ELSE 8 END,time ASC":"_id DESC";return getReadableDatabase().rawQuery("SELECT * FROM "+table+" ORDER BY "+order,null);}
 public Cursor one(String table,long id){return getReadableDatabase().query(table,null,"_id=?",new String[]{String.valueOf(id)},null,null,null);}
 public void update(String table,long id,ContentValues values){getWritableDatabase().update(table,values,"_id=?",new String[]{String.valueOf(id)});}
 public int count(String table){try(Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM "+table,null)){c.moveToFirst();return c.getInt(0);}}
 public void delete(String table,long id){getWritableDatabase().delete(table,"_id=?",new String[]{String.valueOf(id)});}
 public int completedHomework(){try(Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM homework WHERE done=1",null)){c.moveToFirst();return c.getInt(0);}}
 public int totalFocusMinutes(){try(Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(minutes),0) FROM focus_sessions",null)){c.moveToFirst();return c.getInt(0);}}
 public int focusMinutesLastSevenDays(){long since=System.currentTimeMillis()-7L*24*60*60*1000;try(Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(minutes),0) FROM focus_sessions WHERE completed_at>=?",new String[]{String.valueOf(since)})){c.moveToFirst();return c.getInt(0);}}
 public int focusSessionsLastSevenDays(){long since=System.currentTimeMillis()-7L*24*60*60*1000;try(Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM focus_sessions WHERE completed_at>=?",new String[]{String.valueOf(since)})){c.moveToFirst();return c.getInt(0);}}
 public long verifiedStudySeconds(){try(Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(CASE WHEN duration_seconds>0 THEN duration_seconds ELSE minutes*60 END),0) FROM focus_sessions WHERE verified=1",null)){c.moveToFirst();return c.getLong(0);}}
 public int pendingHomework(){try(Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM homework WHERE done=0",null)){c.moveToFirst();return c.getInt(0);}}
 public int overdueHomework(){String today=java.time.LocalDate.now().toString();try(Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM homework WHERE done=0 AND due IS NOT NULL AND due<>'' AND due<?",new String[]{today})){c.moveToFirst();return c.getInt(0);}}
 public Cursor upcomingHomework(){String today=java.time.LocalDate.now().toString();return getReadableDatabase().rawQuery("SELECT title,subject,due FROM homework WHERE done=0 AND due IS NOT NULL AND due<>'' AND due>=? ORDER BY due ASC,_id ASC LIMIT 5",new String[]{today});}
 public void setDone(long id,boolean done){ContentValues v=new ContentValues();v.put("done",done?1:0);getWritableDatabase().update("homework",v,"_id=?",new String[]{String.valueOf(id)});}
}
