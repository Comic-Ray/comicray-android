package com.comicreader.comicray.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.comicreader.comicray.databinding.ItemComicBinding
import com.comicreader.comicray.utils.load

class CustomAdapter(
    val listOfComics : List<ComicDetails>
) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

//    val differCallback = object : DiffUtil.ItemCallback<CommonData>() {
//        override fun areItemsTheSame(oldItem: CommonData, newItem: CommonData): Boolean {
//            return oldItem.title == newItem.title
//        }
//
//        override fun areContentsTheSame(oldItem: CommonData, newItem: CommonData): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    val differ = AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            ItemComicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentComic = listOfComics[position]
        holder.bind(currentComic)
    }

    override fun getItemCount(): Int {
        return listOfComics.size
    }


    inner class CustomViewHolder(private val binding: ItemComicBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(currentComic : ComicDetails){
                binding.setImage(currentComic.imageUrl)
                binding.txtView.text = currentComic.title

            }
    }

    private fun ItemComicBinding.setImage(imageUrl : String){
        imgView.load(
            uri = imageUrl,
            onSuccess = { bmp ->
                imgView.setImageBitmap(bmp)
            }
        )
    }
}