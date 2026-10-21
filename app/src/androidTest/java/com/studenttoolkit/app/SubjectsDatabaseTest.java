package com.studenttoolkit.app;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SubjectsDatabaseTest {
    private Store freshStore() {
        android.content.Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase("student_toolkit.db");
        return new Store(context);
    }
    private ContentValues subject(String name, String tag) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("tag", tag); return v;
    }
    @Test public void addEditDeleteAndTagsPersist() {
        Store store = freshStore();
        try {
            store.add("subjects", subject("Accountancy", "Commerce"));
            assertEquals(1, store.count("subjects"));
            long id;
            try (Cursor c = store.all("subjects")) {
                assertTrue(c.moveToFirst());
                id = c.getLong(c.getColumnIndexOrThrow("_id"));
                assertEquals("Commerce", c.getString(c.getColumnIndexOrThrow("tag")));
            }
            store.update("subjects", id, subject("Accounts", "Core"));
            try (Cursor c = store.one("subjects", id)) {
                assertTrue(c.moveToFirst());
                assertEquals("Accounts", c.getString(c.getColumnIndexOrThrow("name")));
                assertEquals("Core", c.getString(c.getColumnIndexOrThrow("tag")));
            }
            store.close();
            store = new Store(InstrumentationRegistry.getInstrumentation().getTargetContext());
            assertEquals(1, store.count("subjects"));
            store.delete("subjects", id);
            assertEquals(0, store.count("subjects"));
        } finally { store.close(); }
    }
    @Test public void duplicateNamesIgnoreCaseAndWhitespaceButExcludeCurrentRecord() {
        Store store = freshStore();
        try {
            store.add("subjects", subject("Economics", "Theory"));
            long id;
            try (Cursor c = store.all("subjects")) {
                assertTrue(c.moveToFirst());
                id = c.getLong(c.getColumnIndexOrThrow("_id"));
            }
            assertTrue(store.subjectNameExists(" economics ", -1));
            assertTrue(store.subjectNameExists("ECONOMICS", -1));
            assertFalse(store.subjectNameExists("Economics", id));
            assertFalse(store.subjectNameExists("History", -1));
        } finally { store.close(); }
    }
    @Test public void subjectsAppearInTimerPicker() {
        Store store = freshStore();
        try {
            store.add("subjects", subject("Physics", "Science"));
            try (Cursor c = store.subjectsForTimer()) {
                assertTrue(c.moveToFirst());
                assertEquals("Physics", c.getString(0));
            }
        } finally { store.close(); }
    }
    @Test public void upgradesV4SubjectsWithoutLosingRecords() {
        android.content.Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ctx.deleteDatabase("student_toolkit.db");
        android.database.sqlite.SQLiteDatabase old = ctx.openOrCreateDatabase("student_toolkit.db", 0, null);
        old.execSQL("CREATE TABLE subjects (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL)");
        old.execSQL("INSERT INTO subjects(name) VALUES ('Legacy subject')");
        old.setVersion(4);
        old.close();
        Store store = new Store(ctx);
        try (Cursor c = store.all("subjects")) {
            assertTrue(c.moveToFirst());
            assertEquals("Legacy subject", c.getString(c.getColumnIndexOrThrow("name")));
            assertEquals("", c.getString(c.getColumnIndexOrThrow("tag")));
        } finally { store.close(); }
    }
}
