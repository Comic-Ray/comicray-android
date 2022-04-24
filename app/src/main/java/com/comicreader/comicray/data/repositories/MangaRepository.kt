package com.comicreader.comicray.data.repositories

import com.comicreader.comicray.api.MangaApi
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.custom.GenreDetail
import com.comicreader.comicray.data.models.custom.MangaGenreResponse
import com.comicreader.comicray.data.models.custom.toGenreDetail
import com.comicreader.comicray.data.models.mangaDetails.MangaDetailsResponse
import com.comicreader.comicray.db.ComicDatabase
import com.comicreader.comicray.utils.Resource
import com.comicreader.comicray.utils.networkBoundResource
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MangaRepository @Inject constructor(
    private val mangaApi: MangaApi,
    private val comicDb: ComicDatabase
) {

    //in manga api tag = categoryname, categoryNo = CategoryNumber
    fun getGenre(
        forceRefresh : Boolean,
        categoryNo: String,
        tag : String,
        fetchSuccess: () -> Unit,
        onFetchFailed : (Throwable) -> Unit
    ) : Flow<Resource<MangaGenreResponse>> = networkBoundResource(
        query = {
            val query = comicDb.homeComicDao().getGenreComicsResponse(tag, BookType.Manga).map { it.getMangaGenreResponse() }
            query
        },
        fetch = {
            val data = mangaApi.getMangaGenre(category = categoryNo, page = 1)
            data
        },
        saveFetchResult = { comicDb.homeComicDao().insertGenreDetailResponse(it.toGenreDetail(tag)) },
        shouldFetch = { genreResponse ->
            if (forceRefresh){
                true
            }else {
                genreResponse.data.isEmpty()
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

    fun getMangaDetails(url: String): Flow<Resource<MangaDetailsResponse>> = flow{
        try {
            val data = mangaApi.getMangaDetails(url)
            emit(Resource.Success(data))
        }catch (e: Exception){
            emit(Resource.Error(e))
        }
    }
    
}