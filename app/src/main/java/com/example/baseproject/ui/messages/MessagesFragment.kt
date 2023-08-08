package com.example.baseproject.ui.messages

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentMessagesBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.utils.Constants
import com.example.core.base.fragment.BaseFragment
import com.example.core.utils.tint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessagesFragment : BaseFragment<FragmentMessagesBinding, MessagesViewModel> (R.layout.fragment_messages) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: MessagesViewModel by viewModels()

    private lateinit var sendId: String
    private lateinit var toId: String

    private var messagesAdapter: MessagesAdapter? = null

    override fun getVM() = viewModel

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val bundle = arguments?.getString(Constants.USER_ID).toString()

        toId = bundle

        viewModel.getUserById(toId)

        viewModel.getAllMessage()

        messagesAdapter = MessagesAdapter()
        binding.recyclerViewMessages.adapter = messagesAdapter

    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.messageListLiveData.observe(viewLifecycleOwner){
            Log.e("abc", "Message: $it", )
            messagesAdapter?.submitList(it.toMutableList())
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        // watch to send text
        watchToEnableButton()

        binding.btnBack.setOnClickListener {
            appNavigation.navigateUp()
        }

        binding.btnImagePicker.setOnClickListener {
            binding.btnImagePicker.tint(R.color.primary_color)
        }

        viewModel.friendProfile.observe(viewLifecycleOwner){ user ->
            binding.tvName.text = user.name
            if (user.avatar != null && user.avatar.isNotEmpty()){
                Glide.with(this).load(user.avatar)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_avatar_default)
                    .into(binding.ivAvatar)
            }

        }

        viewModel.chatId.observe(viewLifecycleOwner){
            Log.e("abc", "Chat ID: $it", )
        }

        binding.btnSend.setOnClickListener {
            viewModel.sendMessage(toId, binding.edtMessage.text.toString(), "text")
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

        binding.edtMessage.addTextChangedListener(textWatcher)
    }

    fun checkButtonVisibility() {
        val message = binding.edtMessage.text.toString()

        if (message.isNotEmpty() && message.isNotEmpty()) {
            binding.btnSend.visibility = View.VISIBLE
        } else {
            binding.btnSend.visibility = View.GONE
        }
    }
}