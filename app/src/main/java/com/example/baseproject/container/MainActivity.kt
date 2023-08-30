package com.example.baseproject.container

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.baseproject.R
import com.example.baseproject.databinding.ActivityMainBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.home.HomeFragment
import com.example.baseproject.ui.messages.MessagesFragment
import com.example.baseproject.utils.Constants
import com.example.core.base.activity.BaseActivityNotRequireViewModel
import com.example.core.base.dialog.ConfirmDialogListener
import com.example.core.pref.RxPreferences
import com.example.core.utils.NetworkConnectionManager
import com.example.core.utils.setLanguage
import com.example.core.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivityNotRequireViewModel<ActivityMainBinding>(), ConfirmDialogListener {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
        appNavigation.bind(navHostFragment.navController)

        val extras = intent?.extras
        if (extras != null) {
            val id = extras.getString(Constants.FROM_ID_USER)
            val type = extras.getString(Constants.MESSAGE_TYPE)
            if (id != null && type == Constants.NOTIFICATION_TYPE_NEW_MESSAGE) openMessageScreen(id)
            if (id != null && type == Constants.NOTIFICATION_TYPE_STATE_FRIEND) openFriendScreen()
        }

        lifecycleScope.launch {
            val language = rxPreferences.getLanguage().first()
            language?.let { setLanguage(it) }
        }

        networkConnectionManager.isNetworkConnectedFlow
            .onEach {
                if (it) {
                    Timber.tag("MainActivity").d("onCreate: Network connected")
                } else {
                    Timber.tag("MainActivity").d("onCreate: Network disconnected")
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val id = intent?.getBundleExtra(Constants.NOTIFICATION_DATA)?.getString(Constants.FROM_ID_USER)
        val type = intent?.getBundleExtra(Constants.NOTIFICATION_DATA)?.getString(Constants.MESSAGE_TYPE)

        if (id != null && type == Constants.NOTIFICATION_TYPE_NEW_MESSAGE) openMessageScreen(id)
        if (id != null && type == Constants.NOTIFICATION_TYPE_STATE_FRIEND) openFriendScreen()
    }

    private fun openMessageScreen(friendId: String) {
        val bundle = Bundle()
        bundle.putString(Constants.USER_ID, friendId)
        val fragment = MessagesFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .add(R.id.nav_host, fragment)
            .commit()
    }

    private fun openFriendScreen() {
        val fragment = HomeFragment()
        val bundle = Bundle()
        bundle.putString(Constants.MESSAGE_TYPE, Constants.FRIEND)
        fragment.arguments = bundle
    }

    override fun onStart() {
        super.onStart()
        networkConnectionManager.startListenNetworkState()
    }

    override fun onStop() {
        super.onStop()
        networkConnectionManager.stopListenNetworkState()
    }

    override fun onClickOk(type: Int?) {
        "Ok Activity".toast(this)
    }

    override fun onClickCancel(type: Int?) {
        "Cancel Activity".toast(this)
    }

}