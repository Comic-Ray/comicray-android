package com.comicreader.comicray.api

import com.comicreader.comicray.data.models.completedComic.CompletedComic
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComicResponse
import com.comicreader.comicray.data.models.ongoingComic.OngoingComic
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers


interface ComicApi {

    @Headers("Accept: application/json")
    @GET("comic/Home/featured")
    suspend fun getFeaturedComics(): Response<List<FeaturedComic>>

    @Headers("Accept: application/json")
    @GET("comic/Home/featured")
    suspend fun getFeaturedComicsTest(): List<FeaturedComic>

    @GET("comic/Home/ongoing")
    fun getOngoingComics(): Response<List<OngoingComic>>

    @GET("comic/Home/completed")
    fun getCompletedComics(): Response<List<CompletedComic>>


}