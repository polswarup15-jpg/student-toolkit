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
public class ExamsDatabaseTest {
 private Store fresh(){Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();ctx.deleteDatabase("student_toolkit.db");return new Store(ctx);}
 private ContentValues exam(String title,String subject,String date){ContentValues v=new ContentValues();v.put("title",title);v.put("subject",subject);v.put("date",date);return v;}
 @Test public void createEditPersistDelete(){Store db=fresh();long id;try{db.add("exams",exam("Accounting","Commerce","2027-03-15"));assertEquals(1,db.count("exams"));try(Cursor c=db.all("exams")){assertTrue(c.moveToFirst());id=c.getLong(c.getColumnIndexOrThrow("_id"));}db.update("exams",id,exam("Accounting Final","Commerce","2027-03-17"));}finally{db.close();}db=new Store(InstrumentationRegistry.getInstrumentation().getTargetContext());try{try(Cursor c=db.one("exams",id)){assertTrue(c.moveToFirst());assertEquals("Accounting Final",c.getString(c.getColumnIndexOrThrow("title")));assertEquals("2027-03-17",c.getString(c.getColumnIndexOrThrow("date")));}db.delete("exams",id);assertEquals(0,db.count("exams"));}finally{db.close();}}
 @Test public void sortsByExamDate(){Store db=fresh();try{db.add("exams",exam("Later","","2027-05-01"));db.add("exams",exam("Earlier","","2027-01-01"));try(Cursor c=db.all("exams")){assertTrue(c.moveToFirst());assertEquals("Earlier",c.getString(c.getColumnIndexOrThrow("title")));assertTrue(c.moveToNext());assertEquals("Later",c.getString(c.getColumnIndexOrThrow("title")));}}finally{db.close();}}
}