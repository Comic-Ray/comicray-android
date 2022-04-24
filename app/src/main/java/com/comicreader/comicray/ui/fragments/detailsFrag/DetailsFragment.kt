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
import com.bumptech.glide.Glide
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.comicDetails.ComicDetailsResponse
import com.comicreader.comicray.databinding.FragmentDetailsBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.fragments.detailsFrag.controller.DetailsController
import com.comicreader.comicray.utils.Resource
import com.kpstv.navigation.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DetailsFragment : ValueFragment() {

    private val binding by viewBinding(FragmentDetailsBinding::bind)

    private val viewModel: DetailsViewModel by viewModels()

    private lateinit var detailsController: DetailsController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailsController = DetailsController()

        val args = getKeyArgs<DetailsArgs>()

        viewModel.onFetch(args.url, args.type)

        if (args.type == BookType.Comic){
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                    launch {
                        viewModel.detailsComic.collect{
                            when(it){
                                is Resource.Loading ->{
                                    binding.swipeRefreshLayout.isRefreshing = true
                                }

                                is Resource.Success -> {
                                    binding.swipeRefreshLayout.isRefreshing = false
                                    setComicDetails(it.data!!)
                                }

                                is Resource.Error -> {
                                    binding.swipeRefreshLayout.isRefreshing = false
                                    Toast.makeText(context,"${it.throwable?.localizedMessage}",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }//end of comic
        else{

        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onFetch(args.url,args.type)
        }
    }

    private fun setComicDetails(data: ComicDetailsResponse){
        binding.txtTitle.text = data.title
        binding.descriptiontxt.text = data.summary
        Glide.with(binding.imgView)
            .load(data.imageUrl)
            .into(binding.imgView)
        detailsController.submitComicChapters(data.issues)
        binding.recView.setController(detailsController)
        binding.recView.setHasFixedSize(true)
    }

    @Parcelize
    data class DetailsArgs(val name: String, val url: String, val type: BookType) : BaseArgs()

    companion object {
        fun FragmentNavigator.gotoDetails(name: String, url: String, type: BookType) {
            val options = FragmentNavigator.NavOptions(
                args = DetailsArgs(name = name, url = url, type = type),
                transaction = FragmentNavigator.TransactionType.REPLACE,
                animation = AnimationDefinition.SlideInRight,
                remember = true
            )
            navigateTo(DetailsFragment::class, options)
        }
    }

}