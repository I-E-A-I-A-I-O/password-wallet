package com.example.passwordwallet.ui.generatePassword

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.passwordwallet.databinding.FragmentGeneratePasswordBinding
import kotlin.random.Random

private const val LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz"
private const val UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
private const val NUMBERS = "1234567890"
private const val SPECIAL_CHARACTERS = "!\"#$%&/()=?¡+*{[}]}-_.:;,|°¬@·~\\'"

class GeneratePasswordFragment : Fragment() {
    private lateinit var binding: FragmentGeneratePasswordBinding
    private var baseString = LOWERCASE_LETTERS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGeneratePasswordBinding.inflate(inflater, container, false)
        binding.capsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked and isLastActive()) {
                binding.minusSwitch.isChecked = true
            }
        }
        binding.numbersSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked and isLastActive()) {
                binding.minusSwitch.isChecked = true
            }
        }
        binding.specialCharsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked and isLastActive()) {
                binding.minusSwitch.isChecked = true
            }
        }
        binding.minusSwitch.setOnCheckedChangeListener { view, isChecked ->
            if (!isChecked and isLastActive()) {
                view.isChecked = true
            }
        }
        binding.generatePasswordButton.setOnClickListener {
            setBaseString()
            generateString()
        }
        binding.materialCardView2.setOnClickListener {
            val text = binding.generatedPasswordTextView.text
            if (text.isNotEmpty()) {
                val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Generated password", text)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context, "Text copied.", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
    private fun generateString() {
        binding.generatePasswordButton.isEnabled = false
        val length = binding.passwordLengthSeekBar.progress
        var string = ""
        var pos: Int
        repeat(length) {
            pos = Random.nextInt(0, baseString.length)
            string += baseString[pos]
        }
        binding.generatedPasswordTextView.text = string
        binding.generatePasswordButton.isEnabled = true
    }
    private fun setBaseString() {
        var temp = ""
        if (binding.minusSwitch.isChecked) {
            temp += LOWERCASE_LETTERS
        }
        if (binding.capsSwitch.isChecked) {
            temp += UPPERCASE_LETTERS
        }
        if (binding.numbersSwitch.isChecked) {
            temp += NUMBERS
        }
        if (binding.specialCharsSwitch.isChecked) {
            temp += SPECIAL_CHARACTERS
        }
        baseString = temp
    }
    private fun isLastActive(): Boolean {
        return !binding.minusSwitch.isChecked and
                !binding.capsSwitch.isChecked and
                !binding.numbersSwitch.isChecked and
                !binding.specialCharsSwitch.isChecked
    }
}