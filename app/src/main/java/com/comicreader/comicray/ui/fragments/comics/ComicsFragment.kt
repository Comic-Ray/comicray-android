package com.comicreader.comicray.ui.fragments.comics

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.comicreader.comicray.R
import com.comicreader.comicray.adapters.ComicAdapter
import com.comicreader.comicray.databinding.FragmentComicsBinding
import com.kpstv.navigation.ValueFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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

        setupRecView()

//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            viewModel.collectionComicsFlow.collect {
//                adapter.submitList(it)
//            }
//        }

//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            viewModel.collectionF.collect {
//                adapter.submitList(it)
//            }
//        }

//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            viewModel.featuredComics.collect {
//                binding.progressbar.isVisible = it is Resource.Loading
//                binding.featuredRecView.isVisible = it !is Resource.Loading
//                binding.swipeRefreshLayout.isRefreshing = it is Resource.Loading
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect {
                when (it) {
                    is ComicsViewModel.Event.showErrorMessage ->
                        Toast.makeText(context, it.error.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
//                    viewModel.comicList.collect {
//                        Log.d("TAGG", "onViewCreated inside New Funtion: $it")
//                        adapter.submitList(it)
//                        adapter.notifyDataSetChanged()
//                    }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.getAllComics().collect {

                    adapter.submitList(it)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onManuelRefresh()
        }
    }

    private fun setupRecView() {
        adapter = ComicAdapter()
        binding.featuredRecView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.featuredRecView.adapter = adapter
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