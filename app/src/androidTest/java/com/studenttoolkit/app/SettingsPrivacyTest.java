package com.studenttoolkit.app;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SettingsPrivacyTest {
 @Test public void privacyManifestDisablesBackupAndUsesOnlyExpectedPermissions() throws Exception {
  Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
  PackageManager pm=ctx.getPackageManager();
  ApplicationInfo app=pm.getApplicationInfo(ctx.getPackageName(),0);
  assertEquals(0,app.flags & ApplicationInfo.FLAG_ALLOW_BACKUP);
  PackageInfo pkg=pm.getPackageInfo(ctx.getPackageName(),PackageManager.GET_PERMISSIONS);
  java.util.Set<String> actual=new java.util.HashSet<>();
  if(pkg.requestedPermissions!=null)java.util.Collections.addAll(actual,pkg.requestedPermissions);
  java.util.Set<String> expected=new java.util.HashSet<>();
  expected.add("android.permission.SYSTEM_ALERT_WINDOW");
  expected.add("android.permission.FOREGROUND_SERVICE");
  expected.add("android.permission.FOREGROUND_SERVICE_SPECIAL_USE");
  assertEquals(expected,actual);
 }
 @Test public void strictModePreferencePersistsAcrossContextReads() {
  Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
  android.content.SharedPreferences prefs=ctx.getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
  boolean original=prefs.getBoolean("strict_mode",false);
  try {
   assertTrue(prefs.edit().putBoolean("strict_mode",true).commit());
   Context freshContext=ctx.createPackageContext(ctx.getPackageName(),0);
   assertTrue(freshContext.getSharedPreferences("MainActivity",Context.MODE_PRIVATE).getBoolean("strict_mode",false));
   assertTrue(prefs.edit().putBoolean("strict_mode",false).commit());
   assertFalse(freshContext.getSharedPreferences("MainActivity",Context.MODE_PRIVATE).getBoolean("strict_mode",true));
  }catch(android.content.pm.PackageManager.NameNotFoundException ex){throw new AssertionError(ex);}
  finally{prefs.edit().putBoolean("strict_mode",original).commit();}
 }
}