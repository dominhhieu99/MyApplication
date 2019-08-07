package com.example.simpleapplocker;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class BackgroundServices extends Service {

    private BroadcastReceiver receiver;
    public int counter=0;
    LockScreen obj = new LockScreen();
    // context is important
    // every gui or view or activity have context
    // i will use context of NotificationService class
    Context mContext;
    // database class
    Apply_password_on_AppDatabase db = new Apply_password_on_AppDatabase(this);


    // flag is used for stopping or running loop check of
    // current app running
    static  int flag = 0 ;
    static int flag2 = 0;

    // when any app is lanuch and it have password set on it
    // that app name save in current_app varaible
    String current_app = "";

    public BackgroundServices (){}




    @Override
    public void onCreate() {
        super.onCreate();
        // add context of NotificationService to mContext variable
        mContext = this;



        // oreo used different approach for background services
        // other use same approach

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    //  oreo api approach

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestartService.class);
        this.sendBroadcast(broadcastIntent);
    }


    // set timer of one second repeat itself
    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {


                if (flag == 0) {

                    // get data from database
                    Cursor res = db.getAllData();
                    // create two list of name or pass
                    ArrayList<String> name = new ArrayList<String>();
                    ArrayList<String> pass = new ArrayList<String>();

                    while (res.moveToNext()) {
                        // add data to list
                        Log.i("Count", "=========  " + printForegroundTask());
                        name.add(res.getString(0));
                        pass.add(res.getString(1));
                    }

                    // if current app have password set on it
                    // lanuch lock screen

                    if (name.contains(printForegroundTask())) {
                        // flag = 1 means stop loop
                        flag = 1;




                        current_app = printForegroundTask();
                        Intent lockIntent = new Intent(mContext, LockScreen.class);
                        lockIntent.putExtra("name", pass.get(name.indexOf(printForegroundTask())));
                        lockIntent.putExtra("pack", printForegroundTask());
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(lockIntent);

                    }

                }


                if ((!printForegroundTask().equals("com.example.simpleapplocker"))  &&  flag2 == 0 ) {
                    if ((!printForegroundTask().equals(current_app))) {

                        flag = 0;
                    }

                }


                // if security provider is running
                // then dont lanuch lock screen
                // only one time lock screen will appear

                if (printForegroundTask().equals("com.example.simpleapplocker")) {

                    flag = 2;
                }




                // Log.i("Count", "=========  "+ printForegroundTask());
            }
        };
        timer.schedule(timerTask, 0, 100); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    // get string of current app running
    private String printForegroundTask() {
        String currentApp = "NULL";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;

        }

        // Log.e("AppLockerService", "Current App in foreground is: " + currentApp);
        return currentApp;
    }
}