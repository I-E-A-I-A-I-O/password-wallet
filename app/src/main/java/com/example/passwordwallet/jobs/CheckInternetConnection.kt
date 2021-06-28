package com.example.passwordwallet.jobs

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.passwordwallet.requests.testConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckInternetConnection: JobService() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartJob(params: JobParameters?): Boolean {
        coroutineScope.launch {
            val isConnected = testConnection()
            val intent = Intent("IS-CONNECTED").apply {
                putExtra("is_connected", isConnected)
            }
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            jobFinished(params, false)
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}