package com.comicreader.comicray.ui.fragments.search.controller

import android.content.Context
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.carousel
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.epoxyModels.Chip_
import com.comicreader.comicray.epoxyModels.header
import com.comicreader.comicray.epoxyModels.spacer
import java.util.concurrent.CopyOnWriteArrayList

class SearchController(private val context: Context) : EpoxyController() {

    private val comicGenres = CopyOnWriteArrayList<Genre.Comic>()
    private val mangaGenres = CopyOnWriteArrayList<Genre.Manga>()

    fun setComicGenres(list: List<Genre.Comic>) {
        comicGenres.clear()
        comicGenres.addAll(list)
        requestModelBuild()
    }

    fun setMangaGenres(list: List<Genre.Manga>) {
        mangaGenres.clear()
        mangaGenres.addAll(list)
        requestModelBuild()
    }

    override fun buildModels() {
        val context = this@SearchController.context
        if (comicGenres.isNotEmpty()) {
            spacer {
                id("comic-spacer-height")
                heightDp(10f)
            }
            header {
                id("comic-header")
                title(context.getString(R.string.genre_popular, context.getString(R.string.comics)))
                marginHorizontalDp(10f)
            }
            spacer {
                id("comic-spacer-height2")
                heightDp(10f)
            }
            carousel {
                id("comic-chips")
                models(this@SearchController.comicGenres.map { model ->
                    Chip_().id(model.tag).title(model.name)
                        .listener {

                        }
                })
            }
        }
        if (mangaGenres.isNotEmpty()) {
//            spacer {
//                id("comic-spacer-height")
//                heightDp(10f)
//            }
            header {
                id("manga-header")
                title(context.getString(R.string.genre_popular, context.getString(R.string.manga)))
                marginHorizontalDp(10f)
            }
//            spacer {
//                id("manga-spacer-height")
//                heightDp(10f)
//            }
            carousel {
                id("manga-chips")
                models(this@SearchController.mangaGenres.map { model ->
                    Chip_().id(model.category).title(model.name)
                        .listener {

                        }
                })
            }
        }
    }
}