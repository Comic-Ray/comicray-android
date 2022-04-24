package com.comicreader.comicray.ui.fragments.manga

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.comicreader.comicray.R
import com.comicreader.comicray.controllers.MainScreenController
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.databinding.FragmentMangaBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.activities.MainNavViewModel
import com.comicreader.comicray.ui.activities.MainRoutes
import com.comicreader.comicray.ui.fragments.detailsFrag.DetailsFragment
import com.comicreader.comicray.ui.fragments.more.MoreFragment
import com.comicreader.comicray.utils.Constants.Manga
import com.kpstv.navigation.ValueFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MangaFragment : ValueFragment(R.layout.fragment_manga) {

    private val navViewModel by activityViewModels<MainNavViewModel>()
    private val viewModel: MangaViewModel by viewModels()

    private val binding by viewBinding(FragmentMangaBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = MainScreenController(
            goToGenre = { genre ->
                val options = MoreFragment.getGenreNavOptions(genre)
                navViewModel.navigateTo(MainRoutes.MORE, options)
            },
            goToDetail = { data ->
                val options = DetailsFragment.getNavOptions(data.title, data.url, data.type)
                navViewModel.navigateTo(MainRoutes.DETAIL, options)
            }
        )
        controller.submitType(Manga)
        binding.recView.setController(controller)
        binding.recView.setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getTrendingComics().collect {
                controller.addOrReplace(Genre(name = "Trending", tag = "all", type = BookType.Manga), it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getComedyComics().collect {
                controller.addOrReplace(Genre(name = "Comedy", tag = "6", type = BookType.Manga), it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAdventureComics().collect {
                controller.addOrReplace(Genre(name = "Adventure", tag = "4", type = BookType.Manga), it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDramaComics().collect {
                controller.addOrReplace(Genre(name = "Drama", tag = "10", type = BookType.Manga), it)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            controller.submitEmptyList()
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.onManualRefresh()
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

}