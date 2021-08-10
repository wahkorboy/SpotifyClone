package com.wahkor.spotifyclone.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.wahkor.spotifyclone.R
import com.wahkor.spotifyclone.data.entities.Song

class AlbumArt {
    fun getAlbumArt(context: Context, path:String): Bitmap {

        val mmr= MediaMetadataRetriever()
        mmr.setDataSource(path)
        val data = mmr.embeddedPicture
        data?.let {
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        }
        return ResourcesCompat.getDrawable(context.resources,
            R.drawable.ic_music,null)!!.toBitmap()
        //return context.resources.getDrawable(R.drawable.ic_baseline_music_note_24).toBitmap()
    }

   fun getImage(song: Song): Bitmap? {

        val mmr= MediaMetadataRetriever()
        mmr.setDataSource(song.songUrl)
        val data = mmr.embeddedPicture
        data?.let {
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        }
        return null
    }
    fun saveImageToStorage(context: Context, song: Song):String?{
        val pic=getImage(song)
        pic?.let { image ->
                val filename="id_${song.mediaId}_art.png"
            val fos=context.openFileOutput(filename,MODE_PRIVATE)
                image.compress(Bitmap.CompressFormat.PNG,100,fos)
                fos.close()
            return filename
        }?:return null
    }

    fun loadImageFromStorage(context: Context, song: Song):Bitmap?{
        val files=context.filesDir.listFiles()
        files.find{ it.canRead() && it.isFile && it.name.equals("id_${song.mediaId}_art.png") }?.let {
            val bytes=it.readBytes()
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        return null
    }
}