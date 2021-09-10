package com.comicreader.comicray.ui.fragments.comics

import android.util.Log
import androidx.lifecycle.*
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
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class ComicsViewModel @Inject constructor(
    private val comicRepository: ComicRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val comicListChannel = Channel<Map<ComicGenres,CustomData>>()
    val comicList = comicListChannel.receiveAsFlow()

     val comicListMutable: MutableStateFlow<Map<ComicGenres,CustomData>> = MutableStateFlow(emptyMap())


//    private val _collectionOfComics = MediatorLiveData<HashMap<Data, CustomData>>()
//    val collectionComics: LiveData<HashMap<Data, CustomData>> = _collectionOfComics

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger get() = refreshTriggerChannel.receiveAsFlow()

    private val list = HashMap<ComicGenres, CustomData>()

    val collectionComicsFlow: Flow<HashMap<ComicGenres, CustomData>> = flow {
        featuredComics.collect {
            if (it is Resource.Success) {
                list[ComicGenres.Featured] = CustomData(
                    "Featured Comics",
                    convertToCommonData(it.data)
                )
                emit(list)
            }
        }
    }

    // You don't need this
    fun getComics() = channelFlow<HashMap<ComicGenres,CustomData>> {
//        refreshTrigger.flatMapLatest {
//            comicRepository.getFeaturedComics(
//                forceRefresh = it == Refresh.Force,
//                fetchSuccess = {},
//                onFetchFailed = { t ->
//                    viewModelScope.launch {
//                        eventChannel.send(Event.showErrorMessage(t))
//                    }
//                }
//            )
//        }.collect {
//            if (it is Resource.Success) {
//                list[ComicGenres.Featured] = CustomData(
//                    "Featured Comics",
//                    convertToCommonData(it.data)
//                )
//                comicListMutable.value = list
//                comicListChannel.send(list)
//            }
//        }
    }


    fun getAllComics(): Flow<Map<ComicGenres, CustomData>> = flow {
        refreshTrigger.flatMapLatest { trigger ->
            val actionFlow = comicRepository.getGenreComics(
                forceRefresh = trigger == Refresh.Force,
                tag = "action-comic",
                fetchSuccess = {},
                onFetchFailed = { throwable ->
                    viewModelScope.launch {
                        eventChannel.send(Event.showErrorMessage(throwable))
                    }
                }
            )
            val featuredFlow = comicRepository.getFeaturedComics(
                forceRefresh = trigger == Refresh.Force,
                fetchSuccess = {},
                onFetchFailed = { t ->
                    viewModelScope.launch {
                        eventChannel.send(Event.showErrorMessage(t))
                    }
                }
            )
            featuredFlow.combine(actionFlow) { featuredFlowResponse, actionFlowResponse ->
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
                        hashMap[ComicGenres.Featured] = CustomData("Featured Comics", convertToCommonData(featuredData))
                    }
                }
                return@combine hashMap.toImmutableMap()
            }
        }.collect { map ->
            map.forEach { (key, value) ->
                list[key] = value
                comicListMutable.value = list.toImmutableMap()
                emit(comicListMutable.value)
            }
        }
    }

    val collectionF: Flow<HashMap<ComicGenres, CustomData>> = flow {
        actionComics.collect {
            if (it is Resource.Success) {
                if (it.data?.data?.isNotEmpty() == true) {
                    list[ComicGenres.Action] = CustomData(
                        "Action Comics",
                        it.data.data
                    )
                    emit(list)
                }
//                Log.d("TAGG", "${it.data?.data}")
            }
        }
    }

    private val actionComics = refreshTrigger.flatMapLatest {
        comicRepository.getGenreComics(
            forceRefresh = true,
            tag = "action-comic",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    eventChannel.send(Event.showErrorMessage(throwable))
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val featuredComics = refreshTrigger.flatMapLatest {
        comicRepository.getFeaturedComics(
            forceRefresh = it == Refresh.Force,
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    eventChannel.send(Event.showErrorMessage(throwable))
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
//        getFComics()
        viewModelScope.launch {
//            collectionF.collect {
//                comicListMutable.value = it
//            }
            getComics().collect { }
//            getActionComics().collect {  }
        }
    }

    fun onManuelRefresh() {
        viewModelScope.launch {
            if (featuredComics.value !is Resource.Loading) {
                refreshTriggerChannel.send(Refresh.Force)
            }
        }
    }

    fun onStart() {
        viewModelScope.launch {
            if (featuredComics.value !is Resource.Loading) {
                refreshTriggerChannel.send(Refresh.Normal)
            }
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
        data class showErrorMessage(val error: Throwable) : Event()
    }

    override fun onCleared() {
        super.onCleared()
        list.clear()
    }

}


//    fun getFComics() {
//        viewModelScope.launch {
//            featuredComics.collect {
//                if (it is Resource.Success) {
//                    if (!it.data.isNullOrEmpty()) {
//                        list[Data.Featured] = CustomData(
//                            "Featured Comics",
//                            convertToCommonData(it.data)
//                        )
//                        _collectionOfComics.postValue(list)
//                    }
//                }
//            }
//        }
//
//    }