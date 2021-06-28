package com.example.passwordwallet

import android.app.AlertDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.passwordwallet.jobs.VerifyPassword
import com.example.passwordwallet.utils.logout
import com.google.android.material.textfield.TextInputEditText

class AskPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_password)
        showDialog()
    }

    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_ask_password, null)
        val builder = AlertDialog.Builder(this).apply {
            setTitle("Insert your password")
            setCancelable(false)
            setPositiveButton("OK") {
                    _, _ ->
                toggleLoadingLayout()
                val text = dialogView.findViewById<TextInputEditText>(R.id.askPassDialogInput).text.toString()
                val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val bundle = PersistableBundle().apply {
                    putString("password", text)
                }
                val jobInfo = JobInfo.Builder((0..Int.MAX_VALUE).random(),
                ComponentName(this@AskPasswordActivity, VerifyPassword::class.java))
                jobInfo.apply {
                    setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    setExtras(bundle)
                }
                jobScheduler.schedule(jobInfo.build())
            }
            setView(dialogView)
        }
        builder.create().show()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(verifiedReceiver, IntentFilter("IS-CONNECTED"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(verifiedReceiver)
    }

    private fun toggleLoadingLayout() {
        val layout = findViewById<LinearLayout>(R.id.loadingLayout)
        layout.visibility = when(layout.visibility) {
            View.GONE -> View.VISIBLE
            else -> View.GONE
        }
    }

    private val verifiedReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.extras?.getString("message")
            val logout = intent?.extras?.getBoolean("logout")
            val correct = intent?.extras?.getBoolean("correct")
            if (logout == true) {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                logout(this@AskPasswordActivity, lifecycleScope)
            } else {
                if (correct == true) {
                    val newIntent = Intent(this@AskPasswordActivity, BottomNavActivity::class.java)
                    newIntent.apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(newIntent)
                } else {
                    Toast.makeText(this@AskPasswordActivity, message, Toast.LENGTH_SHORT).show()
                    toggleLoadingLayout()
                    showDialog()
                }
            }
        }
    }
}