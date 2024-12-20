package com.dicoding.picodiploma.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.storyapp.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val factory: ViewModelFactory by lazy {
        ViewModelFactory.getInstance(this)
    }
    private val viewModel: SignUpViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupObservers()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupObservers() {
        viewModel.registerStatus.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.signupButton.isEnabled = true
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.success))
                        .setMessage(getString(R.string.registration_successful))
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            finish()
                        }
                        .setOnDismissListener {
                            finish()
                        }
                        .create()
                        .show()
                }

                is Result.Error -> {
                    binding.signupButton.isEnabled = true
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.failed))
                        .setMessage(result.error)
                        .setPositiveButton(getString(R.string.retry)) { _, _ ->
                        }
                        .create()
                        .show()
                }

                Result.Loading -> TODO()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupAction() {

        val emailField = binding.edRegisterEmail
        val passwordField = binding.edRegisterPassword

        val updateButtonState = {
            binding.signupButton.isEnabled = emailField.isValid() && passwordField.isValid()
        }

        binding.edRegisterEmail.addTextChangedListener {
            updateButtonState()
        }

        binding.edRegisterPassword.addTextChangedListener {
            updateButtonState()
        }

        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.getInput()
            val password = binding.edRegisterPassword.getInput()
            binding.signupButton.isEnabled = false
            viewModel.registerUser(name, email, password)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}