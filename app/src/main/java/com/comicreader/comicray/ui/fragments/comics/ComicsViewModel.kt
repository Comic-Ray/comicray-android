package com.comicreader.comicray.ui.fragments.comics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comicreader.comicray.data.models.DataItem
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.data.models.custom.toDataItem
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.repositories.ComicRepository
import com.comicreader.comicray.utils.ComicGenres
import com.comicreader.comicray.utils.Event
import com.comicreader.comicray.utils.Refresh
import com.comicreader.comicray.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableMap
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@HiltViewModel
class ComicsViewModel @Inject constructor(
    private val comicRepository: ComicRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

//    private val comicListChannel = Channel<Map<ComicGenres, CustomData>>()
//    val comicList = comicListChannel.receiveAsFlow()
//
//    val comicListMutable: MutableStateFlow<Map<ComicGenres, CustomData>> =
//        MutableStateFlow(emptyMap())

    private val _isBusy = Channel<Resource<Any>>()
    val isBusy = _isBusy.receiveAsFlow()


    private val refreshTriggerChannel = MutableSharedFlow<Refresh>()
     val refreshTrigger get() = refreshTriggerChannel.asSharedFlow()

    private val list = HashMap<ComicGenres, CustomData>()

    init {
        viewModelScope.launch {
            _isBusy.send(Resource.Loading())
        }
    }

    // Working Old Impl
//    fun getAllComics(): Flow<Map<ComicGenres, CustomData>> = channelFlow {
//        refreshTrigger.flatMapLatest { trigger ->
//
//            if (trigger == Refresh.Force) {
//                _isBusy.send(Resource.Loading())
//            }
//            Log.d("TAGG", "getAllComics: $trigger")
//
//            val featuredFlow = comicRepository.getFeaturedComics(
//                forceRefresh = trigger == Refresh.Force,
//                fetchSuccess = {},
//                onFetchFailed = { t ->
//                    viewModelScope.launch {
//                        eventChannel.send(Event.ShowErrorMessage(t))
//                    }
//                }
//            )
//
//            val actionFlow = comicRepository.getGenreComics(
//                forceRefresh = trigger == Refresh.Force,
//                tag = "action-comic",
//                type = "Comic",
//                fetchSuccess = {},
//                onFetchFailed = { throwable ->
//                    viewModelScope.launch {
//                        eventChannel.send(Event.ShowErrorMessage(throwable))
//                    }
//                }
//            )
//
//            val popularFlow = comicRepository.getGenreComics(
//                forceRefresh = trigger == Refresh.Force,
//                tag = "popular-comic",
//                type = "Comic",
//                fetchSuccess = {},
//                onFetchFailed = { throwable ->
//                    viewModelScope.launch {
//                        eventChannel.send(Event.ShowErrorMessage(throwable))
//                    }
//                }
//            )
//
//            combine(
//                featuredFlow,
//                actionFlow,
//                popularFlow
//            ) { featuredFlowResponse, actionFlowResponse, popularFlowResponse ->
//                val hashMap = HashMap<ComicGenres, CustomData>()
//                if (actionFlowResponse is Resource.Success) {
//                    val actionData = actionFlowResponse.data?.data
//                    if (actionData?.isNotEmpty() == true) {
//                        hashMap[ComicGenres.Action] = CustomData("Action Comics", actionData)
//                    }
//                }
//
//                if (featuredFlowResponse is Resource.Success) {
//                    val featuredData = featuredFlowResponse.data
//                    if (featuredData?.isNotEmpty() == true) {
////                        hashMap[ComicGenres.Featured] =
////                            CustomData("Featured Comics", convertToCommonData(featuredData))
//                    }
//                }
//
//                if (popularFlowResponse is Resource.Success) {
//                    val popularComicsData = popularFlowResponse.data?.data
//                    if (popularComicsData?.isNotEmpty() == true) {
//                        hashMap[ComicGenres.Popular] =
//                            CustomData("Popular Comics", popularComicsData)
//                    }
//                }
//
//                send(hashMap.toImmutableMap())
//                return@combine hashMap.toImmutableMap()
//            }
//        }.collect { map ->
//            map.forEach { (key, value) ->
//                list[key] = value
//                comicListMutable.value = list.toImmutableMap()
//                _isBusy.send(Resource.Success(list))
//                send(comicListMutable.value)
//            }
//        }
//    }

    //New impl

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
        comicRepository.getGenreComics(
            forceRefresh = trigger == Refresh.Force,
            tag = "action-comic",
            type = "Comic",
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
        comicRepository.getGenreComics(
            forceRefresh = trigger == Refresh.Force,
            tag = "popular-comic",
            type = "Comic",
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
    private fun convertToCommonData(data: List<ComicDetails>) : List<DataItem> {
        return data.map { it.toDataItem() }
    }

    private fun convertToCommonData(featured: List<FeaturedComic>?): List<DataItem> {
        return featured?.map {
            ComicDetails(
                title = it.title,
                url = it.url,
                imageUrl = it.imageUrl
            ).toDataItem()
        } ?: emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        list.clear()
    }
}

