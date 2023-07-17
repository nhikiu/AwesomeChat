package com.example.setting.ui.viewPager

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.core.base.fragment.BaseFragment
import com.example.setting.R
import com.example.setting.databinding.FragmentDemoBinding

class DemoFragment : BaseFragment<FragmentDemoBinding, DemoViewModel>(R.layout.fragment_demo) {

    private val mShareViewModel: DemoViewPagerViewModel by activityViewModels()
    private val mViewModel: DemoViewModel by viewModels()

    override fun getVM() = mViewModel

    var number: Int = 0
    var numberRandom: Int = 0
    var idA = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            number = savedInstanceState.getInt("Ahihi")
            numberRandom = savedInstanceState.getInt("AhihiRandom")
        }

        if (parentFragment is DemoViewPager) {
            setFragmentListener(parentFragment as DemoViewPager)
        }
    }

    private val observerAbc = Observer<Boolean> {
        if (it) {
            mViewModel.fetchData(idA)
        }
    }

    override fun onResume() {
        super.onResume()
        mShareViewModel.isCanRequestPoint.observe(viewLifecycleOwner, observerAbc)
        loadFragmentListener?.onResumeItem(this)
    }

    fun setFragmentListener(loadFragmentListener: LoadFragmentListener) {
        this.loadFragmentListener = loadFragmentListener
    }

    var loadFragmentListener: LoadFragmentListener? = null

    interface LoadFragmentListener {
        fun onResumeItem(demoFragment: DemoFragment)
    }

    override fun onPause() {
        super.onPause()
        mShareViewModel.isCanRequestPoint.removeObserver(observerAbc)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("Ahihi", number)
        outState.putInt("AhihiRandom", numberRandom)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        idA = (arguments?.getInt(FRAGMENT_ID) ?: 0).toString()
        binding.tv.text = idA
        binding.number.text = number.toString()
        binding.tvAhihi.text = "ahihi = $numberRandom"

    }

    fun updateText() {
        numberRandom = (0..100).random()
        binding.tvAhihi.text = "ahihi = $numberRandom"
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnCong.setOnClickListener {
            number++
            binding.number.text = number.toString()
        }

        binding.btnTru.setOnClickListener {
            number--
            binding.number.text = number.toString()
        }
    }

    companion object {
        private const val FRAGMENT_ID = "FRAGMENT_ID"
        fun getInstance(id: Int): DemoFragment {
            val fragment = DemoFragment()
            val args = Bundle()
            args.putInt(FRAGMENT_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}