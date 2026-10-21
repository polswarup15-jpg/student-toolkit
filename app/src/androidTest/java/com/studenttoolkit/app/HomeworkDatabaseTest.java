package com.studenttoolkit.app;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class HomeworkDatabaseTest {
    private Store fresh() {
        android.content.Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        ctx.deleteDatabase("student_toolkit.db");
        return new Store(ctx);
    }
    private ContentValues task(String title,String subject,String due) {
        ContentValues v=new ContentValues();
        v.put("title",title);v.put("subject",subject);v.put("due",due);return v;
    }
    @Test public void createEditCompleteReopenDeleteAndPersist() {
        Store db=fresh();
        try {
            db.add("homework",task("Read chapter","Economics","2099-12-31"));
            assertEquals(1,db.pendingHomework());
            long id;
            try(Cursor c=db.all("homework")) {
                assertTrue(c.moveToFirst());
                id=c.getLong(c.getColumnIndexOrThrow("_id"));
                assertEquals("Read chapter",c.getString(c.getColumnIndexOrThrow("title")));
            }
            db.update("homework",id,task("Revise chapter","Economics","2099-12-30"));
            db.setDone(id,true);
            assertEquals(1,db.completedHomework());
            assertEquals(0,db.pendingHomework());
            db.close();
            db=new Store(InstrumentationRegistry.getInstrumentation().getTargetContext());
            try(Cursor c=db.one("homework",id)) {
                assertTrue(c.moveToFirst());
                assertEquals("Revise chapter",c.getString(c.getColumnIndexOrThrow("title")));
                assertEquals(1,c.getInt(c.getColumnIndexOrThrow("done")));
            }
            db.setDone(id,false);
            assertEquals(1,db.pendingHomework());
            db.delete("homework",id);
            assertEquals(0,db.count("homework"));
        } finally {db.close();}
    }
    @Test public void overdueAndUpcomingExcludeCompletedTasks() {
        Store db=fresh();
        try {
            String past=java.time.LocalDate.now().minusDays(1).toString();
            String future=java.time.LocalDate.now().plusDays(2).toString();
            db.add("homework",task("Late","Economics",past));
            db.add("homework",task("Soon","Economics",future));
            db.add("homework",task("Undated","Economics",""));
            assertEquals(1,db.overdueHomework());
            try(Cursor c=db.upcomingHomework()) {
                assertTrue(c.moveToFirst());
                assertEquals("Soon",c.getString(0));
                assertEquals(1,c.getCount());
            }
            long lateId=-1;
            try(Cursor c=db.all("homework")) {
                while(c.moveToNext())if("Late".equals(c.getString(c.getColumnIndexOrThrow("title"))))lateId=c.getLong(c.getColumnIndexOrThrow("_id"));
            }
            assertTrue(lateId>0);
            db.setDone(lateId,true);
            assertEquals(0,db.overdueHomework());
            assertEquals(2,db.pendingHomework());
        } finally {db.close();}
    }
}
