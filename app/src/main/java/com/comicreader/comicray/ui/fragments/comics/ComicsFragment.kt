package com.comicreader.comicray.ui.fragments.comics

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.comicreader.comicray.R
import com.comicreader.comicray.adapters.ComicAdapter
import com.comicreader.comicray.databinding.FragmentComicsBinding
import com.comicreader.comicray.utils.Refresh
import com.comicreader.comicray.utils.Resource
import com.kpstv.navigation.ValueFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComicsFragment : ValueFragment(R.layout.fragment_comics) {

    private var _binding: FragmentComicsBinding? = null
    private val binding get() = _binding!!


    private val viewModel by viewModels<ComicsViewModel>()

    private lateinit var adapter: ComicAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentComicsBinding.bind(view)


        viewModel.collectionComics.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.featuredComics.collect {
                binding.progressbar.isVisible = it is Resource.Loading
                binding.featuredRecView.isVisible = it !is Resource.Loading
                binding.swipeRefreshLayout.isRefreshing = it is Resource.Loading
            }
        }



        adapter = ComicAdapter()
        binding.featuredRecView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.featuredRecView.adapter = adapter



        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onManuelRefresh()
        }


    }


    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}