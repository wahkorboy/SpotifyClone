package com.wahkor.spotifyclone.data.entities

import android.graphics.Bitmap

data class Song(
    val mediaId:String,
    val title:String,
    val subtitle:String,
    val songUrl:String,
    val album:String,
    var albumArt:Bitmap?=null
){
    override fun equals(other: Any?): Boolean =    this.mediaId == (other as Song).mediaId
    val path:String
    get() = songUrl.substringBeforeLast("/")
}