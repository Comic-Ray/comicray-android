package com.comicreader.comicray.ui.fragments.comics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.repositories.ComicRepository
import com.comicreader.comicray.utils.ComicGenres
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

    private val comicListChannel = Channel<Map<ComicGenres, CustomData>>()
    val comicList = comicListChannel.receiveAsFlow()

    val comicListMutable: MutableStateFlow<Map<ComicGenres, CustomData>> =
        MutableStateFlow(emptyMap())

    private val _isBusy = Channel<Resource<Any>>()
    val isBusy = _isBusy.receiveAsFlow()


    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger get() = refreshTriggerChannel.receiveAsFlow()

    private val list = HashMap<ComicGenres, CustomData>()

    init {
//        viewModelScope.launch {
//            _isBusy.send(Resource.Loading())
//        }
        Log.d("TAGG", "onStart: Viewmodel init")
    }

// Working Old Impl
    fun getAllComics(): Flow<Map<ComicGenres, CustomData>> = channelFlow {
        refreshTrigger.flatMapLatest { trigger ->

            if (trigger == Refresh.Force) {
                _isBusy.send(Resource.Loading())
            }

            val featuredFlow = comicRepository.getFeaturedComics(
                forceRefresh = trigger == Refresh.Force,
                fetchSuccess = {},
                onFetchFailed = { t ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(t))
                    }
                }
            )

            val actionFlow = comicRepository.getGenreComics(
                forceRefresh = trigger == Refresh.Force,
                tag = "action-comic",
                fetchSuccess = {},
                onFetchFailed = { throwable ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(throwable))
                    }
                }
            )

            val popularFlow = comicRepository.getGenreComics(
                forceRefresh = trigger == Refresh.Force,
                tag = "popular-comic",
                fetchSuccess = {},
                onFetchFailed = { throwable ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(throwable))
                    }
                }
            )

            combine(
                featuredFlow,
                actionFlow,
                popularFlow
            ) { featuredFlowResponse, actionFlowResponse, popularFlowResponse ->
                val hashMap = HashMap<ComicGenres, CustomData>()
                if (actionFlowResponse is Resource.Success) {
                    val actionData = actionFlowResponse.data?.data
                    if (actionData?.isNotEmpty() == true) {
                        hashMap[ComicGenres.Action] = CustomData("Action Comics", actionData)
                    }
                }

                if (featuredFlowResponse is Resource.Success) {
                    val featuredData = featuredFlowResponse.data
                    if (featuredData?.isNotEmpty() == true) {
                        hashMap[ComicGenres.Featured] =
                            CustomData("Featured Comics", convertToCommonData(featuredData))
                    }
                }

                if (popularFlowResponse is Resource.Success) {
                    val popularComicsData = popularFlowResponse.data?.data
                    if (popularComicsData?.isNotEmpty() == true) {
                        hashMap[ComicGenres.Popular] =
                            CustomData("Popular Comics", popularComicsData)
                    }
                }

                send(hashMap.toImmutableMap())
                return@combine hashMap.toImmutableMap()
            }
        }.collect { map ->
            map.forEach { (key, value) ->
                list[key] = value
                comicListMutable.value = list.toImmutableMap()
                _isBusy.send(Resource.Success(list))
                send(comicListMutable.value)
            }
        }
    }

    //New impl

    fun getFeaturedComics() = channelFlow {
        refreshTrigger.flatMapLatest { trigger ->

            comicRepository.getFeaturedComics(
                forceRefresh = trigger == Refresh.Force,
                fetchSuccess = {},
                onFetchFailed = { t ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(t))
                    }
                }
            )
        }.collect {
            if (it is Resource.Success) {
                send(it)
            }
        }
    }

    fun getActionComics() = channelFlow {
        refreshTrigger.flatMapLatest { trigger ->

            comicRepository.getGenreComics(
                forceRefresh = trigger == Refresh.Force,
                tag = "action-comic",
                fetchSuccess = {},
                onFetchFailed = { throwable ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(throwable))
                    }
                }
            )

        }.collect {
            if (it is Resource.Success)
                send(it)
        }
    }

    fun getPopularComics()= channelFlow {
        refreshTrigger.flatMapLatest { trigger ->

            comicRepository.getGenreComics(
                forceRefresh = trigger == Refresh.Force,
                tag = "popular-comic",
                fetchSuccess = {},
                onFetchFailed = { throwable ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(throwable))
                    }
                }
            )

        }.collect {
            if (it is Resource.Success)
                send(it)
        }
    }


    fun onManuelRefresh() {
        viewModelScope.launch {
//            if (_isBusy.receive() !is Resource.Loading) {
            refreshTriggerChannel.send(Refresh.Force)
//            }
        }
    }

    fun onStart() {
        viewModelScope.launch {
//            if (_isBusy.receive() is Resource.Loading) {
            refreshTriggerChannel.send(Refresh.Normal)
//            }
            Log.d("TAGG", "onStart: Viewmodel OnStart")
        }
    }

    private fun convertToCommonData(featured: List<FeaturedComic>?): List<ComicDetails> {
        return featured?.map {
            ComicDetails(
                title = it.title,
                url = it.url,
                imageUrl = it.imageUrl
            )
        } ?: emptyList()
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }

    override fun onCleared() {
        super.onCleared()
        list.clear()
    }
}


// OLD IMPL
//private val actionComics = refreshTrigger.flatMapLatest {
//    comicRepository.getGenreComics(
//        forceRefresh = true,
//        tag = "action-comic",
//        fetchSuccess = {},
//        onFetchFailed = { throwable ->
//            viewModelScope.launch {
//                eventChannel.send(Event.ShowErrorMessage(throwable))
//            }
//        }
//    )
//}.stateIn(viewModelScope, SharingStarted.Lazily, null)
//
//val featuredComics = refreshTrigger.flatMapLatest {
//    comicRepository.getFeaturedComics(
//        forceRefresh = it == Refresh.Force,
//        fetchSuccess = {},
//        onFetchFailed = { throwable ->
//            viewModelScope.launch {
//                eventChannel.send(Event.ShowErrorMessage(throwable))
//            }
//        }
//    )
//}.stateIn(viewModelScope, SharingStarted.Lazily, null)