package com.example.passwordwallet.ui.dashboard

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.passwordwallet.databinding.FragmentDashboardBinding
import com.example.passwordwallet.jobs.DeletePassword
import com.example.passwordwallet.jobs.GetPasswordsFromServer
import com.example.passwordwallet.room.AppDatabase
import com.example.passwordwallet.room.entities.Passwords
import com.example.passwordwallet.ui.menus.PasswordCardBottomSheet
import com.example.passwordwallet.utils.logout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        binding.swipeRefresh.setOnRefreshListener {
            launchJob()
        }
        binding.swipeRefresh.post {
            binding.swipeRefresh.isRefreshing = true
            launchJob()
        }
        return binding.root
    }
    private fun showBottomSheet(item: Passwords) {
        childFragmentManager.let {
            PasswordCardBottomSheet(item, ::onSelected).apply {
                show(it, "Bottom sheet")
            }
        }
    }
    private fun onSelected(item: Passwords, option: String) {
        when(option) {
            "copy" -> {
                val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(item.description, item.password)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(context, "Password copied.", Toast.LENGTH_SHORT).show()
            }
            "delete" -> {
                binding.swipeRefresh.isRefreshing = true
                val jobScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val bundle = PersistableBundle().apply {
                    putString("id", item.id.toString())
                }
                val jobInfo = JobInfo.Builder((0..Int.MAX_VALUE).random(),
                    ComponentName(requireContext(), DeletePassword::class.java)).apply {
                    setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    setExtras(bundle)
                }
                jobScheduler.schedule(jobInfo.build())
            }
        }
    }
    private fun launchJob() {
        val jobScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobInfo = JobInfo.Builder((0..Int.MAX_VALUE).random(),
        ComponentName(requireContext(), GetPasswordsFromServer::class.java)).apply {
            setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        }
        jobScheduler.schedule(jobInfo.build())
    }
    private fun setListItems() {
        lifecycleScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getInstance(requireContext())
            val items = database.passwordsDao().getPasswords()
            val adapter = RecyclerAdapter(items, ::showBottomSheet)
            lifecycleScope.launch {
                binding.dashboardRecyclerView.adapter = adapter
            }
        }
    }
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(passwordsReceiver, IntentFilter("PASSWORD-GET"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(passwordsReceiver)
    }
    private val passwordsReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val logout = intent?.extras?.getBoolean("logout")
            val message = intent?.extras?.getString("message")
            if (logout == true) {
                Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_SHORT).show()
                logout(requireContext(), lifecycleScope)
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                setListItems()
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
}