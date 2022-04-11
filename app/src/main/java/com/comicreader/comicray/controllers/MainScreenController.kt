package com.comicreader.comicray.controllers

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.carousel
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.epoxyModels.CardModel_
import com.comicreader.comicray.epoxyModels.overline

class MainScreenController : AsyncEpoxyController() {

    private var featuredComics: MutableList<CardModel_> = mutableListOf()
    private var popularComics: MutableList<CardModel_> = mutableListOf()
    private var actionComics: MutableList<CardModel_> = mutableListOf()


    fun setPopularComics(data: List<ComicDetails>) {
        popularComics.clear()
        val models = data.map {
            CardModel_().id("id" + it.title)
                .title(it.title)
                .urlToImage(it.imageUrl)
                .listener { _ ->

                }
        }

        popularComics.addAll(models)
        requestModelBuild()
    }

    fun setActionComics(data: List<ComicDetails>) {
        actionComics.clear()
        val models = data.map {
            CardModel_().id("id" + it.title)
                .title(it.title)
                .urlToImage(it.imageUrl)
                .listener { _ ->

                }
        }
        actionComics.addAll(models)
        requestModelBuild()
    }

    fun setFeaturedComics(data: List<FeaturedComic>) {
        featuredComics.clear()
        val models = data.map {
            CardModel_().id("id" + it.id)
                .title(it.title)
                .urlToImage(it.imageUrl)
                .listener { _ ->

                }
        }
        featuredComics.addAll(models)
        requestModelBuild()
    }


    override fun buildModels() {
        if (featuredComics.isNotEmpty()) {
            overline {
                id("featuredComic")
                value("Featured Comics")
            }

            carousel {
                id("Feature_Carousel")
                models(this@MainScreenController.featuredComics)
            }
        }

        if (popularComics.isNotEmpty()) {
            overline {
                id("popularComics")
                value("Popular Comics")
            }

            carousel {
                id("Popular_Carousel")
                models(this@MainScreenController.popularComics)
                numViewsToShowOnScreen(3f)
            }

        }

        if (actionComics.isNotEmpty()) {
            overline {
                id("actionComics")
                value("Action Comics")
            }

            carousel {
                id("Action_Carousel")
                models(this@MainScreenController.actionComics)
                numViewsToShowOnScreen(3f)
            }
        }

    }
}