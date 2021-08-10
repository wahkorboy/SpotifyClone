package com.wahkor.spotifyclone.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.wahkor.spotifyclone.R
import com.wahkor.spotifyclone.utils.Query.Companion.storageMedia
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide:RequestManager
):BaseSongAdapter(R.layout.list_item){
    override val differ= AsyncListDiffer(this,diffCallback)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val song=songs[holder.adapterPosition]
        holder.itemView.apply {
            tvPrimary.text=song.title
            tvSecondary.text=song.subtitle
            val freshSong=storageMedia.find{ it.mediaId == song.mediaId }
            freshSong?.albumArt?.let { pic ->
                glide.load(pic).into(ivItemImage)
            } ?:run {
                glide.load(R.drawable.ic_music).into(ivItemImage)
            }

            setOnClickListener{
                onItemClickListener?.let { listener ->
                    listener(song)
                }
            }
        }
    }

}