package com.comicreader.comicray.epoxyModels

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.ItemSpacerBinding
import extensions.toPx

@EpoxyModelClass(layout = R.layout.item_spacer)
abstract class Spacer : EpoxyModelWithHolder<Spacer.ViewHolder>() {

    @EpoxyAttribute
    open var heightDp: Float = 0f

    override fun bind(holder: ViewHolder) {
        holder.binding.apply {
            root.updateLayoutParams<ViewGroup.LayoutParams> {
                height = root.context.toPx(heightDp).toInt()
            }
        }
    }

    inner class ViewHolder : EpoxyHolder() {
        lateinit var binding: ItemSpacerBinding
            private set

        override fun bindView(itemView: View) {
            binding = ItemSpacerBinding.bind(itemView)
        }
    }
}