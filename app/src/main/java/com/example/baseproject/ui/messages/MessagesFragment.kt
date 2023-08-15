package com.example.baseproject.ui.messages

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentMessagesBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.GridSpacingItemDecoration
import com.example.baseproject.utils.ListUtils
import com.example.core.base.fragment.BaseFragment
import com.example.core.utils.getPxFromDp
import com.example.core.utils.tint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessagesFragment :
    BaseFragment<FragmentMessagesBinding, MessagesViewModel>(R.layout.fragment_messages),
    GalleryAdapter.OnSelectedListener {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: MessagesViewModel by viewModels()

    private lateinit var sendId: String
    private lateinit var toId: String
    private var selectedImages =  mutableListOf<Int>()

    private var messagesAdapter: MessagesAdapter? = null
    private var galleryAdapter: GalleryAdapter? = null
    private val imagePaths = mutableListOf<String>()


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (imagePaths.isEmpty()) {
                getAllImage()
            } else {
                galleryAdapter?.submitList(imagePaths)
                galleryAdapter?.setOnSelectedListener(this)

                binding.btnImagePicker.tint(R.color.primary_color)
                binding.imagePickerContainer.visibility = View.VISIBLE
            }
        } else {
            Log.e("abc", "Not granted")
        }
    }

    override fun getVM() = viewModel

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val bundle = arguments?.getString(Constants.USER_ID).toString()

        toId = bundle

        viewModel.getUserById(toId)

        viewModel.getAllMessage()

        messagesAdapter = MessagesAdapter()
        binding.recyclerViewMessages.adapter = messagesAdapter

        galleryAdapter = GalleryAdapter()
        binding.recyclerViewImagePicker.adapter = galleryAdapter
        binding.recyclerViewImagePicker.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewImagePicker.addItemDecoration(
            GridSpacingItemDecoration(
                3,
                requireContext().getPxFromDp(2.0F),
                false
            )
        )

        binding.btnSend.visibility = View.GONE
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.messageListLiveData.observe(viewLifecycleOwner) {
            messagesAdapter?.submitList(ListUtils.getMessageListSortByTime(it.toMutableList()))
        }

        viewModel.friendProfile.observe(viewLifecycleOwner) { user ->
            binding.tvName.text = user.name
            if (user.avatar != null && user.avatar.isNotEmpty()) {
                Glide.with(this).load(user.avatar)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_avatar_default)
                    .into(binding.ivAvatar)
            }
            messagesAdapter?.getFriendProfile(user, user)
        }
    }

    @SuppressLint("IntentReset")
    override fun setOnClick() {
        super.setOnClick()

        // watch to send text
        watchToEnableButton()

        binding.btnBack.setOnClickListener {
            appNavigation.navigateUp()
        }

        binding.btnImagePicker.setOnClickListener {
            if (binding.imagePickerContainer.visibility == View.VISIBLE) {
                binding.imagePickerContainer.visibility = View.GONE
                binding.btnImagePicker.tint(R.color.grey_999999)
                galleryAdapter?.clearSelectedItem()
                if (selectedImages.isNotEmpty()) selectedImages.clear()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                Log.e("abc", "setOnClick: ${imagePaths.size}")
            }
        }

        binding.btnSend.setOnClickListener {
            viewModel.sendMessage(toId, binding.edtMessage.text.toString(), Constants.TYPE_TEXT)
            binding.edtMessage.text.clear()
            binding.recyclerViewMessages.layoutManager?.smoothScrollToPosition(
                binding.recyclerViewMessages,
                null,
                0
            )
        }

        binding.btnSendImage.setOnClickListener {
            for (i in selectedImages) {
                viewModel.sendMessage(toId, imagePaths[i].toUri().toString(), Constants.TYPE_IMAGE)
            }
            selectedImages.clear()
            galleryAdapter?.clearSelectedItem()
            binding.imagePickerContainer.visibility = View.GONE
        }
    }

    private fun getAllImage() {

        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

        if (isSDPresent) {
            val columns = arrayOf(MediaStore.Images.Media._ID)

            val orderBy = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

            val cursor = requireContext().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                orderBy
            )
            val count = cursor?.count ?: 0
            for (i in 0 until count) {
                cursor?.moveToPosition(i)
                val idColumnIndex = cursor?.getColumnIndex(MediaStore.Images.Media._ID)

                idColumnIndex?.let {
                    val imageId = cursor.getLong(it)
                    val imageUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        imageId.toString()
                    )
                    imagePaths.add(imageUri.toString())
                }
            }
            cursor?.close()
        }
    }

    private fun watchToEnableButton() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkSendMessageButtonVisibility(p0)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }

        binding.edtMessage.addTextChangedListener(textWatcher)
    }

    fun checkSendMessageButtonVisibility(text: CharSequence?) {
        if (text != null && text.isNotEmpty()) {
            binding.btnSend.visibility = View.VISIBLE
        } else {
            binding.btnSend.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSelectedItemChange(selected: MutableList<Int>) {
        val numberOfImage = selected.size
        selectedImages = selected
        if (numberOfImage > 0) {
            binding.btnSendImage.text = "${resources.getString(R.string.send)} ($numberOfImage)"
            binding.btnSendImage.visibility = View.VISIBLE
        } else {
            binding.btnSendImage.visibility = View.GONE
        }
    }
}