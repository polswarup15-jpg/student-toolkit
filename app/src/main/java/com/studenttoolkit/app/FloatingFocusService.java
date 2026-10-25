package com.studenttoolkit.app;
import android.app.*;import android.content.*;import android.graphics.Color;import android.graphics.PixelFormat;import android.graphics.drawable.GradientDrawable;import android.os.*;import android.provider.Settings;import android.view.*;import android.widget.*;
public class FloatingFocusService extends Service {
 public static final String ACTION_PLAY="com.studenttoolkit.app.focus.PLAY", ACTION_PAUSE="com.studenttoolkit.app.focus.PAUSE", ACTION_HIDE="com.studenttoolkit.app.focus.HIDE", ACTION_STOP="com.studenttoolkit.app.focus.STOP";
 private static final String CHANNEL="focus_timer"; private WindowManager wm; private View overlay; private TextView clock,status; private ImageButton play; private Handler handler=new Handler(Looper.getMainLooper()); private boolean running; private long accumulated,started; private WindowManager.LayoutParams params;
 private final Runnable tick=new Runnable(){public void run(){refresh();handler.postDelayed(this,500);}};
 @Override public void onCreate(){super.onCreate();NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);nm.createNotificationChannel(new NotificationChannel(CHANNEL,"Focus timer",NotificationManager.IMPORTANCE_LOW));wm=(WindowManager)getSystemService(WINDOW_SERVICE);accumulated=getSharedPreferences("floating_focus",0).getLong("elapsed",0);running=false;startForeground(22,notification());handler.post(tick);}
 private PendingIntent action(String a,int id){Intent i=new Intent(this,FloatingFocusService.class).setAction(a);return PendingIntent.getService(this,id,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);}
 private Notification notification(){return new Notification.Builder(this,CHANNEL).setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Student Toolkit focus").setContentText(running?"Timer running":"Timer paused").setOngoing(true).addAction(new Notification.Action.Builder(null,running?"Pause":"Play",action(running?ACTION_PAUSE:ACTION_PLAY,1)).build()).addAction(new Notification.Action.Builder(null,"Stop",action(ACTION_STOP,2)).build()).build();}
 private long elapsed(){return accumulated+(running?Math.max(0,SystemClock.elapsedRealtime()-started):0);}
 private void persist(){getSharedPreferences("floating_focus",0).edit().putLong("elapsed",elapsed()).apply();}
 private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
 private GradientDrawable bg(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(18));return d;}
 private void showOverlay(){
  if(overlay!=null||!Settings.canDrawOverlays(this))return;
  LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(10),dp(8),dp(10),dp(8));row.setElevation(dp(8));overlay=row;
  play=new ImageButton(this);play.setScaleType(ImageView.ScaleType.CENTER_INSIDE);play.setPadding(dp(12),dp(12),dp(12),dp(12));play.setBackground(bg(0xff7ed957));row.addView(play,new LinearLayout.LayoutParams(dp(48),dp(48)));
  LinearLayout labels=new LinearLayout(this);labels.setOrientation(LinearLayout.VERTICAL);labels.setPadding(dp(12),0,dp(8),0);
  clock=new TextView(this);clock.setTextSize(23);clock.setTypeface(null,1);labels.addView(clock);
  status=new TextView(this);status.setTextSize(13);labels.addView(status);
  row.addView(labels,new LinearLayout.LayoutParams(0,-2,1));
  ImageButton close=new ImageButton(this);close.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);close.setScaleType(ImageView.ScaleType.CENTER_INSIDE);close.setPadding(dp(10),dp(10),dp(10),dp(10));close.setBackground(bg(0xffd9d9d9));close.setContentDescription("Hide floating timer");row.addView(close,new LinearLayout.LayoutParams(dp(42),dp(46)));
  play.setOnClickListener(v->onStartCommand(new Intent(this,FloatingFocusService.class).setAction(running?ACTION_PAUSE:ACTION_PLAY),0,0));
  close.setOnClickListener(v->onStartCommand(new Intent(this,FloatingFocusService.class).setAction(ACTION_HIDE),0,0));
  int type=Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;
  int width=Math.min(dp(300),getResources().getDisplayMetrics().widthPixels-dp(24));
  params=new WindowManager.LayoutParams(width,-2,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);
  params.gravity=Gravity.TOP|Gravity.START;params.x=dp(12);params.y=dp(150);
  row.setOnTouchListener(new View.OnTouchListener(){float downX,downY;int x,y;public boolean onTouch(View v,MotionEvent e){switch(e.getAction()){case MotionEvent.ACTION_DOWN:downX=e.getRawX();downY=e.getRawY();x=params.x;y=params.y;return true;case MotionEvent.ACTION_MOVE:params.x=x+(int)(e.getRawX()-downX);params.y=y+(int)(e.getRawY()-downY);wm.updateViewLayout(overlay,params);return true;}return false;}});
  wm.addView(row,params);refresh();
 }
 private void refresh(){if(clock==null)return;long seconds=elapsed()/1000;clock.setText(String.format(java.util.Locale.US,"%02d:%02d",seconds/60,seconds%60));status.setText(running?"Playing · Time focused":"Paused · Time focused");play.setImageResource(running?android.R.drawable.ic_media_pause:android.R.drawable.ic_media_play);play.setContentDescription(running?"Pause focus timer":"Play focus timer");String theme=getSharedPreferences("appearance",0).getString("theme","Blue");int background=theme.equals("Dark")?0xff232a39:theme.equals("Green")?0xffe6f4e8:theme.equals("Purple")?0xfff2e9ff:0xffeaf2ff;int foreground=theme.equals("Dark")?Color.WHITE:theme.equals("Green")?0xff174a2d:theme.equals("Purple")?0xff4b2879:0xff17365e;overlay.setBackground(bg(background));clock.setTextColor(foreground);status.setTextColor(foreground);play.setColorFilter(0xff17365e);}
 @Override public int onStartCommand(Intent intent,int flags,int startId){String a=intent==null?"":intent.getAction();if(ACTION_STOP.equals(a)){if(running){accumulated=elapsed();running=false;}persist();removeOverlay();stopForeground(true);stopSelf();return START_NOT_STICKY;}if(ACTION_HIDE.equals(a)){removeOverlay();return START_STICKY;}if(ACTION_PAUSE.equals(a)&&running){accumulated=elapsed();running=false;persist();}else if(ACTION_PLAY.equals(a)&&!running){started=SystemClock.elapsedRealtime();running=true;}showOverlay();((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(22,notification());refresh();return START_STICKY;}
 private void removeOverlay(){if(overlay!=null){wm.removeView(overlay);overlay=null;clock=null;status=null;play=null;}}
 @Override public void onDestroy(){if(running){accumulated=elapsed();running=false;}persist();handler.removeCallbacks(tick);removeOverlay();super.onDestroy();}
 @Override public IBinder onBind(Intent i){return null;}
}
