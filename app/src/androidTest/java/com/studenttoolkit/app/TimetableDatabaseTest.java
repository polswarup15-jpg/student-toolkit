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
public class TimetableDatabaseTest {
    private Store fresh() {
        Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        ctx.deleteDatabase("student_toolkit.db");
        return new Store(ctx);
    }
    private ContentValues entry(String day,String time,String subject) {
        ContentValues v=new ContentValues();
        v.put("day",day);v.put("time",time);v.put("subject",subject);
        return v;
    }
    @Test public void createEditPersistDelete() {
        Store db=fresh();
        long id;
        try {
            db.add("timetable",entry("Monday","09:00","Math"));
            assertEquals(1,db.count("timetable"));
            try(Cursor c=db.all("timetable")) {
                assertTrue(c.moveToFirst());
                id=c.getLong(c.getColumnIndexOrThrow("_id"));
                assertEquals("Monday",c.getString(c.getColumnIndexOrThrow("day")));
            }
            db.update("timetable",id,entry("Tuesday","10:30","Science"));
        } finally {db.close();}
        db=new Store(InstrumentationRegistry.getInstrumentation().getTargetContext());
        try {
            try(Cursor c=db.one("timetable",id)) {
                assertTrue(c.moveToFirst());
                assertEquals("Tuesday",c.getString(c.getColumnIndexOrThrow("day")));
                assertEquals("10:30",c.getString(c.getColumnIndexOrThrow("time")));
                assertEquals("Science",c.getString(c.getColumnIndexOrThrow("subject")));
            }
            db.delete("timetable",id);
            assertEquals(0,db.count("timetable"));
        } finally {db.close();}
    }
    @Test public void entriesSortByWeekdayThenTime() {
        Store db=fresh();
        try {
            db.add("timetable",entry("Friday","08:00","F"));
            db.add("timetable",entry("Monday","11:00","Late"));
            db.add("timetable",entry("Monday","09:00","Early"));
            try(Cursor c=db.all("timetable")) {
                assertEquals(3,c.getCount());
                assertTrue(c.moveToFirst());
                assertEquals("Early",c.getString(c.getColumnIndexOrThrow("subject")));
                assertTrue(c.moveToNext());
                assertEquals("Late",c.getString(c.getColumnIndexOrThrow("subject")));
                assertTrue(c.moveToNext());
                assertEquals("F",c.getString(c.getColumnIndexOrThrow("subject")));
            }
        } finally {db.close();}
    }
}
