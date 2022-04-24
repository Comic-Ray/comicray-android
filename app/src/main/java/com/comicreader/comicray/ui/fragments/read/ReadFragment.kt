package com.comicreader.comicray.ui.fragments.read

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.FragmentReadBinding
import com.comicreader.comicray.extensions.isExpanded
import com.comicreader.comicray.extensions.viewBinding
import com.comicreader.comicray.ui.fragments.read.epoxy.ReadController
import com.kpstv.navigation.BaseArgs
import com.kpstv.navigation.ValueFragment
import com.kpstv.navigation.getKeyArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ReadFragment : ValueFragment(R.layout.fragment_read) {
    private val binding by viewBinding(FragmentReadBinding::bind)

    private val args: Args by lazy { getKeyArgs() }
    private val controller: ReadController by lazy { ReadController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = args.title
        binding.toolbar.subtitle = args.episodeTitle

        setToolbarMenu()
        setRecyclerView()
        setProgressIndicator()
        setToolbarScrollBehavior()
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
        controller.submitImageList(args.imageList)
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
    data class Args(val title: String, val episodeTitle: String, val url: String, val imageList: List<String>) : BaseArgs()
}