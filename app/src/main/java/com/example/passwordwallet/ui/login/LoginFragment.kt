package com.example.passwordwallet.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.passwordwallet.BottomNav
import com.example.passwordwallet.R
import com.example.passwordwallet.databinding.LoginFragmentBinding
import com.example.passwordwallet.requests.login
import com.example.passwordwallet.requests.types.Login
import com.example.passwordwallet.requests.types.OKLogin
import com.example.passwordwallet.room.AppDatabase
import com.example.passwordwallet.room.entities.User
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.goToRegister.setOnClickListener {
            this.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        form {
            useRealTimeValidation(disableSubmit = true)
            inputLayout(binding.loginEmailLayout) {
                isEmail().description("Insert a valid email!")
                isNotEmpty().description("Email is required!")
            }
            inputLayout(binding.loginPasswordLayout) {
                isNotEmpty().description("Password is required!")
            }
            submitWith(binding.loginButton) {
                requestLogin()
            }
        }
        return binding.root
    }
    private fun requestLogin() {
        toggleLoading()
        val email = binding.loginEmailInput.text.toString().lowercase()
        val password = binding.loginPasswordInput.text.toString()
        lifecycleScope.launch {
            val logObject = Login(email, password)
            login(logObject) {
                accepted, message, data ->
                if (!accepted) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                } else {
                    val db = AppDatabase.getInstance(requireContext())
                    val user = User(
                        name = data?.name!!,
                        email = email,
                        token = data.token!!
                    )
                    db.userDao().insertUser(user)
                    val intent = Intent(context, BottomNav::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    activity?.startActivity(intent)
                }
            }
        }
    }
    private fun toggleLoading() {
        binding.loginButton.isEnabled = !binding.loginButton.isEnabled
        binding.progressBar.visibility = when(binding.progressBar.visibility) {
            View.GONE -> View.VISIBLE
            else -> View.GONE
        }
    }
}