package info.androidhive.recyclerview;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Performance> movieList = new ArrayList<>();
    List<Performance> filteredList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PerformanceAdapter mAdapter;
    public EditText search;
    private MyService myService;
    private Intent mServiceIntent;
    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    Performance movie1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        initdbProperties();
        myService = new MyService();
        mServiceIntent = new Intent(MainActivity.this, MyService.class);
        if (!isMyServiceRunning(myService.getClass())) {
            //startService(mServiceIntent);
        }
        /*Intent intent = new Intent(MainActivity.this, MyService.class);
        PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 30*1000, pintent);*/
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        search = (EditText) findViewById( R.id.search);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new PerformanceAdapter(movieList,this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        addTextListener();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Performance movie;
                if(filteredList.size() > 0) {
                     movie = filteredList.get(position);
                }else{
                     movie = movieList.get(position);
                }
                Toast.makeText(getApplicationContext(), movie.getTitle() + movie.getGenre() + movie.getYear() +" is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("package_name",movie.getTitle());
                intent.putExtra("uid",movie.getYear());
                startActivity(intent);
                finish();
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareMovieData();
    }

    private void prepareMovieData() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);
        int UID;
        Performance movie;
        if(packages.size() > 0){
            for (ApplicationInfo packageInfo : packages) {
                movie = new Performance(packageInfo.packageName,String.valueOf(packageInfo.targetSdkVersion),String.valueOf(packageInfo.uid));
                movieList.add(movie);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    public void addTextListener(){

        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                 filteredList = new ArrayList<>();

                for (int i = 0; i < movieList.size(); i++) {
                    String text = movieList.get(i).getTitle();
                    if (text.contains(query)) {
                        movie1 = new Performance(movieList.get(i).getTitle(),movieList.get(i).getGenre(),movieList.get(i).getYear());
                        filteredList.add(movie1);
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                mAdapter = new PerformanceAdapter(filteredList, MainActivity.this);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }
    void initdbProperties(){
        Database.getInstance(getApplicationContext());
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

}
