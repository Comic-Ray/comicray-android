package com.comicreader.comicray.api

import com.comicreader.comicray.data.models.completedComic.CompletedComic
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.comicreader.comicray.data.models.custom.GenreResponse
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.models.ongoingComic.OngoingComic
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ComicApi {

    @Headers("Accept: application/json")
    @GET("comic/Home/featured")
    suspend fun getFeaturedComics(): List<FeaturedComic>

    @GET("comic/Home/ongoing")
    fun getOngoingComics(): Response<List<OngoingComic>>

    @GET("comic/Home/completed")
    fun getCompletedComics(): Response<List<CompletedComic>>

    @Headers("Accept: application/json")
    @GET("comic/Genre")
    suspend fun getGenreComics(
        @Query("tag") tag : String,
        @Query("page") page : Int
    ) : GenreResponse




}