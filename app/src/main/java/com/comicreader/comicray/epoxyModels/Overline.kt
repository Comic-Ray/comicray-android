package com.comicreader.comicray.epoxyModels

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.OverlineBinding
import com.comicreader.comicray.extensions.hide
import com.comicreader.comicray.extensions.show

@EpoxyModelClass(layout = R.layout.overline)
abstract class Overline : EpoxyModelWithHolder<Overline.ViewHolder>() {

    @EpoxyAttribute
    open var value: String? = null

    @EpoxyAttribute
    open var moreAvailable = true

    @EpoxyAttribute(EpoxyAttribute.Option.IgnoreRequireHashCode)
    open var listener: View.OnClickListener? = null

    override fun bind(holder: ViewHolder) {
        holder.binding.apply {
            titleText.text = value
            if (moreAvailable) {
                showMore.show()
                root.setOnClickListener(listener)
            } else {
                showMore.hide()
                root.setOnClickListener(null)
            }
        }
    }

    inner class ViewHolder : EpoxyHolder() {

        lateinit var binding: OverlineBinding
            private set

        override fun bindView(itemView: View) {
            binding = OverlineBinding.bind(itemView)
        }
    }
}