package com.comicreader.comicray.ui.fragments.comics

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.comicreader.comicray.R
import com.comicreader.comicray.controllers.MainScreenController
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.databinding.FragmentComicsBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.activities.MainNavViewModel
import com.comicreader.comicray.ui.activities.MainRoutes
import com.comicreader.comicray.ui.fragments.detailsFrag.DetailsFragment
import com.comicreader.comicray.ui.fragments.more.MoreFragment
import com.comicreader.comicray.utils.Constants.Comics
import com.comicreader.comicray.utils.Event
import com.kpstv.navigation.ValueFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComicsFragment : ValueFragment(R.layout.fragment_comics) {

    private val binding by viewBinding(FragmentComicsBinding::bind)

    private val navViewModel by activityViewModels<MainNavViewModel>()
    private val viewModel by viewModels<ComicsViewModel>()

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
        controller.submitType(Comics)
        binding.recView.setController(controller)
        binding.recView.setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFeaturedComics().collect {
                   controller.setFeaturedComics(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getActionComics().collect {
                controller.addOrReplace(Genre("Action", tag = "action-comic", type = BookType.Comic), it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPopularComics().collect {
                controller.addOrReplace(Genre("Popular", tag = "popular-comic", type = BookType.Comic), it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect {
                        when (it) {
                            is Event.ShowErrorMessage ->
                                Toast.makeText(
                                    context,
                                    it.error.localizedMessage,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                        }
                    }
                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            controller.submitEmptyList()
            viewModel.onManualRefresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }
}
