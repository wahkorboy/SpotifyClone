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

class SongAdapter @Inject constructor(
    private val glide:RequestManager
):RecyclerView.Adapter<SongAdapter.VH>() {

    class VH(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val song=songs[holder.adapterPosition]
        holder.itemView.apply {
            tvPrimary.text=song.title
            tvSecondary.text=song.subtitle
            glide.load(song.imageUrl).into(ivItemImage)
            setOnClickListener{
                onItemClickListener?.let { listener ->
                    listener(song)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    private var onItemClickListener:((Song) -> Unit)?=null
    fun setOnItemClickListener(listener:(Song) -> Unit){
        onItemClickListener=listener
    }
    private val diffCallback =object:DiffUtil.ItemCallback<Song>(){
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId==newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this,diffCallback)

    var songs:List<Song>
    get() = differ.currentList
    set(value) = differ.submitList(value)
}