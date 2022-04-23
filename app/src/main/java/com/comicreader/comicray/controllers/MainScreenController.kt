package com.comicreader.comicray.controllers

import com.airbnb.epoxy.*
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.epoxyModels.CardModel_
import com.comicreader.comicray.epoxyModels.overline
import com.comicreader.comicray.utils.ComicGenres
import java.util.concurrent.CopyOnWriteArrayList

class MainScreenController : AsyncEpoxyController() {

    private var featuredComics: CopyOnWriteArrayList<ComicDetails> = CopyOnWriteArrayList()
    private var popularComics: CopyOnWriteArrayList<ComicDetails> = CopyOnWriteArrayList()
    private var actionComics: CopyOnWriteArrayList<ComicDetails> = CopyOnWriteArrayList()
    private var comicType: String = ""

    private var trendingManga: CopyOnWriteArrayList<ComicDetails> = CopyOnWriteArrayList()
    private var comedyManga: CopyOnWriteArrayList<ComicDetails> = CopyOnWriteArrayList()
    private var adventureManga: CopyOnWriteArrayList<ComicDetails> = CopyOnWriteArrayList()
    private var dramaManga: CopyOnWriteArrayList<ComicDetails> = CopyOnWriteArrayList()


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
        featuredComics.addAll(data.map {
            ComicDetails(
                title = it.title,
                url = it.url,
                imageUrl = it.imageUrl
            )
        })
        requestModelBuild()
    }

    //old impl
    fun submitList(data: Map<ComicGenres, CustomData>) {
        featuredComics.clear()
        actionComics.clear()
        popularComics.clear()

        data[ComicGenres.Featured]?.let { featuredComics.addAll(it.comics) }
        data[ComicGenres.Action]?.let { actionComics.addAll(it.comics) }
        data[ComicGenres.Popular]?.let { popularComics.addAll(it.comics) }
        requestModelBuild()
    }

    fun submitType(comicType: String) {
        this.comicType = comicType
    }

    //manga funcs
    fun submitTrendingManga(data: List<ComicDetails>) {
        trendingManga.clear()
        trendingManga.addAll(data)
        requestModelBuild()
    }

    fun submitComedyManga(data: List<ComicDetails>) {
        comedyManga.clear()
        comedyManga.addAll(data)
        requestModelBuild()
    }

    fun submitAdventureManga(data: List<ComicDetails>) {
        adventureManga.clear()
        adventureManga.addAll(data)
        requestModelBuild()
    }

    fun submitDramaManga(data: List<ComicDetails>) {
        dramaManga.clear()
        dramaManga.addAll(data)
        requestModelBuild()
    }

    override fun buildModels() {
        Carousel.setDefaultGlobalSnapHelperFactory(null)
        if (this.comicType == Comics) {
            if (featuredComics.isNotEmpty()) {
                overline {
                    id("featuredComic")
                    value("Featured Comics")
                }

                carousel {
                    id("id-feat-comics")
                    models(this@MainScreenController.featuredComics.map { item ->
                        CardModel_().id("featured-comics-id:" + item.title)
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

        } else {
            if (!trendingManga.isNullOrEmpty()) {
                overline {
                    id("trendingManga")
                    value("Trending")
                }

                carousel {
                    id("trending_carousel")
                    models(this@MainScreenController.trendingManga.map { item ->
                        CardModel_().id("trending-comic-id:" + item.title)
                            .title(item.title)
                            .urlToImage(item.imageUrl)
                            .listener { _ ->

                            }
                    })
                }
            }

            if (!comedyManga.isNullOrEmpty()){
                overline {
                    id("comedyManga")
                    value("Comedy")
                }

                carousel {
                    id("comedy_carousel")
                    models(this@MainScreenController.comedyManga.map { item ->
                        CardModel_().id("comedy-id:" + item.title)
                            .title(item.title)
                            .urlToImage(item.imageUrl)
                            .listener { _ ->

                            }
                    })
                }
            }

            if (!adventureManga.isNullOrEmpty()){
                overline {
                    id("adventureManga")
                    value("Adventure")
                }

                carousel {
                    id("adventure_carousel")
                    models(this@MainScreenController.adventureManga.map { item ->
                        CardModel_().id("adventure-id:" + item.title)
                            .title(item.title)
                            .urlToImage(item.imageUrl)
                            .listener { _ ->

                            }
                    })
                }
            }

            if (!dramaManga.isNullOrEmpty()){
                overline {
                    id("actionManga")
                    value("Drama")
                }

                carousel {
                    id("action-carousel")
                    models(this@MainScreenController.dramaManga.map { item ->
                        CardModel_().id("drama-id:" + item.title)
                            .title(item.title)
                            .urlToImage(item.imageUrl)
                            .listener { _ ->

                            }
                    })
                }
            }

        }//end of else
    }
}