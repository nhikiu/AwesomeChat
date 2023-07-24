package com.example.baseproject.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentHomeBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.chats.ChatsFragment
import com.example.baseproject.ui.friends.FriendsFragment
import com.example.baseproject.ui.profile.ProfileFragment
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: HomeViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        replaceFragment(ChatsFragment())
    }

    override fun getVM() = viewModel


    override fun setOnClick() {
        super.setOnClick()

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.chats -> replaceFragment(ChatsFragment())
                R.id.friends -> replaceFragment(FriendsFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> {

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val activity = activity
        if (activity != null) {
            val fragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.commit()
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag("HomeFragment").d("A   " + "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("HomeFragment").d("A   " + "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.tag("HomeFragment").d("A   " + "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag("HomeFragment").d("A   " + "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Timber.tag("HomeFragment").d("A   " + "onStart")
        super.onStart()
    }

    override fun onResume() {
        Timber.tag("VietBH").d("A   " + "onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.tag("HomeFragment").d("A   " + "onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.tag("HomeFragment").d("A   " + "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.tag("HomeFragment").d("A   " + "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.tag("HomeFragment").d("A   " + "onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Timber.tag("HomeFragment").d("A   " + "onCreate")
        super.onDetach()
    }


}