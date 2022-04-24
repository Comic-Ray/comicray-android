package com.comicreader.comicray.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.comicreader.comicray.databinding.ActivityMainBinding
import com.comicreader.comicray.ui.fragments.main.MainFragment
import com.kpstv.navigation.*
import dagger.hilt.android.AndroidEntryPoint
import com.comicreader.comicray.extensions.AbstractNavigationOptionsExtensions.consume


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FragmentNavigator.Transmitter{

    private lateinit var navigator : FragmentNavigator
    override fun getNavigator(): FragmentNavigator = navigator

    private val navViewModel by viewModels<MainNavViewModel>()

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigator = FragmentNavigator.with(this,savedInstanceState)
            .initialize(binding.fragmentContainer, Destination.Companion.of(MainFragment::class))

        navViewModel.navigation.observe(this) { options ->
            options?.consume { navigator.navigateTo(options.clazz, options.navOptions) }
        }
    }

    override fun onBackPressed() {
        if (navigator.canFinish()){
            super.onBackPressed()
        }
    }
}