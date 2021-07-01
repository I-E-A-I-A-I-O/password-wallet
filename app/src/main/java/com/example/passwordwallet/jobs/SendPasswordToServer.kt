package com.example.passwordwallet.jobs

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.passwordwallet.requests.Api
import com.example.passwordwallet.requests.insertPassword
import com.example.passwordwallet.requests.isTokenValid
import com.example.passwordwallet.requests.refreshToken
import com.example.passwordwallet.requests.types.requests.PostPassword
import com.example.passwordwallet.room.AppDatabase
import com.example.passwordwallet.room.entities.Passwords
import com.example.passwordwallet.ui.addPassword.AddPasswordFragment
import kotlinx.coroutines.*
import java.util.*

class SendPasswordToServer: JobService() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartJob(params: JobParameters?): Boolean {
        coroutineScope.launch {
            val database = AppDatabase.getInstance(applicationContext)
            val user = database.userDao().getUser()[0]
            val isTokenValid = isTokenValid(user.token)
            if (isTokenValid) {
                sendPassword(user.token, params?.extras!!)
            } else {
                val refreshedToken = refreshToken(user.refreshToken)
                if (refreshedToken.success) {
                    database.userDao().updateAccessToken(refreshedToken.token!!, user.token)
                    sendPassword(refreshedToken.token, params?.extras!!)
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

    private fun sendPassword(token: String, extras: PersistableBundle) {
        val database = AppDatabase.getInstance(applicationContext)
        val description = extras.getString("description")!!
        val password = extras.getString("password")!!
        val accountPassword = extras.getString("accPass")!!
        val localPassword = Passwords(
            description = description,
            password = password
        )
        database.passwordsDao().insertPassword(localPassword)
        val postPassword = PostPassword(description, password, accountPassword)
        val response = insertPassword(token, postPassword)
        if (response.success) {
            database.passwordsDao().updateId(UUID.fromString(response.id), localPassword.id)
        }
        sendBroadcast(response.message, false)
    }

    private fun sendBroadcast(message: String, shouldLogout: Boolean) {
        val intent = Intent("PASSWORD-POSTED").apply {
            putExtra("message", message)
            putExtra("logout", shouldLogout)
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}