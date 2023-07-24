package com.example.baseproject.ui.login

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.navigation.AppNavigation
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.baseproject.databinding.FragmentLoginBinding
import com.example.baseproject.utils.*

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: LoginViewModel by viewModels()

    override fun getVM() = viewModel

    private val alert = DialogView()

    override fun bindingStateView() {
        super.bindingStateView()
        observer()
    }

    override fun setOnClick() {
        super.setOnClick()

        watchToEnableButton()
        binding.tvGoToSignUp.setOnClickListener {
            appNavigation.openLogInToSignUpScreen()
        }

    }

    private fun watchToEnableButton() {
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

    }

    fun checkButtonVisibility() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
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

            if (errorEmail == Constants.EMAIL_REQUIRED) {
                alert.showErrorDialog(activity, resources.getString(R.string.error), resources.getString(R.string.error_email_required))
            } else if (errorEmail == Constants.EMAIL_INVALID) {
                alert.showErrorDialog(activity, resources.getString(R.string.error), resources.getString(R.string.error_email_invalid))
            } else if (errorPassword == Constants.PASSWORD_REQUIRED) {
                alert.showErrorDialog(activity, resources.getString(R.string.error), resources.getString(R.string.error_password_required))
            } else if (errorPassword == Constants.PASSWORD_INVALID) {
                alert.showErrorDialog(activity, resources.getString(R.string.error), resources.getString(R.string.error_password_invalid))
            } else {
                viewModel.loginUser(
                    email = email,
                    password = password
                )
            }
        }
    }

    private fun observer() {
        viewModel.login.observe(viewLifecycleOwner){state ->
            when(state) {
                is UIState.Loading -> {
                    ProgressBarView.showProgressBar(activity)
                }
                is UIState.Failure -> {
                    context?.let {
                        val errorMessage = state.error
                        val error = it.getString(R.string.error)
                        when (errorMessage) {
                            Constants.USER_NOT_FOUND -> {
                                alert.showErrorDialog(activity, error, it.getString(R.string.error_user_not_found))
                            }
                            Constants.EMAIL_PASSWORD_INVALID -> {
                                alert.showErrorDialog(activity, error, it.getString(R.string.error_email_password_invalid))
                            }
                            Constants.NETWORK_NOT_CONNECTION -> {
                                alert.showErrorDialog(activity, error, it.getString(R.string.error_network_not_connection))
                            }
                            else -> {
                                alert.showErrorDialog(activity, error, it.getString(R.string.error_unknown))
                            }
                        }
                    }
                    ProgressBarView.hideProgressBar()
                }
                is UIState.Success -> {
                    appNavigation.openLogInToHomeScreen()
                    context?.let {
                        val sharePref = it.getSharedPreferences(Constants.ISLOGIN, Context.MODE_PRIVATE)
                        val editor = sharePref.edit()
                        editor.putBoolean(Constants.ISLOGIN, true)
                        editor.apply()
                    }
                    ProgressBarView.hideProgressBar()
                }
            }
        }
    }
}