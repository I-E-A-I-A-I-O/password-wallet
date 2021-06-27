package com.example.passwordwallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
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
            val intent: Intent
            if (users.isEmpty()) {
                intent = Intent(this@LauncherActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            } else {
                intent = Intent(this@LauncherActivity, BottomNav::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            lifecycleScope.launch {
                startActivity(intent)
            }
        }
    }
}