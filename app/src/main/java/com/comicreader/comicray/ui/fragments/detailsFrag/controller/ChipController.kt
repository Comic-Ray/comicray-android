package com.comicreader.comicray.ui.fragments.detailsFrag.controller

import com.airbnb.epoxy.EpoxyController
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.Genre.Companion.toGenre
import com.comicreader.comicray.epoxyModels.chip
import java.util.concurrent.CopyOnWriteArrayList

class ChipController(
    private val goToGenre: (Genre) -> Unit
) : EpoxyController() {

    private var genres: CopyOnWriteArrayList<Genre> = CopyOnWriteArrayList()

    fun submitComicGenres(data: List<Genre>) {
        genres.clear()
        genres.addAll(data)
        requestModelBuild()
    }

    override fun buildModels() {
        for (genre in genres) {
            chip {
                id("Genre" + genre.tag)
                title(genre.name)
                marginHorizontalDp(3f)
                listener { this@ChipController.goToGenre(genre) }
            }
        }
    }
}
