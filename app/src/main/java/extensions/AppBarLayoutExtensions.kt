package com.comicreader.comicray.extensions

import com.google.android.material.appbar.AppBarLayout

val AppBarLayout.isExpanded : Boolean get() = (height - bottom) == 0