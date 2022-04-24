package com.comicreader.comicray.ui.fragments.detailsFrag.controller

import com.airbnb.epoxy.EpoxyController
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.comicDetails.Genre
import com.comicreader.comicray.epoxyModels.chip
import java.util.concurrent.CopyOnWriteArrayList

class ChipController : EpoxyController() {

    private var comicGenres: CopyOnWriteArrayList<Genre> = CopyOnWriteArrayList()
    private var mangaGenres: CopyOnWriteArrayList<com.comicreader.comicray.data.models.mangaDetails.Genre> =
        CopyOnWriteArrayList()

    private var type: BookType = BookType.Comic


    fun submitComicGenres(data: List<Genre>) {
        comicGenres.clear()
        comicGenres.addAll(data)
        requestModelBuild()
    }

    fun submitMangaGenres(data: List<com.comicreader.comicray.data.models.mangaDetails.Genre>) {
        mangaGenres.clear()
        mangaGenres.addAll(data)
        requestModelBuild()
    }

    fun submitType(type: BookType) {
        this.type = type
    }

    override fun buildModels() {
        if (type == BookType.Comic) {
            if (comicGenres.isNotEmpty()) {
                for (comicGenre in comicGenres) {
                    chip {
                        id("Genre" + comicGenre.tag)
                        title(comicGenre.name)
                    }
                }
            }
        }

        if (type == BookType.Manga) {
            if (mangaGenres.isNotEmpty()) {
                for (mangaGenre in mangaGenres) {
                    chip {
                        id("MangaGenre" + mangaGenre.category)
                        title(mangaGenre.name)
                    }
                }
            }
        }

    }
}
