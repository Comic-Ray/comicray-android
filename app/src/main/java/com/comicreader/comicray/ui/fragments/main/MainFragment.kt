package com.comicreader.comicray.ui.fragments.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.comicreader.comicray.R
import com.comicreader.comicray.TestComicData
import com.comicreader.comicray.TestMangaData
import com.comicreader.comicray.databinding.FragmentMainBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.fragments.comics.ComicsFragment
import com.comicreader.comicray.ui.fragments.discover.DiscoverFragment
import com.comicreader.comicray.ui.fragments.manga.MangaFragment
import com.comicreader.comicray.ui.fragments.read.ReadFragment
import com.kpstv.navigation.*
import kotlin.reflect.KClass

class MainFragment : ValueFragment(R.layout.fragment_main), FragmentNavigator.Transmitter {

    private lateinit var navigator: FragmentNavigator
    private lateinit var bottomNavController: BottomNavigationController

    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun getNavigator(): FragmentNavigator = navigator

    override val forceBackPress: Boolean
        get() = binding.bottomNavView.selectedItemId != R.id.Comics


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigator = FragmentNavigator.with(this, savedInstanceState)
            .initialize(binding.fragmentContainer)

        bottomNavController = navigator.install(object : FragmentNavigator.BottomNavigation() {
            override val bottomNavigationViewId: Int = R.id.bottom_nav_view
            override val bottomNavigationFragments: Map<Int, KClass<out Fragment>> =
                mapOf(
                    R.id.Comics to ComicsFragment::class,
                    R.id.Manga to MangaFragment::class,
                    R.id.Search to DiscoverFragment::class,
                )
            override val fragmentNavigationTransition = Animation.SlideHorizontally
        })

        // TODO(KP): Remove this code once detail screens are implemented.

        binding.testComicRead.setOnClickListener {
            val options = FragmentNavigator.NavOptions(
                args = ReadFragment.Args(
                    title = TestComicData.title,
                    episodeTitle = TestComicData.issueTitle,
                    url = TestComicData.url,
                    imageList = TestComicData.imageList
                ),
                animation = AnimationDefinition.Fade,
                remember = true,
            )
            getParentNavigator().navigateTo(ReadFragment::class, options)
        }

        binding.testMangaRead.setOnClickListener {
            val options = FragmentNavigator.NavOptions(
                args = ReadFragment.Args(
                    title = TestMangaData.title,
                    episodeTitle = TestMangaData.issueTitle,
                    url = TestMangaData.url,
                    imageList = TestMangaData.imageList
                ),
                animation = AnimationDefinition.Fade,
                remember = true
            )
            getParentNavigator().navigateTo(ReadFragment::class, options)
        }
    }

    override fun onBackPressed(): Boolean {
        if (binding.bottomNavView.selectedItemId != R.id.Comics) {
            bottomNavController.select(R.id.Comics)
            return true
        }
        return super.onBackPressed()
    }
}