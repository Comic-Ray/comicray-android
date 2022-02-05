package com.comicreader.comicray.ui.fragments.read.epoxy

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.ItemFragmentReadBinding
import com.comicreader.comicray.utils.load

@EpoxyModelClass(layout = R.layout.item_fragment_read)
abstract class ReadImageViewModel : EpoxyModelWithHolder<ReadImageViewModel.ReadHolder>() {

    @field:EpoxyAttribute
    open lateinit var imageUrl: String

    override fun bind(holder: ReadHolder) : Unit = with(holder.binding) {
        imageView.load(imageUrl)
    }

    inner class ReadHolder : EpoxyHolder() {
        lateinit var binding: ItemFragmentReadBinding
            private set
        override fun bindView(itemView: View) {
            binding = ItemFragmentReadBinding.bind(itemView)
        }
    }

}