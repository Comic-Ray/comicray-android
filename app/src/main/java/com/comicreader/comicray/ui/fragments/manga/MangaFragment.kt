package com.comicreader.comicray.ui.fragments.manga

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.comicreader.comicray.R
import com.comicreader.comicray.controllers.MainScreenController
import com.comicreader.comicray.databinding.FragmentMangaBinding
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.utils.Constants.Manga
import com.kpstv.navigation.ValueFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MangaFragment : ValueFragment(R.layout.fragment_manga) {

    private val viewModel: MangaViewModel by viewModels()

    private val binding by viewBinding(FragmentMangaBinding::bind)

    private lateinit var controller: MainScreenController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = MainScreenController()
        controller.submitType(Manga)
        binding.recView.setController(controller)
        binding.recView.setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.getTrendingComics().collect{
                controller.submitTrendingManga(it.data)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.getComedyComics().collect{
                    controller.submitComedyManga(it.data)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.getAdventureComics().collect{
                    controller.submitAdventureManga(it.data)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.getDramaComics().collect{
                    controller.submitDramaManga(it.data)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            controller.submitEmptyListManga()
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.onManualRefresh()
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

}