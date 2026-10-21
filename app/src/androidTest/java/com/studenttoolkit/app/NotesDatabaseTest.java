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
public class NotesDatabaseTest {
    private Store fresh() {
        Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        ctx.deleteDatabase("student_toolkit.db");
        return new Store(ctx);
    }
    private ContentValues note(String title,String body) {
        ContentValues v=new ContentValues();
        v.put("title",title);
        v.put("body",body);
        return v;
    }
    @Test public void createEditPersistAndDeleteMultilineNote() {
        Store db=fresh();
        long id;
        try {
            db.add("notes",note("Lecture","First line\nSecond line"));
            assertEquals(1,db.count("notes"));
            try(Cursor c=db.all("notes")) {
                assertTrue(c.moveToFirst());
                id=c.getLong(c.getColumnIndexOrThrow("_id"));
                assertEquals("Lecture",c.getString(c.getColumnIndexOrThrow("title")));
                assertEquals("First line\nSecond line",c.getString(c.getColumnIndexOrThrow("body")));
            }
            db.update("notes",id,note("Revised lecture","Updated\nWith multiple lines"));
        } finally {db.close();}
        db=new Store(InstrumentationRegistry.getInstrumentation().getTargetContext());
        try {
            try(Cursor c=db.one("notes",id)) {
                assertTrue(c.moveToFirst());
                assertEquals("Revised lecture",c.getString(c.getColumnIndexOrThrow("title")));
                assertEquals("Updated\nWith multiple lines",c.getString(c.getColumnIndexOrThrow("body")));
            }
            db.delete("notes",id);
            assertEquals(0,db.count("notes"));
        } finally {db.close();}
    }
    @Test public void notesRemainSeparateAndAllowEmptyBody() {
        Store db=fresh();
        try {
            db.add("notes",note("First",""));
            db.add("notes",note("Second","Different text"));
            assertEquals(2,db.count("notes"));
            long secondId;
            try(Cursor c=db.all("notes")) {
                assertTrue(c.moveToFirst());
                assertEquals("Second",c.getString(c.getColumnIndexOrThrow("title")));
                secondId=c.getLong(c.getColumnIndexOrThrow("_id"));
                assertTrue(c.moveToNext());
                assertEquals("",c.getString(c.getColumnIndexOrThrow("body")));
            }
            db.delete("notes",secondId);
            assertEquals(1,db.count("notes"));
            try(Cursor c=db.all("notes")) {
                assertTrue(c.moveToFirst());
                assertEquals("First",c.getString(c.getColumnIndexOrThrow("title")));
            }
        } finally {db.close();}
    }
}
