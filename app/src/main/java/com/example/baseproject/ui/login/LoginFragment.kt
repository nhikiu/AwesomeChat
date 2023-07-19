package com.example.baseproject.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.navigation.AppNavigation
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.baseproject.databinding.FragmentLoginBinding
import com.example.baseproject.utils.DialogView
import com.example.baseproject.utils.ValidationUtils

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: LoginViewModel by viewModels()

    override fun getVM() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ValidationUtils.init(requireContext())
    }

    override fun setOnClick() {
        super.setOnClick()
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkButtonVisibility()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }


        binding.edtEmail.addTextChangedListener(textWatcher)
        binding.edtPassword.addTextChangedListener(textWatcher)

        binding.tvGoToSignUp.setOnClickListener {
            appNavigation.openLogInToSignUpScreen()
        }

    }

    fun checkButtonVisibility() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if (email.isNotEmpty() && email != "" && password.isNotEmpty() && password != "") {
            binding.btnLogin.isClickable = true
            binding.btnLogin.setBackgroundResource(R.drawable.bg_button_enable)
            onClickLogInButton(email, password)
        } else {
            binding.btnLogin.isClickable = false
            binding.btnLogin.setBackgroundResource(R.drawable.bg_button_disable)
        }
    }

    private fun onClickLogInButton(email: String, password: String) {
        binding.btnLogin.setOnClickListener {
            val errorEmail: String? = ValidationUtils.validateEmail(email)
            val errorPassword: String? = ValidationUtils.validatePassword(password)

            val alert = DialogView()

            if (errorEmail != null) {
                alert.showErrorDialog(activity, resources.getString(R.string.email_error_title), errorEmail)
            } else if (errorPassword != null) {
                alert.showErrorDialog(activity, resources.getString(R.string.password_error_title), errorPassword)
            } else {
                appNavigation.openLogInToHomeScreen()
            }
        }
    }
}