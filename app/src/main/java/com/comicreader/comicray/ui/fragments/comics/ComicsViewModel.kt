package com.comicreader.comicray.ui.fragments.comics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comicreader.comicray.data.models.DataItem
import com.comicreader.comicray.data.models.custom.ComicDetail
import com.comicreader.comicray.data.models.custom.toDataItem
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.repositories.ComicRepository
import com.comicreader.comicray.utils.Event
import com.comicreader.comicray.utils.Refresh
import com.comicreader.comicray.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComicsViewModel @Inject constructor(
    private val comicRepository: ComicRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val _isBusy = Channel<Resource<Any>>()
    val isBusy = _isBusy.receiveAsFlow()


    private val refreshTriggerChannel = MutableSharedFlow<Refresh>()
     val refreshTrigger get() = refreshTriggerChannel.asSharedFlow()


    init {
        viewModelScope.launch {
            _isBusy.send(Resource.Loading())
        }
    }

    fun getFeaturedComics(): Flow<List<DataItem>> = refreshTrigger.flatMapLatest { trigger ->
        comicRepository.getFeaturedComics(
            forceRefresh = trigger == Refresh.Force,
            fetchSuccess = {},
            onFetchFailed = { t ->
                viewModelScope.launch {
                    eventChannel.send(Event.ShowErrorMessage(t))
                }
            }
        )
    }.filter { it is Resource.Success }.mapNotNull { convertToCommonData(it.data) }

    fun getActionComics(): Flow<List<DataItem>> = refreshTrigger.flatMapLatest { trigger ->
        comicRepository.getGenre(
            forceRefresh = trigger == Refresh.Force,
            tag = "action-comic",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }
        .mapNotNull { convertToCommonData(it.data?.data ?: emptyList()) }

    fun getPopularComics(): Flow<List<DataItem>> = refreshTrigger.flatMapLatest { trigger ->
        comicRepository.getGenre(
            forceRefresh = trigger == Refresh.Force,
            tag = "popular-comic",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }
        .mapNotNull { convertToCommonData(it.data?.data ?: emptyList()) }

    fun getFantasyComics(): Flow<List<DataItem>> = refreshTrigger.flatMapLatest { trigger ->
        comicRepository.getGenre(
            forceRefresh = trigger == Refresh.Force,
            tag = "fantasy-comic",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }
        .mapNotNull { convertToCommonData(it.data?.data ?: emptyList()) }

    fun onManualRefresh() {
        viewModelScope.launch {
            refreshTriggerChannel.emit(Refresh.Force)
        }
    }

    fun onStart() {
        viewModelScope.launch {
            refreshTriggerChannel.emit(Refresh.Normal)
        }
    }

    @JvmName(name = "convertToCommonData1")
    private fun convertToCommonData(data: List<ComicDetail>) : List<DataItem> {
        return data.map { it.toDataItem() }
    }

    private fun convertToCommonData(featured: List<FeaturedComic>?): List<DataItem> {
        return featured?.map {
            ComicDetail(
                title = it.title,
                url = it.url,
                imageUrl = it.imageUrl
            ).toDataItem()
        } ?: emptyList()
    }
}

