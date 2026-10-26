package com.studenttoolkit.app;

import android.app.Instrumentation;
import android.content.Intent;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Regression smoke test: each implemented screen must render without crashing. */
@RunWith(AndroidJUnit4.class)
public class NavigationSmokeTest {
    @Test public void everyDeclaredScreenRendersAndBottomTabsHaveAccessibleLabels() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Intent intent = new Intent(instrumentation.getTargetContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(intent);
        try {
            instrumentation.waitForIdleSync();
            assertEquals("Dashboard", activity.pages[activity.page]);
            for (int index = 0; index < activity.pages.length; index++) {
                final int destination = index;
                instrumentation.runOnMainSync(() -> activity.show(destination));
                instrumentation.waitForIdleSync();
                assertEquals("Wrong screen selected", destination, activity.page);
                assertNotNull("Screen has no content container", activity.body);
                assertTrue("Screen has no content", activity.body.getChildCount() > 0);
                assertEquals("Expected four primary bottom tabs", 4, activity.nav.getChildCount());
                for (int tab = 0; tab < activity.nav.getChildCount(); tab++) {
                    TextView item = (TextView) activity.nav.getChildAt(tab);
                    assertTrue("Bottom tab lacks accessibility label",
                        item.getContentDescription() != null && item.getContentDescription().length() > 0);
                }
            }
        } finally {
            instrumentation.runOnMainSync(activity::finish);
        }
    }
}
