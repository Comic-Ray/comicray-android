package com.comicreader.comicray.ui.fragments.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.FragmentSearchBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.fragments.search.controller.SearchController
import com.comicreader.comicray.utils.Resource
import com.kpstv.navigation.ValueFragment
import dagger.hilt.android.AndroidEntryPoint
import extensions.hide
import extensions.show

@AndroidEntryPoint
class SearchFragment : ValueFragment(R.layout.fragment_search) {
    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val viewModel by viewModels<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = SearchController(requireContext())
        binding.epoxyRecyclerView.setController(controller)

        binding.toolbar.setNavigationOnClickListener { goBack() }

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

        binding.btnClose.setOnClickListener {
            binding.etSearch.setText("")
        }

        binding.etSearch.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                if (text?.isNotEmpty() == true) {
                    binding.btnClose.show()
                } else {
                    binding.btnClose.hide()
                }
                viewModel.setSearchQuery(text?.toString() ?: "")
            }
        )

        viewModel.searchData.observe(viewLifecycleOwner) { data ->
            controller.setSearch(data)
        }

        binding.etSearch.requestFocus()
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }
}