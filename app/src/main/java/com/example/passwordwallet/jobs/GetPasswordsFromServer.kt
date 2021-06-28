package com.example.passwordwallet.jobs

import android.app.job.JobParameters
import android.app.job.JobService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class GetPasswordsFromServer: JobService() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartJob(params: JobParameters?): Boolean {

        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}