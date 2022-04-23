package com.comicreader.comicray.ui.fragments.genre.controller

import com.airbnb.epoxy.AsyncEpoxyController
import com.comicreader.comicray.data.models.custom.ComicDetails
import java.util.concurrent.CopyOnWriteArrayList

class GenreController : AsyncEpoxyController() {

    private val items = CopyOnWriteArrayList<ComicDetails>()

    fun submitList(list: List<ComicDetails>) {
        items.clear()
        items.addAll(list)
        requestModelBuild()
    }

    override fun buildModels() {

    }
}