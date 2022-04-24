package extensions

import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.collapse() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}