package com.example.setting.ui.home

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.core.adapter.OnItemClickListener
import com.example.core.base.dialog.CONFIRM_DIALOG_FRAGMENT
import com.example.core.base.dialog.ConfirmDialogListener
import com.example.core.base.dialog.NOTICE_DIALOG_FRAGMENT
import com.example.core.base.dialog.NoticeDialog
import com.example.core.base.dialog.NoticeDialogListener
import com.example.core.base.fragment.BaseFragment
import com.example.core.utils.prefetcher.bindToLifecycle
import com.example.core.utils.prefetcher.setupWithPrefetchViewPool
import com.example.core.utils.setOnSafeClickListener
import com.example.core.utils.toast
import com.example.setting.DemoNavigation
import com.example.setting.R
import com.example.setting.adapter.HomePageAdapter
import com.example.setting.adapter.HomeSlideViewHolder
import com.example.setting.databinding.FragmentHomePageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomePageFragment :
    BaseFragment<FragmentHomePageBinding, HomePageViewModel>(R.layout.fragment_home_page),
    ConfirmDialogListener, NoticeDialogListener {

    @Inject
    lateinit var appNavigation: DemoNavigation

    private var adapterHomePage: HomePageAdapter? = null

    private val viewModel: HomePageViewModel by viewModels()

    override fun getVM() = viewModel

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        initAdapter()
    }

    private fun initAdapter() {

        binding.layoutHome.apply {

            adapterHomePage = HomePageAdapter(object : OnItemClickListener {
                override fun onItemClick(position: Int) {
                    //do nothing
                }
            })

            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapterHomePage?.getItemViewType(position)) {
                        R.layout.item_home_slide_layout -> 2
                        R.layout.item_song_layout -> 2
                        R.layout.item_title_home_layout -> 2
                        R.layout.item_album_layout -> 1
                        else -> 2
                    }
                }
            }

            layoutManager = gridLayoutManager
            setHasFixedSize(true)
            adapter = adapterHomePage

            setupWithPrefetchViewPool {
                setPrefetchBound(viewType = R.layout.item_album_layout, count = 6)
                setPrefetchBound(viewType = R.layout.item_song_layout, count = 16)
                setPrefetchBound(viewType = R.layout.item_home_slide_layout, count = 1)
                setPrefetchBound(viewType = R.layout.item_title_home_layout, count = 2)
            }.bindToLifecycle(viewLifecycleOwner)

        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listHomePage.collect {
                    adapterHomePage?.submitList(it)
                }
            }
        }

    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btn.setOnSafeClickListener {
            appNavigation.openDemoViewPager()
        }

        binding.btnDialog.setOnSafeClickListener {
//            if (childFragmentManager.findFragmentByTag(CONFIRM_DIALOG_FRAGMENT) == null) {
//                val demoDialog = ConfirmDialogFragment.getInstance("Title","content")
//                demoDialog.setDialogListener(this)
//                demoDialog.show(childFragmentManager, CONFIRM_DIALOG_FRAGMENT)
//            }

            if (childFragmentManager.findFragmentByTag(NOTICE_DIALOG_FRAGMENT) == null) {
                val demoDialog = NoticeDialog.getInstance("Title")
                demoDialog.setDialogListener(this)
                demoDialog.show(childFragmentManager, CONFIRM_DIALOG_FRAGMENT)
            }
        }
    }

    override fun onDestroyView() {
        val holder = binding.layoutHome.findViewHolderForAdapterPosition(0)
        if (holder is HomeSlideViewHolder) {
            holder.onViewRecycled()
        }
        adapterHomePage = null
        super.onDestroyView()
    }

    override fun onClickOk(type: Int?) {
        getString(R.string.ok).toast(requireContext())
    }

    override fun onClickCancel(type: Int?) {
        getString(R.string.cancel).toast(requireContext())
    }

}