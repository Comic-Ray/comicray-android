package com.comicreader.comicray.ui.fragments.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.FragmentSearchBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.activities.MainNavViewModel
import com.comicreader.comicray.ui.activities.MainRoutes
import com.comicreader.comicray.ui.fragments.more.MoreFragment
import com.comicreader.comicray.ui.fragments.search.controller.SearchController
import com.comicreader.comicray.utils.Resource
import com.kpstv.navigation.ValueFragment
import dagger.hilt.android.AndroidEntryPoint
import com.comicreader.comicray.extensions.hide
import com.comicreader.comicray.extensions.show
import com.comicreader.comicray.ui.fragments.detailsFrag.DetailsFragment

@AndroidEntryPoint
class SearchFragment : ValueFragment(R.layout.fragment_search) {
    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val navViewModel by activityViewModels<MainNavViewModel>()
    private val viewModel by viewModels<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = SearchController(
            context = requireContext(),
            goToGenre = { genre ->
                val options = MoreFragment.getGenreNavOptions(genre)
                navViewModel.navigateTo(MainRoutes.MORE, options)
            },
            goToMoreSearch = { type ->
                val options = MoreFragment.getSearchNavOptions(viewModel.getCurrentQuery(), type = type)
                navViewModel.navigateTo(MainRoutes.MORE, options)
            },
            goToDetail = { data ->
                val options = DetailsFragment.getNavOptions(data.title, data.url, data.type)
                navViewModel.navigateTo(MainRoutes.DETAIL, options)
            }
        )
        binding.epoxyRecyclerView.setController(controller)

        binding.toolbar.setNavigationOnClickListener {
            (parentFragment as? ValueFragment)?.onBackPressed()
        }

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