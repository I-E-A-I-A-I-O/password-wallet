package com.example.passwordwallet

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.passwordwallet.jobs.CheckInternetConnection
import com.example.passwordwallet.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(this@LauncherActivity)
            val users = db.userDao().getUser()
            if (users.isEmpty()) {
                val intent = Intent(this@LauncherActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                lifecycleScope.launch {
                    startActivity(intent)
                }
            } else {
                val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val jobInfo = JobInfo.Builder((0..Int.MAX_VALUE).random(),
                ComponentName(this@LauncherActivity, CheckInternetConnection::class.java)).apply {
                    setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                }
                jobScheduler.schedule(jobInfo.build())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(isConnectedReceiver, IntentFilter("IS-CONNECTED"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(isConnectedReceiver)
    }

    private val isConnectedReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isConnected = intent?.extras?.getBoolean("is_connected")
            val newIntent: Intent
            if (isConnected == true) {
                newIntent = Intent(this@LauncherActivity, AskPasswordActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            } else {
                newIntent = Intent(this@LauncherActivity, BottomNavActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            lifecycleScope.launch {
                startActivity(newIntent)
            }
        }
    }
}