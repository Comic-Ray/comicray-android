package com.comicreader.comicray.epoxyModels

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.ItemChipBinding
import com.comicreader.comicray.extensions.toPx

@EpoxyModelClass(layout = R.layout.item_chip)
abstract class Chip : EpoxyModelWithHolder<Chip.ViewHolder>() {

    @EpoxyAttribute
    open var title: String = ""

    @EpoxyAttribute
    open var marginHorizontalDp: Float = 15f

    @EpoxyAttribute(value = [EpoxyAttribute.Option.IgnoreRequireHashCode])
    open var listener: () -> Unit = {}

    override fun bind(holder: ViewHolder) {
        holder.binding.apply {
            root.text = title
            root.setOnClickListener { listener.invoke() }
            val px = root.context.toPx(marginHorizontalDp).toInt()
            root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(left = px, right = px)
            }
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