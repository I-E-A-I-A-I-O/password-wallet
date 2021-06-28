package com.example.passwordwallet.jobs

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.passwordwallet.requests.isPasswordValid
import com.example.passwordwallet.requests.isTokenValid
import com.example.passwordwallet.requests.refreshToken
import com.example.passwordwallet.room.AppDatabase
import com.example.passwordwallet.utils.SessionValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerifyPassword: JobService() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartJob(params: JobParameters?): Boolean {
        coroutineScope.launch {
            val database = AppDatabase.getInstance(applicationContext)
            val user = database.userDao().getUser()[0]
            val password = params?.extras?.getString("password")!!
            val isValid = isTokenValid(user.token)
            if (isValid) {
                doVerification(user.token, password)
            } else {
                val refresh = refreshToken(user.refreshToken)
                if (refresh.success) {
                    database.userDao().updateAccessToken(refresh.token!!, user.token)
                    doVerification(refresh.token, password)
                } else {
                    sendBroadcast("Session expired.", shouldLogout = true, correctPass = false)
                }
            }
            jobFinished(params, false)
        }
        return true
    }
    private fun doVerification(token: String, password: String) {
        val isValid = isPasswordValid(password, token)
        if (isValid.valid) {
            SessionValues.password = password
        }
        sendBroadcast(isValid.message, false, isValid.valid)
    }
    private fun sendBroadcast(message: String, shouldLogout: Boolean, correctPass: Boolean) {
        val intent = Intent("IS-CONNECTED").apply {
            putExtra("message", message)
            putExtra("logout", shouldLogout)
            putExtra("correct", correctPass)
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}