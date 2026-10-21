package com.studenttoolkit.app;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.CheckBox;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SettingsPrivacyTest {
 @Test public void privacyManifestDisablesBackupAndHasNoPermissions() throws Exception {
  Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
  PackageManager pm=ctx.getPackageManager();
  ApplicationInfo app=pm.getApplicationInfo(ctx.getPackageName(),0);
  assertEquals(0,app.flags & ApplicationInfo.FLAG_ALLOW_BACKUP);
  PackageInfo pkg=pm.getPackageInfo(ctx.getPackageName(),PackageManager.GET_PERMISSIONS);
  assertTrue(pkg.requestedPermissions==null || pkg.requestedPermissions.length==0);
 }
 @Test public void strictModePreferenceSurvivesActivityRecreation() throws Exception {
  Instrumentation inst=InstrumentationRegistry.getInstrumentation();
  Context ctx=inst.getTargetContext();
  ctx.getSharedPreferences("MainActivity",Context.MODE_PRIVATE).edit().putBoolean("strict_mode",true).commit();
  Intent intent=new Intent(ctx,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  MainActivity activity=(MainActivity)inst.startActivitySync(intent);
  try {
   assertTrue(activity.strictMode);
   inst.runOnMainSync(()->activity.show(8));
   inst.waitForIdleSync();
   assertTrue(activity.strictMode);
   assertTrue(ctx.getSharedPreferences("MainActivity",Context.MODE_PRIVATE).getBoolean("strict_mode",false));
  }finally{
   inst.runOnMainSync(activity::finish);
   ctx.getSharedPreferences("MainActivity",Context.MODE_PRIVATE).edit().remove("strict_mode").commit();
  }
 }
}