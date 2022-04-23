package com.comicreader.comicray.ui.fragments.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.FragmentMainBinding
import com.comicreader.comicray.ui.fragments.comics.ComicsFragment
import com.comicreader.comicray.ui.fragments.discover.DiscoverFragment
import com.comicreader.comicray.ui.fragments.manga.MangaFragment
import com.kpstv.navigation.BottomNavigationController
import com.kpstv.navigation.FragmentNavigator
import com.kpstv.navigation.ValueFragment
import com.kpstv.navigation.install
import kotlin.reflect.KClass

class MainFragment : ValueFragment(R.layout.fragment_main), FragmentNavigator.Transmitter {

    private lateinit var navigator : FragmentNavigator
    private lateinit var bottomNavController : BottomNavigationController

    private var _binding : FragmentMainBinding?=null
    private val binding get() = _binding!!

    override fun getNavigator(): FragmentNavigator = navigator

    override val forceBackPress: Boolean
        get() = _binding?.bottomNavView?.selectedItemId != R.id.Comics


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMainBinding.bind(view)

        navigator = FragmentNavigator.with(this,savedInstanceState)
            .initialize(binding.fragmentContainer)

        bottomNavController = navigator.install(object : FragmentNavigator.BottomNavigation(){
            // The @IdRes of the BottomNavigationView
            override val bottomNavigationViewId: Int = R.id.bottom_nav_view

            override val bottomNavigationFragments: Map<Int, KClass<out Fragment>> =
                mapOf(
                    R.id.Comics to ComicsFragment::class,
                    R.id.Manga to MangaFragment::class,
                    R.id.Search to DiscoverFragment::class,
                )

            // Slide from left/right animation when selection is changed.
            override val fragmentNavigationTransition = Animation.SlideHorizontally
        })

    }

    override fun onBackPressed(): Boolean {
        if (binding.bottomNavView.selectedItemId != R.id.Comics) {
            bottomNavController.select(R.id.Comics)
            return true
        }
        return super.onBackPressed()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}