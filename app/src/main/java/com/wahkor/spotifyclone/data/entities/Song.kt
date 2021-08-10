package com.wahkor.spotifyclone.data.entities

import android.graphics.Bitmap

data class Song(
    val mediaId:String="",
    val title:String="",
    val subtitle:String="",
    val songUrl:String="",
    val imageUrl:String="",
    var albumArt:Bitmap?=null
)