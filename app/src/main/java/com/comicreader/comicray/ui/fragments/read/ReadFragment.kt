package com.comicreader.comicray.ui.fragments.read

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.databinding.FragmentReadBinding
import com.comicreader.comicray.extensions.hide
import com.comicreader.comicray.extensions.isExpanded
import com.comicreader.comicray.extensions.show
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.fragments.read.controller.ReadController
import com.kpstv.navigation.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ReadFragment : ValueFragment(R.layout.fragment_read) {
    private val binding by viewBinding(FragmentReadBinding::bind)

    private val controller: ReadController by lazy { ReadController() }

    private val viewModel by viewModels<ReadViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = getKeyArgs<Args>()

        setToolbarMenu()
        setRecyclerView()
        setProgressIndicator()
        setToolbarScrollBehavior()

        viewModel.getReadItem(args.url, args.type).observe(viewLifecycleOwner) { state ->
            binding.swipeRefreshLayout.isRefreshing = state is ReadViewModel.ReadUiState.Loading
            when(state) {
                ReadViewModel.ReadUiState.Loading -> {
                    binding.epoxyRecyclerView.hide()
                }
                is ReadViewModel.ReadUiState.Success -> {
                    binding.epoxyRecyclerView.show()
                    binding.toolbar.title = state.data.title
                    binding.toolbar.subtitle = state.data.issueName
                    controller.submitImageList(state.data.imageUrls)
                }
                is ReadViewModel.ReadUiState.Error -> {
                    state.error.printStackTrace()
                }
            }
        }
    }

    private fun setToolbarScrollBehavior() {
        binding.epoxyRecyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            private val gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent?): Boolean {
                    binding.appBarLayout.setExpanded(!binding.appBarLayout.isExpanded)
                    return super.onSingleTapUp(e)
                }
            })

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(e)
                return super.onInterceptTouchEvent(rv, e)
            }
        })
    }

    private fun setProgressIndicator() {
        binding.epoxyRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val scrollRange = recyclerView.computeVerticalScrollRange()
                val scrollOffset = recyclerView.computeVerticalScrollOffset()
                val scrollExtent = recyclerView.computeVerticalScrollExtent()

                if (scrollOffset > 0 && scrollExtent > 0 && scrollExtent > 0) {
                    val progress = (scrollOffset + scrollExtent) * 100 / scrollRange
                    binding.progressBar.setProgressCompat(progress, false)
                }
            }
        })
    }

    private fun setRecyclerView() {
        binding.epoxyRecyclerView.setControllerAndBuildModels(controller)
    }

    private fun setToolbarMenu() {
        binding.toolbar.setNavigationOnClickListener { goBack() }

        binding.toolbar.setOnMenuItemClickListener listener@{ item ->
            when(item.itemId) {
                R.id.action_share -> {
                    // TODO(KP): Deeplink support to directly open comics
                    return@listener true
                }
            }
            return@listener false
        }
    }

    @Parcelize
    data class Args(val url: String, val type: BookType) : BaseArgs()

    companion object {
        fun getNavOptions(url: String, type: BookType): FragmentNavigator.NavOptions {
            return FragmentNavigator.NavOptions(
                args = Args(url = url, type = type),
                animation = AnimationDefinition.Fade,
                remember = true
            )
        }
    }
}