package com.comicreader.comicray.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BookType : Parcelable { Comic, Manga } // provide migration if data changes