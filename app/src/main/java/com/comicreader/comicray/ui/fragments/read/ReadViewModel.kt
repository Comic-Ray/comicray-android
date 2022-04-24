package com.comicreader.comicray.ui.fragments.read

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.api.MangaApi
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.ReadItem
import com.comicreader.comicray.data.models.toReadItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor(
    private val mangaApi: MangaApi,
    private val comicApi: ComicApi,
) : ViewModel() {
    fun getReadItem(url: String, type: BookType) : LiveData<ReadUiState> = flow {
        emit(ReadUiState.Loading)
        try {
            val data: ReadItem = when(type) {
                BookType.Comic -> comicApi.getReadData(url).toReadItem()
                BookType.Manga -> mangaApi.getReadData(url).toReadItem()
            }
            emit(ReadUiState.Success(data))
        } catch (e: Exception) {
            emit(ReadUiState.Error(e))
        }
    }.asLiveData()

    sealed class ReadUiState {
        object Loading : ReadUiState()
        data class Error(val error: Exception) : ReadUiState()
        data class Success(val data: ReadItem) : ReadUiState()
    }
}