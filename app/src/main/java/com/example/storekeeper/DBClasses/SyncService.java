package com.example.storekeeper.DBClasses;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class SyncService extends Service {

    private Handler mHandler;
    private Timer mTimer;
    private DBHelper mDBHelper;
    private MySQLiteHelper mMySQLiteHelper;
    private MySQLHelper mMySQLHelper;

    private static final int SYNC_INTERVAL = 1000 * 60; //* 5; // Περίοδος ελέγχου σε 5 λεπτά
    private static final String TAG = "SyncService";

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mTimer = new Timer();
        mDBHelper = new DBHelper(this);
        mMySQLiteHelper = new MySQLiteHelper(this);
        mMySQLHelper = new MySQLHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(getApplicationContext(), "Starting sync...", Toast.LENGTH_SHORT).show();
        mTimer.scheduleAtFixedRate(new SyncTask(), 0, SYNC_INTERVAL);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class SyncTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Starting sync...");

                    Toast.makeText(getApplicationContext(), "Starting sync..." + mMySQLHelper.connect(), Toast.LENGTH_SHORT).show();
                    // Έλεγχος για αλλαγές στη βάση δεδομένων SQLite
                    if (mDBHelper.hasChanges()) {
                        // Συγχρονισμός με τη βάση δεδομένων MySQL
                        mMySQLiteHelper.syncWithMySQL(mMySQLHelper);
                    } else {
                        Log.d(TAG, "No changes to sync.");
                        Toast.makeText(getApplicationContext(), "No changes to sync.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}


