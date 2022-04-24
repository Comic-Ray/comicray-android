package com.comicreader.comicray.ui.fragments.genre

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.databinding.FragmentGenreBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.fragments.genre.controller.GenreController
import com.kpstv.navigation.*
import dagger.hilt.android.AndroidEntryPoint
import extensions.hide
import extensions.show
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class GenreFragment : ValueFragment(R.layout.fragment_genre) {
    private val binding by viewBinding(FragmentGenreBinding::bind)

    private val viewModel by viewModels<GenreViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = GenreController()

        val args = getKeyArgs<Args>()

        binding.toolbar.title = getString(R.string.genre_title, args.name)
        binding.toolbar.setNavigationOnClickListener {
            goBack()
        }

        val listing = viewModel.getData(args.tag, args.type)

        listing.data.observe(viewLifecycleOwner) { items ->
            controller.submitList(items)
        }

        listing.initialLoadState.observe(viewLifecycleOwner) { state ->
            binding.swipeRefreshLayout.isRefreshing = state is GenreLoadState.Loading
            binding.progressBar.hide()
            when(state) {
                is GenreLoadState.Loading, GenreLoadState.Success -> {
                    binding.epoxyRecyclerView.show()
                    binding.btnRetry.hide()
                }
                is GenreLoadState.Error -> {
                    binding.epoxyRecyclerView.hide()
                    binding.btnRetry.show()
                }
                else -> {}
            }
        }

        listing.loadMoreState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is GenreLoadState.Loading -> binding.progressBar.show()
                is GenreLoadState.Success -> binding.progressBar.hide()
                is GenreLoadState.Error -> {
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
    data class Args(val name: String, val tag: String, val type: BookType) : BaseArgs()

    companion object {
        fun FragmentNavigator.goToGenre(name: String, tag: String, type: BookType) {
            val options = FragmentNavigator.NavOptions(
                args = Args(name = name, tag = tag, type = type),
                transaction = FragmentNavigator.TransactionType.ADD,
                animation = AnimationDefinition.SlideInRight,
                remember = true
            )
            navigateTo(GenreFragment::class, options)
        }
    }
}