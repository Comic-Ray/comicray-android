package com.comicreader.comicray.ui.fragments.genre

import android.os.Bundle
import android.view.View
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.GenreType
import com.comicreader.comicray.databinding.FragmentGenreBinding
import com.comicreader.comicray.extensions.viewBinding
import com.kpstv.navigation.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class GenreFragment : ValueFragment(R.layout.fragment_genre) {
    private val binding by viewBinding(FragmentGenreBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = getKeyArgs<Args>()

    }

    @Parcelize
    data class Args(val name: String, val tag: String, val type: GenreType) : BaseArgs()

    companion object {
        fun FragmentNavigator.goToGenre(name: String, tag: String, type: GenreType) {
            val options = FragmentNavigator.NavOptions(
                args = Args(name = name, tag = tag, type = type),
                transaction = FragmentNavigator.TransactionType.ADD,
                animation = AnimationDefinition.SlideInRight,
                remember = true
            )
            navigateTo(GenreFragment::class, options)
        }
    }
}