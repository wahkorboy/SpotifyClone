package com.wahkor.spotifyclone.data.entities

import android.graphics.Bitmap

data class SongFilter(

    val mediaId:String?=null,
    val title:String?=null,
    val subtitle:String?=null,
    val songUrl:String?=null

) {
    fun isPass(song: Song):Boolean{
        this.mediaId?.let { if(it!=song.mediaId) return false }
        this.title?.let { if(it!=song.title) return false }
        this.subtitle?.let { if(it!=song.subtitle) return false }
        this.songUrl?.let { if(it!=song.songUrl) return false }
        return true
    }
}