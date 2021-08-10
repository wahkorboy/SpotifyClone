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

abstract class BaseSongAdapter(
    private val layoutId:Int
):RecyclerView.Adapter<BaseSongAdapter.VH>() {

    class VH(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(layoutId,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    protected var onItemClickListener:((Song) -> Unit)?=null
    fun setItemClickListener(listener:(Song) -> Unit){
        onItemClickListener=listener
    }
    protected val diffCallback =object: DiffUtil.ItemCallback<Song>(){
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId==newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    protected abstract val  differ:AsyncListDiffer<Song>

    var songs:List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)
}