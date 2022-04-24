package com.comicreader.comicray.epoxyModels

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.ItemHeaderBinding
import com.comicreader.comicray.extensions.toPx

@EpoxyModelClass(layout = R.layout.item_header)
abstract class Header : EpoxyModelWithHolder<Header.ViewHolder>() {

    @EpoxyAttribute
    open var title: String? = null

    @EpoxyAttribute
    open var marginHorizontalDp: Float = 0f

    @EpoxyAttribute
    open var textSizeSp: Float = 0f

    override fun bind(holder: ViewHolder) {
        holder.binding.apply {
            root.text = title
            val marginPx = root.context.toPx(marginHorizontalDp).toInt()
            root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(right = marginPx, left = marginPx)
            }
            if (textSizeSp != 0f) {
                root.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
            }
        }
    }

    inner class ViewHolder : EpoxyHolder() {
        lateinit var binding: ItemHeaderBinding
            private set
        override fun bindView(itemView: View) {
            binding = ItemHeaderBinding.bind(itemView)
        }
    }
}