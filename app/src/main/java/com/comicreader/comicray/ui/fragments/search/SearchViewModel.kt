package com.comicreader.comicray.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.api.MangaApi
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.custom.SearchCommon
import com.comicreader.comicray.data.models.custom.toSearchCommon
import com.comicreader.comicray.data.repositories.ComicRepository
import com.comicreader.comicray.data.repositories.MangaRepository
import com.comicreader.comicray.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val comicRepository: ComicRepository,
    private val mangaRepository: MangaRepository,
    private val mangaApi: MangaApi,
    private val comicApi: ComicApi,
) : ViewModel() {

    private val searchQueryStateFlow = MutableStateFlow("")

    val searchData: LiveData<List<SearchCommon>?> = searchQueryStateFlow
        .debounce(1000L)
        .flatMapLatest { query ->
            val innerFlow = MutableStateFlow<List<SearchCommon>?>(emptyList())
            if (query.trim().isNotEmpty()) {
                innerFlow.emit(emptyList())
                val scope = CoroutineScope(coroutineContext)
                scope.launch {
                    val data = comicApi.getSearch(query, page = 1)
                    innerFlow.emit(innerFlow.value!! + data.toSearchCommon(query, BookType.Comic))
                }
                scope.launch {
                    val data = mangaApi.getSearch(query, page = 1)
                    innerFlow.emit(innerFlow.value!! + data.toSearchCommon(query, BookType.Manga))
                }
            } else {
                innerFlow.emit(null)
            }
            innerFlow
        }.asLiveData()

    fun getComicGenreList(): LiveData<Resource<List<Genre.Comic>>> = comicRepository.getGenreList().asLiveData()
    fun getMangaGenreList(): LiveData<Resource<List<Genre.Manga>>> = mangaRepository.getGenreList().asLiveData()

    fun setSearchQuery(query: String) {
        viewModelScope.launch {
            searchQueryStateFlow.emit(query)
        }
    }

    fun getCurrentQuery() : String = searchQueryStateFlow.value
}