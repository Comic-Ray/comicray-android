package com.comicreader.comicray.ui.fragments.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.FragmentSearchBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.fragments.search.controller.SearchController
import com.comicreader.comicray.utils.Resource
import com.kpstv.navigation.ValueFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : ValueFragment(R.layout.fragment_search) {
    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val viewModel by viewModels<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = SearchController(requireContext())
        binding.epoxyRecyclerView.setController(controller)

        viewModel.getComicGenreList().observe(viewLifecycleOwner) { state ->
            if (state is Resource.Success && state.data != null) {
                controller.setComicGenres(state.data)
            }
        }

        viewModel.getMangaGenreList().observe(viewLifecycleOwner) { state ->
            if (state is Resource.Success && state.data != null) {
                controller.setMangaGenres(state.data)
            }
        }
    }
}