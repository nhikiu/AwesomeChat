package com.example.baseproject.ui.signup

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentSignupBinding
import com.example.baseproject.models.User
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.utils.*
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding, SignupViewModel>(R.layout.fragment_signup) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: SignupViewModel by viewModels()

    override fun getVM() = viewModel

    private val alert = DialogView()

    override fun bindingStateView() {
        super.bindingStateView()
        observer()
    }

    override fun setOnClick() {
        super.setOnClick()

        watchToEnableButton()

        binding.tvGoToLogIn.setOnClickListener {
            appNavigation.navigateUp()
        }

        binding.linearPolicy.setOnClickListener {
            val isChecked = binding.cbPolicy.isChecked
            binding.cbPolicy.isChecked = !isChecked
        }

        binding.ivBack.setOnClickListener {
            appNavigation.navigateUp()
        }
    }

    private fun watchToEnableButton() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkButtonVisibility()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        binding.edtEmail.addTextChangedListener(textWatcher)
        binding.edtPassword.addTextChangedListener(textWatcher)
        binding.edtFullname.addTextChangedListener(textWatcher)
        binding.cbPolicy.setOnCheckedChangeListener { _, _ ->  checkButtonVisibility()}
    }

    private fun checkButtonVisibility() {
        val name = binding.edtFullname.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val isChecked = binding.cbPolicy.isChecked

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && isChecked) {
            binding.btnSignup.isClickable = true
            binding.btnSignup.setBackgroundResource(R.drawable.bg_button_enable)
            onClickSignUpButton(name, email, password)

        } else {
            binding.btnSignup.isClickable = false
            binding.btnSignup.setBackgroundResource(R.drawable.bg_button_disable)
        }
    }

    private fun observer() {
        viewModel.signup.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UIState.Loading -> {
                    ProgressBarView.showProgressBar(activity)
                }
                is UIState.Failure -> {
                    context?.let {
                        val errorMessage = state.error
                        val error = it.getString(R.string.error)
                        when (errorMessage) {
                            Constants.NETWORK_NOT_CONNECTION -> {
                                alert.showErrorDialog(activity, error, it.getString(R.string.error_network_not_connection))
                            }
                            Constants.USER_EXIST -> {
                                alert.showErrorDialog(activity, error, it.getString(R.string.error_user_is_exist))
                            }
                            else -> {
                                alert.showErrorDialog(activity, error, it.getString(R.string.error_unknown))
                            }
                        }
                    }
                    ProgressBarView.hideProgressBar()
                }
                is UIState.Success -> {
                    appNavigation.openSignUpToHomeScreen()
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

    private fun getUser() : User {
        return User(
            id = "",
            name = binding.edtFullname.text.toString(),
            email = binding.edtEmail.text.toString(),
            avatar = "",
            phoneNumber = "",
            dateOfBirth = "",
        )
    }

    private fun onClickSignUpButton(name: String, email: String, password: String) {
        binding.btnSignup.setOnClickListener {
            val errorName: String? = ValidationUtils.validateName(name)
            val errorEmail: String? = ValidationUtils.validateEmail(email)
            val errorPassword: String? = ValidationUtils.validatePassword(password)

            if (errorName == Constants.NAME_REQUIRED) {
                alert.showErrorDialog(
                    activity,
                    resources.getString(R.string.error),
                    resources.getString(R.string.error_name_required))
            } else if (errorEmail == Constants.EMAIL_REQUIRED) {
                alert.showErrorDialog(
                    activity,
                    resources.getString(R.string.error),
                    resources.getString(R.string.error_email_invalid))
            } else if (errorEmail == Constants.EMAIL_INVALID) {
                alert.showErrorDialog(
                    activity,
                    resources.getString(R.string.error),
                    resources.getString(R.string.error_email_invalid))
            }
            else if (errorPassword == Constants.PASSWORD_REQUIRED) {
                alert.showErrorDialog(
                    activity,
                    resources.getString(R.string.error),
                    resources.getString(R.string.error_password_required))
            } else if (errorPassword == Constants.PASSWORD_INVALID) {
                alert.showErrorDialog(
                    activity,
                    resources.getString(R.string.error),
                    resources.getString(R.string.error_password_invalid))
            }
            else {
                viewModel.signupUser(
                    email = binding.edtEmail.text.toString(),
                    password = binding.edtPassword.text.toString(),
                    user = getUser()
                )
            }
        }
    }
}

