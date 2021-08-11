package com.wahkor.spotifyclone.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.wahkor.spotifyclone.data.entities.Song
private const val TAG="Query class"
class Query {
    companion object{
        var requestInitialing: Boolean = true
        private set
        val storageMedia=ArrayList<Song>()
    }

    private var imageUrl=""

    fun reloadData(context: Context){
        //update storageMedia
        requestInitialing=true
        getTracks(context)
    }
    fun getTracks(context: Context){
        if (requestInitialing){
            getImage(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getAudioFile(context, MediaStore.Audio.Media.INTERNAL_CONTENT_URI)
            getAudioFile(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        }
        requestInitialing=false

    }
    @SuppressLint("Range")
    private fun getImage(context: Context, uri: Uri){
            val contentResolver=context.contentResolver
            val cursor=contentResolver.query(uri,null,null,null,null)

            cursor?.let{
                while(cursor.moveToNext()){
                    val path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    //println("path= $path")
                    if(path.contains("ic_music_note")){
                        imageUrl=path
                        return@let
                    }
                }
            }
            cursor?.close()
        }
    @SuppressLint("Range")
    private fun getAudioFile(context: Context, uri: Uri){
        val contentResolver=context.contentResolver
        val cursor=contentResolver.query(uri,null,null,null,null)

        cursor?.let{
            while(cursor.moveToNext()){
                val path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA ) )
                val title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM))
                val artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST))
                if(path.substring(0,8)=="/storage"){
                    Log.e(TAG, "getAudioFile: $path", )
                    storageMedia.add(
                        Song(
                            mediaId = storageMedia.size.toString(),
                            title,
                            artist,
                            path,
                            album
                        )
                    )
                }
            }
        }
        cursor?.close()
    }
}