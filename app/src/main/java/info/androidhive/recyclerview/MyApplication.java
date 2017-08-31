package info.androidhive.recyclerview;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sumanth.reddy on 27/02/17.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApp", "onCreate!");
        //add your sevice heremServiceIntent = new Intent(MainActivity.this, MyService.class);
        //Intent mServiceIntent = new Intent(this, MyService.class);
        //startService(mServiceIntent);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("MyApp", "onLowMemory!");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d("MyApp", "onTrimMemory!");
    }
}
