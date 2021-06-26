package com.example.passwordwallet.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.afollestad.vvalidator.form
import com.example.passwordwallet.R
import com.example.passwordwallet.databinding.RegisterFragmentBinding
import com.example.passwordwallet.requests.types.User
import com.example.passwordwallet.secure.encrypt
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: RegisterFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegisterFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        form {
            useRealTimeValidation(disableSubmit = true)
            inputLayout(binding.registerEmailLayout) {
                isEmail().description("Insert a valid email!")
            }
            inputLayout(binding.registerNameLayout) {
                isNotEmpty().description("Name is required.")
                length().atMost(30).description("Name too long.")
            }
            inputLayout(binding.registerPasswordLayout) {
                length().atLeast(5).atMost(30).description("At least 5 characters, at most 30 characters long.")
            }
            submitWith(binding.registerButton) {
                onSubmit()
            }
        }
        return binding.root
    }
    private fun onSubmit() {
        val name = binding.registerNameInput.text.toString()
        val email = binding.registerEmailInput.text.toString()
        val password = binding.registerPasswordInput.text.toString()
        lifecycleScope.launch {
            val encrypted = encrypt(password)
            Log.d("TEST", encrypted[0])
        }
    }
}