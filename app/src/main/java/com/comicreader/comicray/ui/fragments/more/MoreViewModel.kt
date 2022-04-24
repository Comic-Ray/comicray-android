package com.comicreader.comicray.ui.fragments.more

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
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.custom.toDataItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val comicApi: ComicApi,
    private val mangaApi: MangaApi,
) : ViewModel() {

    fun getGenreData(genreTag: String, type: BookType): MoreListing {
        return getData(type = type, genreTag = genreTag)
    }

    fun getSearchData(query: String, type: BookType) : MoreListing {
        return getData(type = type, query = query)
    }

    private fun getData(type: BookType, genreTag: String? = null, query: String? = null): MoreListing {
        val dataSourceFactory = MoreDataSourceFactory(
            scope = viewModelScope,
            comicApi = comicApi,
            mangaApi = mangaApi,
            genreTag = genreTag,
            query = query,
            type = type
        )

        val pageConfig = PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .build()

        return MoreListing(
            data = dataSourceFactory.toLiveData(pageConfig),
            onRefresh = { dataSourceFactory.refresh() },
            initialLoadState = dataSourceFactory.initialLoadState.asLiveData(),
            loadMoreState = dataSourceFactory.loadMoreState.asLiveData()
        )
    }
}

sealed class MoreLoadState {
    object None : MoreLoadState()
    object Loading : MoreLoadState()
    object Success : MoreLoadState()
    data class Error(val ex: Exception) : MoreLoadState()
}

data class MoreListing(
    val data: LiveData<PagedList<DataItem>>,
    val onRefresh: () -> Unit,
    val initialLoadState: LiveData<MoreLoadState>,
    val loadMoreState: LiveData<MoreLoadState>,
)

private class MoreDataSourceFactory(
    private val scope: CoroutineScope,
    private val comicApi: ComicApi,
    private val mangaApi: MangaApi,
    private val type: BookType,
    private val genreTag: String?,
    private val query: String?,
) : DataSource.Factory<Int, DataItem>() {

    val initialLoadState: MutableStateFlow<MoreLoadState> = MutableStateFlow(MoreLoadState.None)
    val loadMoreState: MutableStateFlow<MoreLoadState> = MutableStateFlow(MoreLoadState.None)

    private var dataSource: MoreDataSource? = null

    override fun create(): DataSource<Int, DataItem> {
        val  dataSource = MoreDataSource(
            scope = scope,
            initialLoadState = initialLoadState,
            loadMoreState = loadMoreState,
            comicApi = comicApi,
            mangaApi = mangaApi,
            type = type,
            genreTag = genreTag,
            query = query,
        )
        return dataSource.also { this.dataSource = dataSource }
    }

    fun refresh() {
        dataSource?.invalidate()
    }
}

private class MoreDataSource(
    private val scope: CoroutineScope,
    private val initialLoadState: MutableStateFlow<MoreLoadState>,
    private val loadMoreState: MutableStateFlow<MoreLoadState>,
    private val comicApi: ComicApi,
    private val mangaApi: MangaApi,
    private val type: BookType,
    private val genreTag: String?,
    private val query: String?,
) : PageKeyedDataSource<Int, DataItem>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, DataItem>) {
        scope.launch {
            initialLoadState.emit(MoreLoadState.Loading)
            try {
                val data = when (type) {
                    BookType.Comic -> {
                        if (genreTag != null) {
                            comicApi.getGenreComics(genreTag, 1).data.map { it.toDataItem() }
                        } else if (query != null) {
                            comicApi.getSearch(query, 1).data.map { it.toDataItem() }
                        } else throw IllegalStateException("Unhandled paging exception")
                    }
                    BookType.Manga -> {
                        if (genreTag != null) {
                            mangaApi.getMangaGenre(genreTag, 1).data.map { it.toDataItem() }
                        } else if (query != null) {
                            mangaApi.getSearch(query, 1).data.map { it.toDataItem() }
                        } else throw IllegalStateException("Unhandled paging exception")
                    }
                }
                callback.onResult(data, null, 2)
                initialLoadState.emit(MoreLoadState.Success)
            } catch (e: Exception) {
                initialLoadState.emit(MoreLoadState.Error(e))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DataItem>) {
        scope.launch {
            loadMoreState.emit(MoreLoadState.Loading)
            try {
                var totalPages = 0
                val data = when (type) {
                    BookType.Comic -> {
                        if (genreTag != null) {
                            comicApi.getGenreComics(genreTag, params.key).run {
                                totalPages = this.totalPages
                                data.map { it.toDataItem() }
                            }
                        } else if (query != null) {
                            comicApi.getSearch(query, params.key).run {
                                totalPages = this.totalPages
                                data.map { it.toDataItem() }
                            }
                        } else throw IllegalStateException("Unhandled paging exception")
                    }
                    BookType.Manga -> {
                        if (genreTag != null) {
                            mangaApi.getMangaGenre(genreTag, params.key).run {
                                totalPages = this.totalPages
                                data.map { it.toDataItem() }
                            }
                        } else if (query != null) {
                            mangaApi.getSearch(query, params.key).run {
                                totalPages = this.totalPages
                                data.map { it.toDataItem() }
                            }
                        } else throw IllegalStateException("Unhandled paging exception")

                    }
                }
                val nextPage: Int? = if (params.key < totalPages) {
                    params.key + 1
                } else {
                    null
                }
                callback.onResult(data, nextPage)
                loadMoreState.emit(MoreLoadState.Success)
            } catch (e: Exception) {
                loadMoreState.emit(MoreLoadState.Error(e))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DataItem>) {
        // we don't need it
    }
}