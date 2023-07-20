package com.example.baseproject.ui.signup

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ValidationUtils.init(requireContext())
    }

    override fun bindingStateView() {
        super.bindingStateView()
        observer()
    }

    override fun setOnClick() {
        super.setOnClick()

        watchToEnableButton()

        binding.tvGoToLogIn.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }

        binding.ivBack.setOnClickListener {
            view?.findNavController()?.navigateUp()
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
        binding.cbPolicy.setOnCheckedChangeListener { compoundButton, isChecked ->  checkButtonVisibility()}
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
        var count = 1
        viewModel.signup.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UIState.Loading -> {
                    count = 1
                    ProgressBarView.showProgressBar(activity)
                }
                is UIState.Failure -> {
                    if (count == 1) {
                        alert.showErrorDialog(activity, title = resources.getString(R.string.sign_up_error), body = state.error!!)
                        count++
                        ProgressBarView.hideProgressBar()
                    }
                }
                is UIState.Success -> {
                    appNavigation.openSignUpToHomeScreen()
                    val sharePref = context?.getSharedPreferences(Constants.ISLOGIN, Context.MODE_PRIVATE)
                    val editor = sharePref?.edit()
                    editor?.putBoolean(Constants.ISLOGIN, true)
                    editor?.apply()
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
            password = binding.edtPassword.text.toString(),
            avatar = ""
        )
    }

    private fun onClickSignUpButton(name: String, email: String, password: String) {
        binding.btnSignup.setOnClickListener {
            val errorName: String? = ValidationUtils.validateName(name)
            val errorEmail: String? = ValidationUtils.validateEmail(email)
            val errorPassword: String? = ValidationUtils.validatePassword(password)

            if (errorName != null) {
                alert.showErrorDialog(activity, resources.getString(R.string.name_required), errorName)
            } else if (errorEmail != null) {
                alert.showErrorDialog(activity, resources.getString(R.string.email_error_title), errorEmail)
            } else if (errorPassword != null) {
                alert.showErrorDialog(activity, resources.getString(R.string.password_error_title), errorPassword)
            } else {
                viewModel.signupUser(
                    email = binding.edtEmail.text.toString(),
                    password = binding.edtPassword.text.toString(),
                    user = getUser()
                )
            }
        }
    }
}

