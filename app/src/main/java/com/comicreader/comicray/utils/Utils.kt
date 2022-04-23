package com.comicreader.comicray.utils

enum class Refresh {
    Normal,
    Force
}


sealed class Event {
    data class ShowErrorMessage(val error: Throwable) : Event()
}

enum class ComicGenres {
    Featured,
    Action,
    Popular
}

