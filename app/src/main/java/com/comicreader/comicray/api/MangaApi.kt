package com.comicreader.comicray.api

import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.custom.MangaGenreResponse
import retrofit2.http.GET

interface MangaApi {
    @GET("v1/manga/Genre/list")
    suspend fun getMangaGenreList(): List<Genre.Manga>

    @GET("v1/manga/Genre")
    suspend fun getMangaGenre(category: String, page: Int) : MangaGenreResponse
}