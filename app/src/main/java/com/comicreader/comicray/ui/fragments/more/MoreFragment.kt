package com.comicreader.comicray.ui.fragments.more

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.databinding.FragmentGenreBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.fragments.more.controller.MoreController
import com.kpstv.navigation.*
import dagger.hilt.android.AndroidEntryPoint
import extensions.hide
import extensions.show
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class MoreFragment : ValueFragment(R.layout.fragment_genre) {
    private val binding by viewBinding(FragmentGenreBinding::bind)

    private val viewModel by viewModels<MoreViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = MoreController()

        val isGenreArgs = hasKeyArgs<GenreArgs>()
        val isSearchArgs = hasKeyArgs<SearchArgs>()

        val listing = if (isGenreArgs) {
            val args = getKeyArgs<GenreArgs>()
            binding.toolbar.title = getString(R.string.genre_title, args.name)
            viewModel.getGenreData(args.tag, args.type)
        } else if (isSearchArgs) {
            val args = getKeyArgs<SearchArgs>()
            binding.toolbar.title = getString(R.string.search_on, args)
            viewModel.getSearchData(args.query, args.type)
        } else throw IllegalStateException("Unhandled case")

        binding.toolbar.setNavigationOnClickListener {
            goBack()
        }

        listing.data.observe(viewLifecycleOwner) { items ->
            controller.submitList(items)
        }

        listing.initialLoadState.observe(viewLifecycleOwner) { state ->
            binding.swipeRefreshLayout.isRefreshing = state is MoreLoadState.Loading
            binding.progressBar.hide()
            when(state) {
                is MoreLoadState.Loading, MoreLoadState.Success -> {
                    binding.epoxyRecyclerView.show()
                    binding.btnRetry.hide()
                }
                is MoreLoadState.Error -> {
                    binding.epoxyRecyclerView.hide()
                    binding.btnRetry.show()
                }
                else -> {}
            }
        }

        listing.loadMoreState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is MoreLoadState.Loading -> binding.progressBar.show()
                is MoreLoadState.Success -> binding.progressBar.hide()
                is MoreLoadState.Error -> {
                    // TODO: Handle Error in more screen
                    binding.progressBar.hide()
                }
                else -> {}
            }
        }


        binding.btnRetry.setOnClickListener {
            listing.onRefresh.invoke()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            listing.onRefresh.invoke()
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        controller.spanCount = 3
        gridLayoutManager.spanSizeLookup = controller.spanSizeLookup
        binding.epoxyRecyclerView.layoutManager = gridLayoutManager
        binding.epoxyRecyclerView.setController(controller)
    }

    @Parcelize
    data class GenreArgs(val name: String, val tag: String, val type: BookType) : BaseArgs()

    @Parcelize
    data class SearchArgs(val query: String, val type: BookType) : BaseArgs()

    companion object {
        fun getGenreNavOptions(genre: Genre): FragmentNavigator.NavOptions {
            return FragmentNavigator.NavOptions(
                args = GenreArgs(name = genre.name, tag = genre.tag, type = genre.type),
                transaction = FragmentNavigator.TransactionType.ADD,
                animation = AnimationDefinition.SlideInRight,
                remember = true
            )
        }
    }
}