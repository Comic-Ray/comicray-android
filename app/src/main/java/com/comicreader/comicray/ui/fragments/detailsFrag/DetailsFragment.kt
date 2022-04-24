package com.comicreader.comicray.ui.fragments.detailsFrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.comicDetails.ComicDetailsResponse
import com.comicreader.comicray.data.models.custom.toCommonDetail
import com.comicreader.comicray.data.models.mangaDetails.MangaDetailsResponse
import com.comicreader.comicray.databinding.FragmentDetailsBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.activities.MainNavViewModel
import com.comicreader.comicray.ui.activities.MainRoutes
import com.comicreader.comicray.ui.fragments.detailsFrag.controller.ChipController
import com.comicreader.comicray.ui.fragments.detailsFrag.controller.DetailsController
import com.comicreader.comicray.ui.fragments.more.MoreFragment
import com.comicreader.comicray.ui.fragments.read.ReadFragment
import com.comicreader.comicray.ui.fragments.read.ReadViewModel
import com.comicreader.comicray.utils.Resource
import com.kpstv.navigation.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DetailsFragment : ValueFragment(R.layout.fragment_details) {

    private val binding by viewBinding(FragmentDetailsBinding::bind)

    private val viewModel: DetailsViewModel by viewModels()
    private val navViewModel by activityViewModels<MainNavViewModel>()

    private lateinit var detailsController: DetailsController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = getKeyArgs<DetailsArgs>()

        binding.swipeRefreshLayout.isEnabled = false
        binding.fab.hide()

        binding.toolbar.setNavigationOnClickListener { goBack() }

        detailsController = DetailsController(
            goToRead = { url, type ->
                val options = ReadFragment.getNavOptions(url, type)
                navViewModel.navigateTo(MainRoutes.READ, options)
            },
            goToGenre = { genre ->
                val options = MoreFragment.getGenreNavOptions(genre)
                navViewModel.navigateTo(MainRoutes.MORE, options)
            },
        )
        detailsController.submitType(args.type)
        binding.recView.setController(detailsController)

        viewModel.onFetch(args.url, args.type)

        if (args.type == BookType.Comic) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.detailsComic.collect {
                            binding.swipeRefreshLayout.isRefreshing = it is Resource.Loading
                            when (it) {
                                is Resource.Loading -> { }
                                is Resource.Success -> {
                                    setComicDetails(it.data!!)
                                }
                                is Resource.Error -> {
                                    Toast.makeText(
                                        context,
                                        "${it.throwable?.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }//end of comic
        else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.detailsManga.collect {
                            when (it) {
                                is Resource.Loading -> {
                                    binding.swipeRefreshLayout.isEnabled = true
                                    binding.swipeRefreshLayout.isRefreshing = true
                                }

                                is Resource.Success -> {
                                    binding.swipeRefreshLayout.isEnabled = false
                                    setMangaDetails(it.data!!)
                                }

                                is Resource.Error -> {
                                    binding.swipeRefreshLayout.isEnabled = true
                                    Toast.makeText(
                                        context,
                                        "${it.throwable?.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }


        binding.fab.setOnClickListener {
            binding.recView.smoothScrollBy(0, -binding.recView.computeVerticalScrollExtent())
        }

        binding.recView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val height = binding.recView.computeVerticalScrollOffset()
                    if (height > resources.displayMetrics.widthPixels) {
                        binding.fab.show()
                    } else {
                        binding.fab.hide()
                    }
                }
            }
        })
    }

    private fun setMangaDetails(data: MangaDetailsResponse) {
        detailsController.detail = data.toCommonDetail()
        detailsController.submitMangaChapters(data.chapters.asReversed())
    }

    private fun setComicDetails(data: ComicDetailsResponse) {
        detailsController.detail = data.toCommonDetail()
        detailsController.submitComicChapters(data.issues.asReversed())
    }

    @Parcelize
    data class DetailsArgs(val name: String, val url: String, val type: BookType) : BaseArgs()

    companion object {
        fun getNavOptions(name: String, url: String, type: BookType): FragmentNavigator.NavOptions {
            return FragmentNavigator.NavOptions(
                args = DetailsArgs(name = name, url = url, type = type),
                transaction = FragmentNavigator.TransactionType.ADD,
                animation = AnimationDefinition.SlideInRight,
                remember = true
            )
        }
    }
}