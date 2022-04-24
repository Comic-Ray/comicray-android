package com.comicreader.comicray.ui.fragments.search.paging

import androidx.paging.PageKeyedDataSource
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.data.models.custom.ComicDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private class SearchComicDataSource(
    private val scope: CoroutineScope,
    private val query: String,
    private val comicApi: ComicApi,
) : PageKeyedDataSource<Int, ComicDetail>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ComicDetail>) {
        scope.launch {
            try {
                val result = comicApi.getSearch(query = query, page = 1)
                callback.onResult(result.data, null, 2)
            } catch (e : Exception) {

            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ComicDetail>) {
        TODO("Not yet implemented")
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ComicDetail>) {
        // not needed
    }
}