package com.example.setting.ui.viewPager

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.example.core.base.fragment.BaseFragmentNotRequireViewModel
import com.example.core.utils.getCurrentFragment
import com.example.core.utils.getFragmentAt
import com.example.core.utils.setOnSafeClickListener
import com.example.setting.R
import com.example.setting.databinding.FragmentDemoViewpagerBinding

class DemoViewPager :
    BaseFragmentNotRequireViewModel<FragmentDemoViewpagerBinding>(R.layout.fragment_demo_viewpager),
    DemoFragment.LoadFragmentListener {

    private val mViewModel: DemoViewPagerViewModel by activityViewModels()

    //ViewPager dynamic
    private var viewPager1Adapter: MyPagerAdapter? = null
    private val listFragment1 = arrayListOf<DemoFragment>()

    private var viewPager2Adapter: MyStatePagerAdapter? = null
    private val listFragment2 = arrayListOf<DemoFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        initDynamicViewPager2()
//        initDynamicViewPager1()
    }

    private fun initDynamicViewPager2() {
        listFragment2.add(
            (binding.viewPager2.getFragmentAt(
                childFragmentManager,
                0
            ) ?: DemoFragment.getInstance(1)) as DemoFragment
        )
        listFragment2.add(
            (binding.viewPager2.getFragmentAt(
                childFragmentManager,
                1
            ) ?: DemoFragment.getInstance(2)) as DemoFragment
        )
        listFragment2.add(
            (binding.viewPager2.getFragmentAt(
                childFragmentManager,
                2
            ) ?: DemoFragment.getInstance(3)) as DemoFragment
        )
        listFragment2.add(
            (binding.viewPager2.getFragmentAt(
                childFragmentManager,
                3
            ) ?: DemoFragment.getInstance(4)) as DemoFragment
        )
        viewPager2Adapter = MyStatePagerAdapter(
            listFragment2,
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )
        binding.viewPager2.adapter = viewPager2Adapter
        binding.viewPager2.post {
            binding.viewPager2.offscreenPageLimit = 1
            binding.viewPager2.currentItem = 3
        }

//        listFragment2.forEach {
//            it.setFragmentListener(object : DemoFragment.LoadFragmentListener{
//                override fun onResumeItem() {
//                    if (it === binding.viewPager2.getFragmentAt(childFragmentManager,3)){
//                        mViewModel.canLoadData()
//                    }
//                }
//            })
//        }
    }

    override fun onResumeItem(demoFragment: DemoFragment) {
        if (demoFragment === binding.viewPager2.getFragmentAt(childFragmentManager, 3)) {
            mViewModel.canLoadData()
        }
    }

    private fun initDynamicViewPager1() {
        listFragment1.add(
            (binding.viewPager1.getFragmentAt(
                childFragmentManager,
                0
            ) ?: DemoFragment.getInstance(1)) as DemoFragment
        )
        listFragment1.add(
            (binding.viewPager1.getFragmentAt(
                childFragmentManager,
                1
            ) ?: DemoFragment.getInstance(2)) as DemoFragment
        )
        listFragment1.add(
            (binding.viewPager1.getFragmentAt(
                childFragmentManager,
                2
            ) ?: DemoFragment.getInstance(3)) as DemoFragment
        )
        listFragment1.add(
            (binding.viewPager1.getFragmentAt(
                childFragmentManager,
                3
            ) ?: DemoFragment.getInstance(4)) as DemoFragment
        )

        viewPager1Adapter = MyPagerAdapter(listFragment1, childFragmentManager)
        binding.viewPager1.adapter = viewPager1Adapter
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.btn.setOnSafeClickListener {
            val fragment = binding.viewPager1.getCurrentFragment(childFragmentManager)
            if (fragment is DemoFragment) {
                fragment.updateText()
            }

            val fragment2 = binding.viewPager2.getCurrentFragment(childFragmentManager)
            if (fragment2 is DemoFragment) {
                fragment2.updateText()
            }
        }
    }

    override fun onDestroyView() {
        listFragment1.clear()
        binding.viewPager1.adapter = null

        listFragment2.clear()
        binding.viewPager2.adapter = null
        super.onDestroyView()
    }
}