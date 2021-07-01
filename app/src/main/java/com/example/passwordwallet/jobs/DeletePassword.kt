package com.example.passwordwallet.jobs

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.passwordwallet.requests.deletePassword
import com.example.passwordwallet.requests.isTokenValid
import com.example.passwordwallet.requests.refreshToken
import com.example.passwordwallet.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DeletePassword: JobService() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartJob(params: JobParameters?): Boolean {
        coroutineScope.launch {
            val database = AppDatabase.getInstance(applicationContext)
            val user = database.userDao().getUser()[0]
            val id = params?.extras?.getString("id")
            database.passwordsDao().deleteById(UUID.fromString(id))
            val isTokenValid = isTokenValid(user.token)
            if (isTokenValid) {
                doDeletePassword(user.token, params?.extras!!)
            } else {
                val refreshedToken = refreshToken(user.refreshToken)
                if (refreshedToken.success) {
                    database.userDao().updateAccessToken(refreshedToken.token!!, user.token)
                    doDeletePassword(refreshedToken.token, params?.extras!!)
                } else {
                    Log.d("Token refresh error",
                        "Token refresh was unsuccessful and the job 'SendPasswordToServer' wasn't completed was aborted.")
                    sendBroadcast("Session expired.", true)
                }
            }
            jobFinished(params, false)
        }
        return true
    }

    private fun doDeletePassword(token: String, extras: PersistableBundle) {
        val id = extras.getString("id")!!
        val response = deletePassword(id, token)
        sendBroadcast(response.message, false)
    }

    private fun sendBroadcast(message: String, shouldLogout: Boolean) {
        val intent = Intent("PASSWORD-GET").apply {
            putExtra("message", message)
            putExtra("logout", shouldLogout)
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}