package com.example.passwordwallet.jobs

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.passwordwallet.requests.getPasswordsFromServer
import com.example.passwordwallet.requests.isTokenValid
import com.example.passwordwallet.requests.refreshToken
import com.example.passwordwallet.room.AppDatabase
import com.example.passwordwallet.room.entities.Passwords
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class GetPasswordsFromServer: JobService() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartJob(params: JobParameters?): Boolean {
        coroutineScope.launch {
            val database = AppDatabase.getInstance(applicationContext)
            val user = database.userDao().getUser()[0]
            val isTokenValid = isTokenValid(user.token)
            if (isTokenValid) {
                getPasswords(user.token)
            } else {
                val refreshedToken = refreshToken(user.refreshToken)
                if (refreshedToken.success) {
                    database.userDao().updateAccessToken(refreshedToken.token!!, user.token)
                    getPasswords(refreshedToken.token)
                } else {
                    Log.d("Token refresh error",
                        "Token refresh was unsuccessful and the job 'GetPasswordFromServer' wasn't completed was aborted.")
                    sendBroadcast("Session expired.", true)
                }
            }
            jobFinished(params, false)
        }
        return true
    }
    private fun getPasswords(token: String) {
        val database = AppDatabase.getInstance(applicationContext)
        val response = getPasswordsFromServer(token)
        if (response.success) {
            val currentPasswords = database.passwordsDao().getPasswords()
            var fromServer = listOf<Passwords>()
            for (pass in response.content) {
                fromServer = fromServer.plus(
                    Passwords(UUID.fromString(pass.id), pass.description, pass.password))
            }
            if(currentPasswords.isNotEmpty()) {
                val unregistered = listOf<Passwords>()
                for (pass in fromServer) {
                    val size = currentPasswords.filter { it.id == pass.id }.size
                    if (size == 0) {
                        unregistered.plus(pass)
                    }
                }
                database.passwordsDao().insertPasswords(*unregistered.toTypedArray())
            } else {
                database.passwordsDao().insertPasswords(*fromServer.toTypedArray())
            }
            sendBroadcast("Completed.", false)
        } else {
            sendBroadcast(response.message, false)
        }
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