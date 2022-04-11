package com.comicreader.comicray.controllers

import com.airbnb.epoxy.*
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.epoxyModels.CardModel_
import com.comicreader.comicray.epoxyModels.overline
import java.util.concurrent.CopyOnWriteArrayList

class MainScreenController : AsyncEpoxyController() {

    private var featuredComics: CopyOnWriteArrayList<FeaturedComic> = CopyOnWriteArrayList()
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
        featuredComics.addAll(data)
       /* val models = data.map {
            CardModel_().id("featured-comics-id:" + it.id)
                .title(it.title)
                .urlToImage(it.imageUrl)
                .listener { _ ->

                }
        }*/
//        featuredComics.addAll(models)
        requestModelBuild()
    }


    override fun buildModels() {
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
               // models(this@MainScreenController.featuredComics)

            }
        }

//        if (popularComics.isNotEmpty()) {
//            overline {
//                id("popularComics")
//                value("Popular Comics")
//            }
//
//            carousel {
//                id("Popular_Carousel")
//                models(this@MainScreenController.popularComics)
//                numViewsToShowOnScreen(3f)
//            }
//
//        }
//
//        if (actionComics.isNotEmpty()) {
//            overline {
//                id("actionComics")
//                value("Action Comics")
//            }
//
//            carousel {
//                id("Action_Carousel")
//                models(this@MainScreenController.actionComics)
//                numViewsToShowOnScreen(3f)
//            }
//        }

    }

    /** Add models to a CarouselModel_ by transforming a list of items into EpoxyModels.
     *
     * @param items The items to transform to models
     * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
     */
    private inline fun <T> CarouselModelBuilder.withModelsFrom(
        items: List<T>,
        modelBuilder: (T) -> EpoxyModel<*>
    ) : CarouselModelBuilder {
        return models(items.map { modelBuilder(it) })
    }
}