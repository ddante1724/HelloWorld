package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import com.example.myapplication.BroadcastRecieverClass;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private TabItem tabLeft;
    private TabItem tabRight;
    private TabLayout tabLayout;
    private Button btnStartJob;
    private Button btnCancelJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        btnStartJob = findViewById(R.id.startJob);
        btnCancelJob = findViewById(R.id.endJob);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tabLeft = findViewById(R.id.tabItemL);
        tabRight = findViewById(R.id.tabItemR);
        tabLayout = findViewById(R.id.tabLayout);
        String getExtra = getIntent().getStringExtra("Try");
        BroadcastRecieverClass bcReceiver = new BroadcastRecieverClass();
        IntentFilter filter = new IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
        this.registerReceiver(bcReceiver, filter);
//      fragmentHolder = findViewById(R.id.fragmentPlaceHolder);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentPlaceHolder, new FragmentTop());
        fragmentTransaction.commit();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                if(tab.getPosition() == 0){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentPlaceHolder, new FragmentTop());
                    fragmentTransaction.commit();
                    Log.d("success", "Left Tab");
                }else if(tab.getPosition() == 1){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentPlaceHolder, new FragmentBottom());
                    fragmentTransaction.commit();
                    Log.d("success", "Right Tab");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            Log.d("success", "on Resume: Landscape");
        }else{
            Log.d("success", "onResume: Portrait");
        }
    }

    public void scheduleJob(View view){
        ComponentName componentName = new ComponentName(getApplicationContext(), MyJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000) //dilakukan setiap 15 menit
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.i(TAG, "scheduleJob: Job Scheduled");
        }else{
            Log.i(TAG, "scheduleJob: Job scheduling failed");
        }
    }

    public void cancelJob(View view){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.i(TAG, "cancelJob");
    }
}