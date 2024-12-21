package com.dicoding.aplikasistoryapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.dicoding.aplikasistoryapp.data.Result
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.aplikasistoryapp.R
import com.dicoding.aplikasistoryapp.data.UserRepository
import com.dicoding.aplikasistoryapp.data.pref.UserPreference
import com.dicoding.aplikasistoryapp.data.pref.dataStore
import com.dicoding.aplikasistoryapp.data.remote.retrofit.ApiConfig
import com.dicoding.aplikasistoryapp.databinding.ActivityRegisterBinding
import com.dicoding.aplikasistoryapp.ui.customview.MyEditText
import com.dicoding.aplikasistoryapp.utils.ViewModelFactory
import com.dicoding.aplikasistoryapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var nameEditText: MyEditText
    private lateinit var emailEditText: MyEditText
    private lateinit var passwordEditText: MyEditText

    private val viewModel by viewModels<RegisterViewModel> {
        val userPreference = UserPreference.getInstance(this.dataStore)
        ViewModelFactory(
            UserRepository.getInstance(
                this,
                userPreference,
                ApiConfig.getApiService()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameEditText = binding.nameEditText
        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText

        val emailErrorText = binding.emailErrorText
        val passwordErrorText = binding.passwordErrorText
        val nameErrorText = binding.nameErrorText

        nameEditText.attachErrorTextView(nameErrorText)
        emailEditText.attachErrorTextView(emailErrorText)
        passwordEditText.attachErrorTextView(passwordErrorText)

        nameEditText.setValidationType(MyEditText.ValidationType.NAME)
        emailEditText.setValidationType(MyEditText.ValidationType.EMAIL)
        passwordEditText.setValidationType(MyEditText.ValidationType.PASSWORD)

        setupView()
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

    private fun setupAction() {

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()


            viewModel.register(name, email, password)

            lifecycleScope.launchWhenStarted {
                viewModel.registerResult.collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            showSuccessDialog(email)
                        }

                        is Result.Error -> {
                            showLoading(false)
                            showError(result)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.registration_success_title))
            setMessage(getString(R.string.registration_success_message, email))
            setPositiveButton(getString(R.string.continue_button))  { _, _ ->
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }


    private fun showError(error: Result.Error) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.error_title))
            setMessage(error.error)
            setPositiveButton(getString(R.string.ok)) { _, _ -> }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -80f, 80f).apply {
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
        val signup = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(100)


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