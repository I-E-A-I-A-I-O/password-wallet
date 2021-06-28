package com.example.passwordwallet.utils

import android.content.Context
import android.content.Intent
import com.example.passwordwallet.MainActivity
import com.example.passwordwallet.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun logout(context: Context, scope: CoroutineScope) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    scope.launch(Dispatchers.IO) {
        val database = AppDatabase.getInstance(context)
        database.passwordsDao().deletePasswordTable()
        database.userDao().deleteUserTable()
    }
    context.startActivity(intent)
}
