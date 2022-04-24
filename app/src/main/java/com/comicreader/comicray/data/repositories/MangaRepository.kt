package com.comicreader.comicray.data.repositories

import androidx.room.withTransaction
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.api.MangaApi
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.custom.GenreResponse
import com.comicreader.comicray.db.ComicDatabase
import com.comicreader.comicray.utils.Resource
import com.comicreader.comicray.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MangaRepository @Inject constructor(
    private val comicApi: ComicApi,
    private val mangaApi: MangaApi,
    private val comicDb: ComicDatabase
) {

    //in manga api tag = categoryname, categoryNo = CategoryNumber
    fun getGenreComics(
        forceRefresh : Boolean,
        categoryNo: String,
        tag : String,
        type: String,
        fetchSuccess: () -> Unit,
        onFetchFailed : (Throwable) -> Unit
    ) : Flow<Resource<GenreResponse>> = networkBoundResource(
        query = {
            val query = comicDb.homeComicDao().getGenreComicsResponse(tag, type)
            query
        },
        fetch = {
            val data = comicApi.getGenreManga(category = categoryNo)
            data
        },
        saveFetchResult = {
            val c = GenreResponse(tag = tag,data = it.data,page = it.page,totalPages = it.totalPages, Comictype = type)
            comicDb.withTransaction {
                comicDb.homeComicDao().deleteGenreComicsResponse(tag, type)
                comicDb.homeComicDao().insertGenreComicsResponse(c)
            }
        },
        shouldFetch = {genreResponse ->
            if (forceRefresh){
                true
            }else {
                if (genreResponse!=null) {
                    val data = genreResponse.data.isEmpty()
                    data
                }else{
                    true
                }
            }
        },
        onFetchSuccess = fetchSuccess,
        onFetchFailed = {
            if (it !is HttpException && it !is IOException) {
                throw it
            }
            onFetchFailed(it)
        }
    )

    fun getGenreList(): Flow<Resource<List<Genre.Manga>>> = networkBoundResource(
        query = { comicDb.homeComicDao().getMangaGenreList() },
        fetch = { mangaApi.getMangaGenreList() },
        saveFetchResult = { comicDb.homeComicDao().saveMangaGenreList(it) },
        shouldFetch = { it.firstOrNull()?.isExpired() ?: true },
        onFetchFailed = {},
        onFetchSuccess = {}
    )
    
}