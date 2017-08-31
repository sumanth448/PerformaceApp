package info.androidhive.recyclerview;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Debug;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.chainfire.libsuperuser.Shell;

public class DetailsActivity extends AppCompatActivity{
    GridLayout gl;
    TextView[] text;
    Toolbar toolbar1;
    int item;
    int i = 0;
    final int PERMISSION_READ_PACKAGE_USAGE_STATS_STATE = 0;
    final int PERMISSION_READ_PHONE_STATE = 1;
    final int PERMISSION_ALL = 2;
    private AppCompatDelegate delegate;
    String uid;
    NetworkStatsHelper networkStatsHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_main);
        toolbar1 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar1);
        toolbar1.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        String packagename = getIntent().getStringExtra("package_name");
        uid = getIntent().getStringExtra("uid");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE};
            if (!isAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }else {
                if (!hasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                } else {
                    showdata();
                }
            }
        }else{
            long txbytesused = TrafficStatsHelper.getPackageTxBytes(Integer.parseInt(uid));
            long rxbytesused = TrafficStatsHelper.getPackageRxBytes(Integer.parseInt(uid));
            TextView textView1 = (TextView) findViewById(R.id.wifitxbytes);
            textView1.setText("WifiUsageTxBytes : ");
            TextView textView2 = (TextView) findViewById(R.id.wifitxbytesvalue);
            textView2.setText(Long.toString(txbytesused));
            TextView textView3 = (TextView) findViewById(R.id.wifirxbytes);
            textView3.setText("WifiUsageRxBytes : ");
            TextView textView4 = (TextView) findViewById(R.id.wifirxbytesvalue);
            textView4.setText(Long.toString(rxbytesused));
        }
        toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        }
        return "";
    }

    public void showdata(){
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
        networkStatsHelper = new NetworkStatsHelper(networkStatsManager,Integer.parseInt(uid));
        long wifiusagetxbytes = networkStatsHelper.getPackageTxBytesWifi();
        long wifiusagerxbytes = networkStatsHelper.getPackageRxBytesWifi();
        long mobileusagetxbytes = networkStatsHelper.getPackageTxBytesMobile(getApplicationContext());
        long mobileusagerxbytes = networkStatsHelper.getPackageRxBytesMobile(getApplicationContext());

        // programmatically create a LineChart
        LineChart chart = new LineChart(getApplicationContext());


        TextView textView1 = (TextView) findViewById(R.id.wifitxbytes);
        textView1.setText("WifiUsageTxBytes :");
        TextView textView2 = (TextView) findViewById(R.id.wifitxbytesvalue);
        textView2.setText(Long.toString(wifiusagetxbytes));
        TextView textView3 = (TextView) findViewById(R.id.wifirxbytes);
        textView3.setText("WifiUsageRxBytes : ");
        TextView textView4 = (TextView) findViewById(R.id.wifirxbytesvalue);
        textView4.setText(Long.toString(wifiusagerxbytes));
        TextView textView5 = (TextView) findViewById(R.id.mobiletxbytes);
        textView5.setText("MobileUsageTxBytes :");
        TextView textView6 = (TextView) findViewById(R.id.mobiletxbytesvalue);
        textView6.setText(Long.toString(mobileusagetxbytes));
        TextView textView7 = (TextView) findViewById(R.id.mobilerxbytes);
        textView7.setText("MobileUsageRxBytes : ");
        TextView textView8 = (TextView) findViewById(R.id.mobilerxbytesvalue);
        textView8.setText(Long.toString(mobileusagerxbytes));
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailsActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("requestpermission", "Permission callback called-------");
        switch (requestCode) {
            case PERMISSION_ALL: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                //perms.put(Manifest.permission.PACKAGE_USAGE_STATS, PackageManager.PERMISSION_GRANTED);
                //perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                        Log.d("requestpermission",permissions[i]);
                        Log.d("requestpermission",Integer.toString(grantResults[i]));
                    }

                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("requestpermission", "read phone stats permission granted");
                        showdata();
                    } else {
                        Log.d("requestpermission", "read phone stats");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(DetailsActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                            showDialogOK("Read Phone stat and Package Usage stat Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    Intent intent = new Intent(DetailsActivity.this,MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void checkAndRequestPermissions(){
        String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE};
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}