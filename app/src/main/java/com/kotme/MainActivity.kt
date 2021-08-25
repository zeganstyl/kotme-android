package com.kotme

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        val req = NetworkRequest.Builder()
//            .addCapability(NET_CAPABILITY_INTERNET)
//            .build()
//        connectivityManager.registerNetworkCallback(req, object : ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: Network) {
//                println("onAvailable")
//            }
//        })

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        val viewModel by viewModels<MainActivityViewModel>()
    }
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val repo: KotmeRepository): ViewModel() {
    fun getUpdates() {
        viewModelScope.launch { repo.getUpdates(0) }
    }
}