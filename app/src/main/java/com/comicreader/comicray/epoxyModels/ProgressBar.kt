package com.comicreader.comicray.epoxyModels

import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R

@EpoxyModelClass(layout = R.layout.item_progress_bar)
abstract class ProgressBar : EpoxyModelWithHolder<ProgressBar.ViewHolder>() {
    inner class ViewHolder : EpoxyHolder() {
        override fun bindView(itemView: View) { /* no-op */ }
    }
}