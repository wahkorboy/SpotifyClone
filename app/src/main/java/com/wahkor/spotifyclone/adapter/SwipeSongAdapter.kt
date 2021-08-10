package com.wahkor.spotifyclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.wahkor.spotifyclone.R
import com.wahkor.spotifyclone.data.entities.Song
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class SwipeSongAdapter:BaseSongAdapter(R.layout.list_item){
    override val differ= AsyncListDiffer(this,diffCallback)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val song=songs[holder.adapterPosition]
        holder.itemView.apply {
            val text= song.title +  " - " + song.subtitle
            tvPrimary.text=text
            setOnClickListener{
                onItemClickListener?.let { listener ->
                    listener(song)
                }
            }
        }
    }

}