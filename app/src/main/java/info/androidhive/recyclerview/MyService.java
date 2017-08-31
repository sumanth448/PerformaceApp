package info.androidhive.recyclerview;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.TrafficStats;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import eu.chainfire.libsuperuser.Shell;

public class MyService extends Service {
    private final String DB_APP_PERF_TABLE = "app_performance_table";
    private final String COLUMN_NAME_PACKAGENAME = "package_name";
    private final String COLUMN_NAME_UUID = "app_uuid";
    private final String COLUMN_NAME_MEMORY = "app_memory";
    private final String COLUMN_NAME_DATAUSAGE_SEND = "data_usage_sent";
    private final String COLUMN_NAME_DATAUSAGE_RECEIVE = "data_usage_receive";
    private final String COLUMN_NAME_updated_time = "updated_at";
    public MyService() {}
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public int counter=0;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("in befor start command", "eneterd");
        super.onStartCommand(intent, flags, startId);
        Log.i("in after start command", "entered");
        startTimer();
        return START_STICKY;
    }
    /**
     * starts the timer
     */
    public void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        //set a new Timer
        timer = new Timer();
        Log.i("in eneted start timer", "eneterd");
        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 5000, 5000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                pushdatatoDB();
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }
    /**
     * Stops the Timer
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void pushdatatoDB(){
        final PackageManager pm = getPackageManager();
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List < String > stdout = Shell.SH.run("ps");
        List < String > packages = new ArrayList < > ();
        HashMap < String, String > hashmap = new HashMap < > ();
        for (String line: stdout) {
            // Get the process-name. It is the last column.
            System.out.println("line is: " + line);
            String[] arr = line.split("\\s+");
            String processName = arr[arr.length - 1].split(":")[0];
            String pid = arr[1].split(":")[0].toString();
            System.out.println("processname is :" + processName);
            System.out.println("pid is :" + pid);
            int processPID[] = new int[1];
            if (pid.matches("[-+]?\\d*\\.?\\d+"))
                processPID[0] = Integer.parseInt(pid);
            Debug.MemoryInfo[] memInfo = am.getProcessMemoryInfo(processPID);
            System.out.println("memory inf0 : " + memInfo[0].getTotalPss());
            hashmap.put(processName, Integer.toString(memInfo[0].getTotalPss()));
            packages.add(processName);
        }
        // Get a list of all installed apps on the device.
        List < ApplicationInfo > apps = pm.getInstalledApplications(0);

        // Remove apps which are not running.
        for (Iterator < ApplicationInfo > it = apps.iterator(); it.hasNext();) {
            if (!packages.contains(it.next().packageName)) {
                it.remove();
            }
        }
        for (ApplicationInfo app: apps) {
            ContentValues contentvalue = new ContentValues();
            String appName = app.loadLabel(pm).toString();
            String processName = app.processName;
            String memory ="";
            if(hashmap.containsKey(processName)){
                memory = hashmap.get(processName);
            }else if(hashmap.containsKey(appName)){
                memory = hashmap.get(appName);
            }
            int uid = app.uid;
            long ulBytes = TrafficStats.getUidTxBytes(uid);
            long dlBytes = TrafficStats.getUidRxBytes(uid);
            contentvalue.put(COLUMN_NAME_PACKAGENAME,processName);
            contentvalue.put(COLUMN_NAME_UUID, Integer.toString(uid));
            contentvalue.put(COLUMN_NAME_MEMORY,memory);
            contentvalue.put(COLUMN_NAME_DATAUSAGE_SEND,Long.toString(ulBytes));
            contentvalue.put(COLUMN_NAME_DATAUSAGE_RECEIVE,Long.toString(dlBytes));
            contentvalue.put(COLUMN_NAME_updated_time,Long.toString(Calendar.getInstance().getTimeInMillis()));
            System.out.println("conettetetet: " + contentvalue);
            Database.getInstance(getApplicationContext()).insertStatusUpdate(contentvalue);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        //Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        //sendBroadcast(broadcastIntent);
        stoptimertask();
    }
    /*@Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("EXIT", "onTaskRemoved!");
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, rootIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 3*1000, pintent);
    }*/
}