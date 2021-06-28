package com.example.passwordwallet.ui.addPassword

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.afollestad.vvalidator.form
import com.example.passwordwallet.databinding.FragmentAddPasswordBinding
import com.example.passwordwallet.jobs.SendPasswordToServer
import com.example.passwordwallet.utils.logout
import kotlin.random.Random

class AddPasswordFragment : Fragment() {
    private lateinit var binding: FragmentAddPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPasswordBinding.inflate(inflater, container, false)
        form {
            useRealTimeValidation(disableSubmit = true)
            inputLayout(binding.passwordDescriptionLayout) {
                isNotEmpty().description("Description is required!")
            }
            inputLayout(binding.newPasswordLayout) {
                isNotEmpty().description("Password is required!")
                length().atLeast(5).atMost(30).description("Password length must be between 5 and 30 characters!")
            }
            inputLayout(binding.accountPasswordLayout) {
                isNotEmpty().description("Account password is required!")
            }
            submitWith(binding.submitButton) {
                toggleLoading()
                submit()
            }
        }
        return binding.root
    }
    private fun submit() {
        val description = binding.passwordDescriptionInput.text.toString()
        val password = binding.newPasswordInput.text.toString()
        val accountPass = binding.accountPasswordInput.text.toString()
        val jobScheduler = activity?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val bundle = PersistableBundle().apply {
            putString("description", description)
            putString("password", password)
            putString("accPass", accountPass)
        }
        val jobInfo = JobInfo.Builder((0..Int.MAX_VALUE).random(),
            ComponentName(requireContext(), SendPasswordToServer::class.java)).apply {
            setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            setExtras(bundle)
        }
        jobScheduler.schedule(jobInfo.build())
    }
    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(submitReceiver, IntentFilter("PASSWORD-POSTED"))
    }
    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(submitReceiver)
    }
    private fun toggleLoading() {
        binding.submitButton.isEnabled = !binding.submitButton.isEnabled
        binding.progressBar.visibility = when(binding.progressBar.visibility) {
            View.GONE -> View.VISIBLE
            else -> View.GONE
        }
    }
    private val submitReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            toggleLoading()
            val message = intent?.extras?.getString("message")
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            val logout = intent?.extras?.getBoolean("logout")
            if (logout == true) {
                logout(requireContext(), lifecycleScope)
            }
        }
    }
}