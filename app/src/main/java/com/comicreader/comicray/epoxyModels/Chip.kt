package com.comicreader.comicray.epoxyModels

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.ItemChipBinding

@EpoxyModelClass(layout = R.layout.item_chip)
abstract class Chip : EpoxyModelWithHolder<Chip.ViewHolder>() {

    @EpoxyAttribute
    open var title: String = ""

    @EpoxyAttribute(value = [EpoxyAttribute.Option.IgnoreRequireHashCode])
    open var listener: () -> Unit = {}

    override fun bind(holder: ViewHolder) {
        holder.binding.apply {
            root.text = title
            root.setOnClickListener { listener.invoke() }
        }
    }

    inner class ViewHolder : EpoxyHolder() {
        lateinit var binding: ItemChipBinding
            private set
        override fun bindView(itemView: View) {
            binding = ItemChipBinding.bind(itemView)
        }
    }
}