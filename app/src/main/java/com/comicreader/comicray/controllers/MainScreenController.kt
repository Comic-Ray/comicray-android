package com.comicreader.comicray.controllers

import com.airbnb.epoxy.*
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.epoxyModels.CardModel_
import com.comicreader.comicray.epoxyModels.overline
import java.util.concurrent.CopyOnWriteArrayList

class MainScreenController : AsyncEpoxyController() {

    private var featuredComics: CopyOnWriteArrayList<FeaturedComic> = CopyOnWriteArrayList()
    private var popularComics: CopyOnWriteArrayList<ComicDetails> = CopyOnWriteArrayList()
    private var actionComics: CopyOnWriteArrayList<ComicDetails> = CopyOnWriteArrayList()

    fun setPopularComics(data: List<ComicDetails>) {
        popularComics.clear()
        popularComics.addAll(data)
        requestModelBuild()
    }

    fun setActionComics(data: List<ComicDetails>) {
        actionComics.clear()
        actionComics.addAll(data)
        requestModelBuild()
    }

    fun setFeaturedComics(data: List<FeaturedComic>) {
        featuredComics.clear()
        featuredComics.addAll(data)
        requestModelBuild()
    }


    override fun buildModels() {
        Carousel.setDefaultGlobalSnapHelperFactory(null)
        if (featuredComics.isNotEmpty()) {
            overline {
                id("featuredComic")
                value("Featured Comics")
            }

            carousel {
                id("id-feat-comics")
                models(this@MainScreenController.featuredComics.map { item ->
                    CardModel_().id("featured-comics-id:" + item.id + item.title)
                        .title(item.title)
                        .urlToImage(item.imageUrl)
                        .listener { _ ->

                        }
                })
            }
        }

        if (popularComics.isNotEmpty()) {
            overline {
                id("popularComics")
                value("Popular Comics")
            }

            carousel {
                id("Popular_Carousel")
                models(this@MainScreenController.popularComics.map {
                    CardModel_().id("id" + it.title)
                        .title(it.title)
                        .urlToImage(it.imageUrl)
                        .listener { _ ->

                        }
                })
            }

        }

        if (actionComics.isNotEmpty()) {
            overline {
                id("actionComics")
                value("Action Comics")
            }

            carousel {
                id("Action_Carousel")
                models(this@MainScreenController.actionComics.map {
                    CardModel_().id("id" + it.title)
                        .title(it.title)
                        .urlToImage(it.imageUrl)
                        .listener { _ ->

                        }
                })
            }
        }

    }
}