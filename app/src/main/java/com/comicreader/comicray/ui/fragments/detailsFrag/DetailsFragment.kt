package com.comicreader.comicray.ui.fragments.detailsFrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.comicDetails.ComicDetailsResponse
import com.comicreader.comicray.data.models.mangaDetails.MangaDetailsResponse
import com.comicreader.comicray.databinding.FragmentDetailsBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.fragments.detailsFrag.controller.ChipController
import com.comicreader.comicray.ui.fragments.detailsFrag.controller.DetailsController
import com.comicreader.comicray.utils.Resource
import com.kpstv.navigation.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DetailsFragment : ValueFragment(R.layout.fragment_details) {

    private val binding by viewBinding(FragmentDetailsBinding::bind)

    private val viewModel: DetailsViewModel by viewModels()

    private lateinit var detailsController: DetailsController

    private lateinit var chipController: ChipController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = getKeyArgs<DetailsArgs>()

        chipController = ChipController()
        chipController.submitType(args.type)

        detailsController = DetailsController()
        detailsController.submitType(args.type)

        viewModel.onFetch(args.url, args.type)

        if (args.type == BookType.Comic) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.detailsComic.collect {
                            when (it) {
                                is Resource.Loading -> {
                                    binding.swipeRefreshLayout.isEnabled = true
                                    binding.swipeRefreshLayout.isRefreshing = true
                                }

                                is Resource.Success -> {
                                    binding.swipeRefreshLayout.isEnabled = false
                                    setComicDetails(it.data!!)
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
    }

    private fun setMangaDetails(data: MangaDetailsResponse) {
        binding.txtTitle.text = data.title
        binding.descriptiontxt.text = data.summary
        Glide.with(binding.imgView)
            .load(data.imageUrl)
            .into(binding.imgView)
        binding.authortxt.text = data.authors[0].name
        binding.statusTxt.text = data.status

        //chip impl
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        chipController.spanCount = 2
        gridLayoutManager.spanSizeLookup = chipController.spanSizeLookup
        binding.chipRec.layoutManager = gridLayoutManager
        chipController.submitMangaGenres(data.genres)
        binding.chipRec.setController(chipController)
        binding.chipRec.setHasFixedSize(true)

        //details Controller IMPL
        detailsController.submitMangaChapters(data.chapters.asReversed())
        binding.recView.setController(detailsController)
        binding.recView.setHasFixedSize(true)
    }

    private fun setComicDetails(data: ComicDetailsResponse) {
        binding.txtTitle.text = data.title
        binding.descriptiontxt.text = data.summary
        Glide.with(binding.imgView)
            .load(data.imageUrl)
            .into(binding.imgView)
        binding.authortxt.text = data.author
        binding.statusTxt.text = data.status

        //chip impl
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        chipController.spanCount = 2
        gridLayoutManager.spanSizeLookup = chipController.spanSizeLookup
        binding.chipRec.layoutManager = gridLayoutManager
        chipController.submitComicGenres(data.genres)
        binding.chipRec.setController(chipController)
        binding.chipRec.setHasFixedSize(true)

        //details Controller IMPL
        detailsController.submitComicChapters(data.issues.asReversed())
        binding.recView.setController(detailsController)
        binding.recView.setHasFixedSize(true)
    }

    @Parcelize
    data class DetailsArgs(val name: String, val url: String, val type: BookType) : BaseArgs()

    companion object {
        fun getNavOptions(name: String, url: String, type: BookType): FragmentNavigator.NavOptions {
            return FragmentNavigator.NavOptions(
                args = DetailsArgs(name = name, url = url, type = type),
                transaction = FragmentNavigator.TransactionType.REPLACE,
                animation = AnimationDefinition.SlideInRight,
                remember = true
            )
        }
    }
}