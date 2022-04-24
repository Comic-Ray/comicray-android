package com.comicreader.comicray.ui.fragments.search.controller

import android.content.Context
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.carousel
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.DataItem
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.Genre.Companion.toGenre
import com.comicreader.comicray.data.models.custom.ComicSearchResponse
import com.comicreader.comicray.data.models.custom.MangaSearchResponse
import com.comicreader.comicray.data.models.custom.SearchCommon
import com.comicreader.comicray.epoxyModels.*
import java.util.concurrent.CopyOnWriteArrayList

class SearchController(
    private val context: Context,
    private val goToGenre: (Genre) -> Unit,
    private val goToMoreSearch: (type: BookType) -> Unit,
    private val goToDetail: (title: String, url: String, type: BookType) -> Unit,
) : EpoxyController() {

    private val comicGenres = CopyOnWriteArrayList<Genre.Comic>()
    private val mangaGenres = CopyOnWriteArrayList<Genre.Manga>()

    private var searches: ArrayList<SearchCommon>? = null

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

    fun setSearch(list: List<SearchCommon>?) {
        searches?.clear()
        if (list != null) {
            searches = ArrayList(list)
        } else {
            searches = null
        }
        requestModelBuild()
    }

    override fun buildModels() {
        val context = this@SearchController.context

        val searches = this.searches

        if (searches != null && searches.isEmpty()) {
            spacer {
                id("progress-spacer")
                heightDp(10f)
            }
            progressBar { id("load-progress") }
            spacer {
                id("progress-spacer2")
                heightDp(10f)
            }
        } else if (searches?.isNotEmpty() == true) {
            val comicSearches = searches.firstOrNull { it.type == BookType.Comic }?.additionalSearchData as? ComicSearchResponse
            val mangaSearches = searches.firstOrNull { it.type == BookType.Manga }?.additionalSearchData as? MangaSearchResponse
            spacer {
                id("search-spacer")
                heightDp(10f)
            }
            if (comicSearches == null && mangaSearches == null) {
                progressBar { id("load-progress") }
            } else {
                if (comicSearches != null && comicSearches.data.isNotEmpty()) {
                    overline {
                        id("comic-search-header")
                        value(context.getString(R.string.comics))
                        moreAvailable(comicSearches.totalPages > 1)
                        listener { _ -> this@SearchController.goToMoreSearch(BookType.Comic) }
                    }
                    carousel {
                        id("comic-search-results")
                        models(comicSearches.data.map { model ->
                            CardModel_().id(model.title.hashCode())
                                .title(model.title)
                                .urlToImage(model.imageUrl)
                                .listener { _ -> this@SearchController.goToDetail(model.title, model.url, BookType.Comic) }
                        })
                    }
                }
                if (mangaSearches != null && mangaSearches.data.isNotEmpty()) {
                    overline {
                        id("manga-search-header")
                        value(context.getString(R.string.manga))
                        moreAvailable(mangaSearches.totalPages > 1)
                        listener { _ -> this@SearchController.goToMoreSearch(BookType.Manga) }
                    }
                    carousel {
                        id("manga-search-results")
                        models(mangaSearches.data.map { model ->
                            CardModel_().id(model.title.hashCode())
                                .title(model.title)
                                .urlToImage(model.imageUrl)
                                .listener { _ -> this@SearchController.goToDetail(model.title, model.url, BookType.Manga) }
                        })
                    }
                }
            }
        } else {
            spacer {
                id("empty-spacer")
                heightDp(10f)
            }
        }

        if (comicGenres.isNotEmpty()) {
            header {
                id("comic-header")
                title(context.getString(R.string.genre_popular, context.getString(R.string.comics)))
                marginHorizontalDp(15f)
            }
            carousel {
                id("comic-chips")
                models(this@SearchController.comicGenres.map { model ->
                    Chip_().id(model.tag).title(model.name)
                        .listener { this@SearchController.goToGenre(model.toGenre()) }
                })
            }
        }
        if (mangaGenres.isNotEmpty()) {
            header {
                id("manga-header")
                title(context.getString(R.string.genre_popular, context.getString(R.string.manga)))
                marginHorizontalDp(15f)
            }
            carousel {
                id("manga-chips")
                models(this@SearchController.mangaGenres.filterNot { it.name == "Adult" }.map { model ->
                    Chip_().id(model.category).title(model.name)
                        .listener { this@SearchController.goToGenre(model.toGenre()) }
                })
            }
        }
    }
}