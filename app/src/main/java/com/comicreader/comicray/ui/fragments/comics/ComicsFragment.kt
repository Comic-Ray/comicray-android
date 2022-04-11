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
import com.comicreader.comicray.controllers.MainScreenController
import com.comicreader.comicray.databinding.FragmentComicsBinding
import com.comicreader.comicray.utils.Resource
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

    private lateinit var controller: MainScreenController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentComicsBinding.bind(view)

        controller = MainScreenController()
        binding.recView.setController(controller)
        binding.recView.setHasFixedSize(true)


        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect {
                        when (it) {
                            is ComicsViewModel.Event.ShowErrorMessage ->
                                Toast.makeText(
                                    context,
                                    it.error.localizedMessage,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                        }
                    }
                }

            //OLD Working IMPL

//            launch {
//                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    viewModel.getAllComics().collect {
//                        adapter.submitList(it)
//                        adapter.notifyDataSetChanged()
//                    }
//                }
//            }
//
//            launch {
//                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    viewModel.isBusy.collect {
//                        when (it) {
//                            is Resource.Loading -> Toast.makeText(
//                                context,
//                                "Loading",
//                                Toast.LENGTH_SHORT
//                            ).show()
//
//                            is Resource.Success -> Toast.makeText(
//                                context,
//                                "Success ${it.data}",
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            is Resource.Error -> Toast.makeText(
//                                context,
//                                "Error",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//            }

            launch {
                viewModel.getFeaturedComics().collect {
                    controller.setFeaturedComics(it.data!!)
                }
            }

            launch {
                viewModel.getPopularComics().collect {

                }
            }

            launch {
                viewModel.getActionComics().collect {

                }
            }

        }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onManuelRefresh()
            binding.swipeRefreshLayout.isRefreshing = false
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