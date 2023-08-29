package com.example.baseproject.ui.profileDetail

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentProfileDetailBinding
import com.example.baseproject.models.User
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.chats.ActionState
import com.example.baseproject.utils.*
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDetailFragment :
    BaseFragment<FragmentProfileDetailBinding, ProfileDetailViewModel>(R.layout.fragment_profile_detail) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: ProfileDetailViewModel by viewModels()

    override fun getVM() = viewModel

    private lateinit var userProfile: User
    private var selectedImageUri: Uri? = null

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                // for camera
                val selectedImageBitmap = result.data?.extras?.get("data") as? Bitmap
                // for gallery
                selectedImageUri = result.data?.data

                if (selectedImageBitmap != null) {
                    selectedImageUri =
                        ImageUtils.saveBitmapToGallery(requireContext(), selectedImageBitmap)
                }

                if (selectedImageUri != null) {
                    Glide.with(this).load(selectedImageUri)
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.ic_avatar_default)
                        .into(binding.ivAvatar)
                }
            }
        }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        viewModel.getCurrentUser()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.actionProfileDetail.observe(viewLifecycleOwner) {
            when (it) {
                is ActionState.Loading -> binding.progressBar.visibility = View.VISIBLE
                else -> binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.actionAvatar.observe(viewLifecycleOwner) {
            when (it) {
                is ActionState.Loading -> binding.progressBar.visibility = View.VISIBLE
                else -> binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            userProfile = user
            binding.edtFullname.setText(user.name)
            binding.edtPhoneNumber.setText(user.phoneNumber)
            binding.edtDateOfBirth.setText(user.dateOfBirth)
            if (user.avatar != null && user.avatar.isNotEmpty()) {
                Glide.with(this).load(user.avatar)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBarAvatar.visibility = View.GONE
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBarAvatar.visibility = View.GONE
                            return false
                        }
                    }).into(binding.ivAvatar)
            } else {
                binding.progressBarAvatar.visibility = View.GONE
            }
        }
    }


    override fun setOnClick() {
        super.setOnClick()
        binding.btnBack.setOnClickListener {
            appNavigation.navigateUp()
        }

        onClickSaveButton()

        onClickChooseDate()

        binding.ivEditAvatar.setOnClickListener {
            showDialogImagePicker()
        }
    }

    @SuppressLint("IntentReset")
    private fun showDialogImagePicker() {
        if (activity != null) {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.bottom_sheet_image_picker)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(true)

            dialog.show()

            val btnImage = dialog.findViewById<ImageView>(R.id.iv_image)
            btnImage.setOnClickListener {
                dialog.dismiss()
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryIntent.type = "image/*"
                startForResult.launch(galleryIntent)
            }

            val btnCamera = dialog.findViewById<ImageView>(R.id.iv_camera)

            btnCamera.setOnClickListener {
                dialog.dismiss()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startForResult.launch(cameraIntent)
            }
        }
    }


    private fun onClickSaveButton() {
        binding.btnSave.setOnClickListener {
            val name: String = binding.edtFullname.text.toString().trim()
            val phoneNumber: String = binding.edtPhoneNumber.text.toString().trim()

            val errorName = ValidationUtils.validateName(name)
            val errorPhoneNumber = ValidationUtils.validatePhoneNumber(phoneNumber)

            if (errorName == Constants.NAME_REQUIRED) {
                DialogView().showErrorDialog(
                    activity,
                    resources.getString(R.string.error),
                    resources.getString(R.string.error_name_required)
                )
            } else if (errorPhoneNumber == Constants.PHONE_NUMBER_INVALID) {
                DialogView().showErrorDialog(
                    activity,
                    resources.getString(R.string.error),
                    resources.getString(R.string.error_phone_number)
                )
            } else {
                val userUpdate = User(
                    id = userProfile.id,
                    name = name,
                    email = userProfile.email,
                    phoneNumber = phoneNumber,
                    dateOfBirth = binding.edtDateOfBirth.text.toString(),
                    avatar = null,
                    token = userProfile.token
                )

                if (selectedImageUri != null) {
                    viewModel.uploadImageToStorage(userUpdate, selectedImageUri!!)
                    viewModel.actionAvatar.observe(viewLifecycleOwner) {
                        if (it == ActionState.Finish) {
                            appNavigation.navigateUp()
                        }
                    }

                } else {
                    viewModel.actionUpdate.observe(viewLifecycleOwner) {
                        if (it == ActionState.Finish) {
                            appNavigation.navigateUp()
                        }
                    }
                    viewModel.updateUserInfor(userUpdate)

                }
            }
        }
    }

    private fun onClickChooseDate() {
        binding.edtDateOfBirth.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val format = resources.getString(R.string.format_date_ddMMyyyy)
                val sdf = SimpleDateFormat(format, Locale.CHINESE)

                binding.edtDateOfBirth.setText(sdf.format(calendar.time))
            }
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        selectedImageUri = null
    }
}