package com.comicreader.comicray.ui.fragments.genre

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.api.MangaApi
import com.comicreader.comicray.data.models.DataItem
import com.comicreader.comicray.data.models.GenreType
import com.comicreader.comicray.data.models.custom.toDataItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(
    private val comicApi: ComicApi,
    private val mangaApi: MangaApi,
) : ViewModel() {

    fun getData(genreTag: String, type: GenreType): GenreListing {
        val dataSourceFactory = GenreDataSourceFactory(
            scope = viewModelScope,
            comicApi = comicApi,
            mangaApi = mangaApi,
            genreTag = genreTag,
            type = type
        )

        val pageConfig = PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .build()

        return GenreListing(
            data = dataSourceFactory.toLiveData(pageConfig),
            onRefresh = { dataSourceFactory.refresh() },
            initialLoadState = dataSourceFactory.initialLoadState.asLiveData(),
            loadMoreState = dataSourceFactory.loadMoreState.asLiveData()
        )
    }
}

sealed class GenreLoadState {
    object None : GenreLoadState()
    object Loading : GenreLoadState()
    object Success : GenreLoadState()
    data class Error(val ex: Exception) : GenreLoadState()
}

data class GenreListing(
    val data: LiveData<PagedList<DataItem>>,
    val onRefresh: () -> Unit,
    val initialLoadState: LiveData<GenreLoadState>,
    val loadMoreState: LiveData<GenreLoadState>,
)

private class GenreDataSourceFactory(
    private val scope: CoroutineScope,
    private val comicApi: ComicApi,
    private val mangaApi: MangaApi,
    private val type: GenreType,
    private val genreTag: String,
) : DataSource.Factory<Int, DataItem>() {

    val initialLoadState: MutableStateFlow<GenreLoadState> = MutableStateFlow(GenreLoadState.None)
    val loadMoreState: MutableStateFlow<GenreLoadState> = MutableStateFlow(GenreLoadState.None)

    private var dataSource: GenreDataSource? = null

    override fun create(): DataSource<Int, DataItem> {
        val  dataSource = GenreDataSource(
            scope = scope,
            initialLoadState = initialLoadState,
            loadMoreState = loadMoreState,
            comicApi = comicApi,
            mangaApi = mangaApi,
            type = type,
            genreTag = genreTag,
        )
        return dataSource.also { this.dataSource = dataSource }
    }

    fun refresh() {
        dataSource?.invalidate()
    }
}

private class GenreDataSource(
    private val scope: CoroutineScope,
    private val initialLoadState: MutableStateFlow<GenreLoadState>,
    private val loadMoreState: MutableStateFlow<GenreLoadState>,
    private val comicApi: ComicApi,
    private val mangaApi: MangaApi,
    private val type: GenreType,
    private val genreTag: String,
) : PageKeyedDataSource<Int, DataItem>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, DataItem>) {
        scope.launch {
            initialLoadState.emit(GenreLoadState.Loading)
            try {
                val data = when (type) {
                    GenreType.Comic -> comicApi.getGenreComics(genreTag, 1).data.map { it.toDataItem() }
                    GenreType.Manga -> mangaApi.getMangaGenre(genreTag, 1).data.map { it.toDataItem() }
                }
                callback.onResult(data, null, 2)
                initialLoadState.emit(GenreLoadState.Success)
            } catch (e: Exception) {
                initialLoadState.emit(GenreLoadState.Error(e))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DataItem>) {
        scope.launch {
            loadMoreState.emit(GenreLoadState.Loading)
            try {
                var totalPages = 0
                val data = when (type) {
                    GenreType.Comic -> comicApi.getGenreComics(genreTag, params.key).run {
                        totalPages = this.totalPages
                        data.map { it.toDataItem() }
                    }
                    GenreType.Manga -> mangaApi.getMangaGenre(genreTag, params.key).run {
                        totalPages = this.totalPages
                        data.map { it.toDataItem() }
                    }
                }
                val nextPage: Int? = if (params.key < totalPages) {
                    params.key + 1
                } else {
                    null
                }
                callback.onResult(data, nextPage)
                loadMoreState.emit(GenreLoadState.Success)
            } catch (e: Exception) {
                loadMoreState.emit(GenreLoadState.Error(e))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DataItem>) {
        // we don't need it
    }
}