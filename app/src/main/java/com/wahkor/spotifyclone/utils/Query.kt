package com.wahkor.spotifyclone.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.wahkor.spotifyclone.data.entities.Song

class Query {
    companion object{
        val storageMedia=ArrayList<Song>()
    }

    private var imageUrl:String=""
    fun getTracks(context: Context){
        if(imageUrl.isBlank()){
        getImage(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }
        getAudioFile(context, MediaStore.Audio.Media.INTERNAL_CONTENT_URI,true)
        getAudioFile(context,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,false)
        getImage(context,MediaStore.Images.Media.INTERNAL_CONTENT_URI)

    }
    private fun getImage(context: Context,uri: Uri){
            val contentResolver=context.contentResolver
            val cursor=contentResolver.query(uri,null,null,null,null)

            cursor?.let{
                while(cursor.moveToNext()){
                    val path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    //println("path= $path")
                    if(path.substring(0,8)=="/storage"){
                        imageUrl=path
                        return
                    }
                }
            }
            cursor?.close()
        }
    private fun getAudioFile(context: Context, uri: Uri, isInternal:Boolean){
        val contentResolver=context.contentResolver
        val cursor=contentResolver.query(uri,null,null,null,null)

        cursor?.let{
            while(cursor.moveToNext()){
                val path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM))
                val artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST))
                val duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                //println("path= $path")
                if(path.substring(0,8)=="/storage"){
                    storageMedia.add(
                        Song(
                            mediaId = storageMedia.size.toString(),
                            title,
                            artist,
                            path,
                            imageUrl
                        )
                    )
                }
            }
        }
        cursor?.close()
    }
}