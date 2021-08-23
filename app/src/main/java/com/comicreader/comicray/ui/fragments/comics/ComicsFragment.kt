package com.comicreader.comicray.ui.fragments.comics

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.comicreader.comicray.R
import com.comicreader.comicray.adapters.ComicAdapter
import com.comicreader.comicray.databinding.FragmentComicsBinding
import com.comicreader.comicray.utils.Resource
import com.kpstv.navigation.ValueFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComicsFragment : ValueFragment(R.layout.fragment_comics) {

    private var _binding: FragmentComicsBinding? = null
    private val binding get() = _binding!!

//    private lateinit var featuredComics: CustomComicLayout

    private val viewModel by viewModels<ComicsViewModel>()

    private lateinit var adapter: ComicAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentComicsBinding.bind(view)


        viewModel.collectionComics.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

//        viewModel.allComics.observe(viewLifecycleOwner, {
//            when (it) {
//                is Resource.Loading -> binding.shimmerContainer.startShimmer()
//                is Resource.Success -> binding.shimmerContainer.stopShimmer()
//                else -> Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
//            }
//        })



        adapter = ComicAdapter()
        binding.featuredRecView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.featuredRecView.adapter = adapter



        binding.swipeRefreshLayout.setOnRefreshListener {
            // TODO: 8/19/2021 need to implement this 
            viewModel.onManuelRefresh()
        }

    }


    override fun onStart() {
        super.onStart()
        // TODO: 8/19/2021 need to implement this 
        viewModel.onStart()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}