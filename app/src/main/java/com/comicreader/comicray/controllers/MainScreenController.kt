package com.comicreader.comicray.controllers

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.comicreader.comicray.data.models.DataItem
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.epoxyModels.CardModel_
import com.comicreader.comicray.epoxyModels.loaderView
import com.comicreader.comicray.epoxyModels.overline
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class MainScreenController(private val goToGenre: (Genre) -> Unit, private val goToDetail: (DataItem) -> Unit) : AsyncEpoxyController() {

    private val dataMap = ConcurrentHashMap<Genre, List<DataItem>>()
    private var featuredComics: CopyOnWriteArrayList<DataItem> = CopyOnWriteArrayList()

    private var comicType: String = ""

    fun setFeaturedComics(data: List<DataItem>) {
        featuredComics.clear()
        featuredComics.addAll(data)
        requestModelBuild()
    }

    fun addOrReplace(genre: Genre, data: List<DataItem>) {
        this.dataMap[genre] = data
        requestModelBuild()
    }

    fun submitType(comicType: String) {
        this.comicType = comicType
    }

    fun submitEmptyList() {
        featuredComics.clear()
        dataMap.clear()
        requestModelBuild()
    }

    override fun buildModels() {
        Carousel.setDefaultGlobalSnapHelperFactory(null)

        if (featuredComics.isEmpty() && dataMap.isEmpty()) {
            loaderView {
                id("loader-view")
                titleText("Featured")
                titleText2("Popular")
                titleText3("Action")
            }
            return
        }

        if (featuredComics.isNotEmpty()) {
            overline {
                id("featuredComic")
                value("Featured Comics")
                moreAvailable(false)
            }
            carousel {
                id("id-feat-comics")
                models(this@MainScreenController.featuredComics.map { item ->
                    CardModel_().id("featured-comics-id:" + item.title)
                        .title(item.title)
                        .urlToImage(item.imageUrl)
                        .listener { _ ->
                            this@MainScreenController.goToDetail(item)
                        }
                })
            }
        }

        for((genre, data) in dataMap) {
            overline {
                id("overline" + genre.name + genre.tag)
                value(genre.name)
                moreAvailable(true)
                listener { _ -> this@MainScreenController.goToGenre(genre) }
            }
            carousel {
                id("carousal" + genre.name + genre.tag)
                models(data.map { item ->
                    CardModel_().id("id" + item.title)
                        .title(item.title)
                        .urlToImage(item.imageUrl)
                        .listener { _ ->
                            this@MainScreenController.goToDetail(item)
                        }
                })
            }
        }
    }
}